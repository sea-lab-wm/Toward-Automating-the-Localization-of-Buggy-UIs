'''
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 * By Junnan Li
'''
import argparse
import os
import numpy as np
import random
import time
from glob import glob
from PIL import Image
import datetime
import json
import ruamel.yaml as yaml
from pathlib import Path
import csv

from torch.utils.data import DataLoader
from torchvision import transforms
from torchvision.transforms.functional import InterpolationMode

import torch
import torch.nn as nn
import torch.nn.functional as F
import torch.backends.cudnn as cudnn
import torch.distributed as dist
from torch.utils.data import DataLoader

from models.blip_retrieval import blip_retrieval
from utils import cosine_lr_schedule, RealOBQuery, calculate_metrics
from evaluation_metrics import reciprocal_rank, average_precision, hit_rate_at_k


@torch.no_grad()
def evaluation(model, ob_text, images, device, config):
    # test
    model.eval()
    print('Computing features for evaluation...')

    num_images = images.shape[0]

    text_input = model.tokenizer(ob_text, padding='max_length', truncation=True, max_length=35, return_tensors="pt").to(
        device)
    text_ids = text_input.input_ids
    text_atts = text_input.attention_mask
    text_output = model.text_encoder(text_input.input_ids, attention_mask=text_input.attention_mask,
                                     mode='text')  # shape = (1, seq_len, hidden_size)
    text_embed = F.normalize(model.text_proj(text_output.last_hidden_state[:, 0, :]))  # shape = (1, hidden_size)

    # images is a list of screens or components 
    images = images.to(device)  # shape = (batch_size, num_frm, 3, 224, 224)
    images_feat = model.visual_encoder(images)  # shape = (batch_size, num_frm, hidden_size)
    images_embed = model.vision_proj(images_feat[:, 0, :])  # shape = (batch_size, hidden_size)
    images_embed = F.normalize(images_embed, dim=-1)  # shape = (batch_size, hidden_size)

    sims_matrix = text_embed @ images_embed.t()  # shape = (1, batch_size)
    score_matrix_t2i = torch.full((1, num_images), -100.0).to(device)  # shape = (1, batch_size)

    topk_sim, topk_idx = sims_matrix[0].topk(k=num_images, dim=0)
    print("topk_sim: ", topk_sim)
    print("topk_idx: ", topk_idx)
    # topk_sim is a list of topk similarity scores, with order from highest to lowest
    # topk_idx is a list of topk indices

    encoder_output = images_feat[topk_idx].to(device)  # encoder_output is ranked by topk_idx
    encoder_att = torch.ones(encoder_output.size()[:-1], dtype=torch.long).to(device)
    output = model.text_encoder(text_ids.repeat(num_images, 1),
                                attention_mask=text_atts.repeat(num_images, 1),
                                encoder_hidden_states=encoder_output,
                                encoder_attention_mask=encoder_att,
                                return_dict=True,
                                )
    score = model.itm_head(output.last_hidden_state[:, 0, :])[:, 1]
    print("score: ", score)
    score_matrix_t2i[0, topk_idx] = score + topk_sim  # shape = (1, batch_size)

    return score_matrix_t2i[0].cpu().numpy(), topk_idx.cpu().numpy()


def get_image_ranking(image_folder_path, query_list, model, device):
    model = model.to(device)
    image_paths = glob(image_folder_path)
    image_paths.sort()

    # Initialize an image dictionary
    images_dict = {}
    # Add all the images in a dictionary where key is the image ID and value is the preprocessed image
    for i in range(len(image_paths)):
        path = image_paths[i]
        image_id = path.split("/")[-1].replace(".jpg", "")
        image_id = image_id.replace(".png", "")
        images_dict[i] = image_id
    # print(images_dict)

    all_query_result = []
    all_query_scores = []
    # Iterate over all the OBs in the OB query list

    normalize = transforms.Normalize((0.48145466, 0.4578275, 0.40821073), (0.26862954, 0.26130258, 0.27577711))
    transform_test = transforms.Compose([
        transforms.Resize((config['image_size'], config['image_size']), interpolation=InterpolationMode.BICUBIC),
        transforms.ToTensor(),
        normalize,
    ])

    # get image features
    images = []
    for path in image_paths:
        image = Image.open(path).convert('RGB')
        image = transform_test(image)
        images.append(image)
    images = torch.stack(images, dim=0)  # shape = (batch_size, 3, 224, 224)

    for query in query_list:
        # get text
        ob_text = query.ob_text

        evaluation_scores, topk_idx = evaluation(model, ob_text, images, device, config)

        idx_to_score = {}
        for idx in range(len(evaluation_scores)):
            idx_to_score[idx] = evaluation_scores[idx]

        # Sort the scores in descending order
        sorted_scores = sorted(idx_to_score.items(), key=lambda x: x[1], reverse=True)
        sorted_keys = [t[0] for t in sorted_scores]

        ranked_screens = [images_dict[i] for i in sorted_keys]

        query_result = []
        # Create the result list of an OB
        for screen in ranked_screens:
            # print(query.ground_truth)
            if screen in query.ground_truth:
                # print(query.ground_truth)
                query_result.append(1)
            else:
                query_result.append(0)

        # Add the result of each OB to the application result list
        all_query_result.append(query_result)
        all_query_scores.append(evaluation_scores)
    # print(f'All Query Result: {all_query_result}')
    # print(f'All Query Ranked Screens: {all_query_ranked_screens}')
    # Return the results of an application as a list of lists
    return all_query_result, all_query_scores


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--config', default='./configs/retrieval_msrvtt.yaml')

    parser.add_argument('--device', default='cuda')
    parser.add_argument('exp_name', help='Name of the experiment')
    parser.add_argument('ob_file_path', help='Path of the JSON file that contains the OBs')
    parser.add_argument('screen_folder_path', help='Path of the folder that contains the screen images')
    parser.add_argument('result_folder_path', help='Path of the CSV file that contains the results of all the OBs')

    args = parser.parse_args()

    config = yaml.load(open(args.config, 'r'), Loader=yaml.Loader)

    Path(args.result_folder_path).mkdir(parents=True, exist_ok=True)

    yaml.dump(config, open(os.path.join(args.output_dir, 'config.yaml'), 'w'))

    device = torch.device("cuda:0") if torch.cuda.is_available() else torch.device("cpu")
    # device = torch.device(args.device)

    #### Model ####
    print("Creating model")
    model = blip_retrieval(pretrained=config['pretrained'],
                           image_size=config['image_size'],
                           vit=config['vit'],
                           #vit_grad_ckpt=config['vit_grad_ckpt'], 
                           #vit_ckpt_layer=config['vit_ckpt_layer'], 
                           #queue_size=config['queue_size'], 
                           #negative_all_rank=config['negative_all_rank']
                           )

    model = model.to(device)

    # Add arguments to the parse

    all_ob_results_file_path = os.path.join(args.result_folder_path, 'SL', 'CLIP_results.csv')
    all_ob_results_with_details_file_path = os.path.join(args.result_folder_path, 'SL', 'CLIP_results_with_details.csv')

    # Create CSV file for writing the results of all OBs
    with open(all_ob_results_file_path, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile, delimiter=';')
        writer.writerow(['# of Queries', 'MRR', 'MAP', 'h@1', 'h@2', 'h@3', 'h@4', 'h@5', 'h@6', 'h@7', 'h@8', 'h@9',
                         'h@10'])

    # Create CSV file for writing the results of all OBs with details
    with open(all_ob_results_with_details_file_path, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile, delimiter=';')
        writer.writerow(['Bug-ID',
                         'OB-ID',
                         'OB-in-Title?',
                         'OB-Type-SC',
                         'OB-Rating',
                         # 'Ground-Truth', 
                         # 'Ranked-Documents', 
                         'First-Rank',
                         'Reciprocal-Rank',
                         'Average-Precision',
                         'h@1', 'h@2', 'h@3', 'h@4', 'h@5', 'h@6', 'h@7', 'h@8', 'h@9', 'h@10'])

    result_of_all_obs = []
    test_bug_counter = 0
    obs_with_no_ground_truth_path_list = []
    print(args.ob_file_path)
    with open(args.ob_file_path, 'r') as json_file:
        data = json.load(json_file)

        for bug_id, bug_details in data.items():
            print(f'Bug-ID: {bug_id}')
            ob_query_list = []
            bug_screens_path = os.path.join(args.screen_folder_path, bug_id, "*.png")
            for ob_id, ob_details in bug_details.items():
                screen_dict_list = ob_details["screens"]
                ground_truth = []
                for screen_dict in screen_dict_list:
                    ground_truth.append(screen_dict["screen_id"])
                if ground_truth.__len__() == 0:
                    continue
                ob_query = RealOBQuery(bug_id, ob_id, ob_details["ob_in_title"], ob_details["ob_type_sc"],
                                       ob_details["ob_rating"], ob_details["ob_text"], ground_truth)
                # Create OB query list by adding all the OB queries
                ob_query_list.append(ob_query)

            result_of_obs_in_one_bug, scores_of_obs_in_one_bug = \
                get_image_ranking(bug_screens_path, ob_query_list, model, device)

            for j in range(ob_query_list.__len__()):
                with open(all_ob_results_with_details_file_path, 'a', newline='') as csvfile:
                    writer = csv.writer(csvfile, delimiter=';')
                    writer.writerow([ob_query_list[j].bug_id,
                                     ob_query_list[j].ob_id,
                                     ob_query_list[j].ob_in_title,
                                     ob_query_list[j].ob_type_sc,
                                     ob_query_list[j].ob_rating,
                                     # ob_query_list[j].ground_truth,
                                     # scores_of_obs_in_one_bug[j],
                                     result_of_obs_in_one_bug[j].index(1) + 1,
                                     reciprocal_rank(result_of_obs_in_one_bug[j]),
                                     average_precision(result_of_obs_in_one_bug[j]),
                                     hit_rate_at_k(result_of_obs_in_one_bug[j], 1),
                                     hit_rate_at_k(result_of_obs_in_one_bug[j], 2),
                                     hit_rate_at_k(result_of_obs_in_one_bug[j], 3),
                                     hit_rate_at_k(result_of_obs_in_one_bug[j], 4),
                                     hit_rate_at_k(result_of_obs_in_one_bug[j], 5),
                                     hit_rate_at_k(result_of_obs_in_one_bug[j], 6),
                                     hit_rate_at_k(result_of_obs_in_one_bug[j], 7),
                                     hit_rate_at_k(result_of_obs_in_one_bug[j], 8),
                                     hit_rate_at_k(result_of_obs_in_one_bug[j], 9),
                                     hit_rate_at_k(result_of_obs_in_one_bug[j], 10)])

            # Add the results of application i to the result of all OBs
            for result in result_of_obs_in_one_bug:
                result_of_all_obs.append(result)

            test_bug_counter += 1
            print(f'# of bugs completed: {test_bug_counter}\n')

        # Calculate metrics for all OBs
        mrr, map, hit_1, hit_2, hit_3, hit_4, hit_5, hit_6, hit_7, hit_8, hit_9, hit_10 = \
            calculate_metrics(result_of_all_obs)

        # Write results of all OBs to the CSV file
        with open(all_ob_results_file_path, 'a', newline='') as csvfile:
            writer = csv.writer(csvfile, delimiter=';')
            writer.writerow(
                [result_of_all_obs.__len__(), mrr, map, hit_1, hit_2, hit_3, hit_4, hit_5, hit_6, hit_7, hit_8,
                 hit_9, hit_10])
