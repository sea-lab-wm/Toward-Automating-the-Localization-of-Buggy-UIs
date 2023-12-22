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
from PIL import Image
from transformers import (
    CLIPModel, CLIPProcessor,
    AdamW, get_scheduler
)
from models.blip_retrieval import blip_retrieval
from utils import cosine_lr_schedule, RealOBQuery, calculate_metrics
from evaluation_metrics import reciprocal_rank, average_precision, hit_rate_at_k
from models.blip import load_checkpoint


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

    # Iterate over all the OBs in the OB query list

    
    # get image features
    images = []
    for path in image_paths:
        image = Image.open(path).convert('RGB')    
        image = image.resize((224, 224))  
        images.append(image)


    for query in query_list:
        # get text

        inputs = processor(text=[query.ob_text],
                                images=images,
                                return_tensors="pt",
                                padding="max_length",
                                max_length=64,
                                truncation=True)
        inputs = {k: v.to(device) for k, v in inputs.items()}
        outputs = model(**inputs)
        logits_per_text = outputs.logits_per_text # shape (1, # of images)
        idx_to_score = {}
        for idx in range(logits_per_text.shape[1]):
            idx_to_score[idx] = logits_per_text[0][idx].cpu().detach().numpy()
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
    # print(f'All Query Result: {all_query_result}')
    # print(f'All Query Ranked Screens: {all_query_ranked_screens}')
    # Return the results of an application as a list of lists
    return all_query_result



if __name__ == '__main__':

    parser = argparse.ArgumentParser()     
    parser.add_argument('--config_file', default='./configs/retrieval_clip.yaml')
    parser.add_argument('exp_name', help='Name of the experiment')
    parser.add_argument('ob_file_path', help='Path of the JSON file that contains the OBs')
    parser.add_argument('component_images_folder_path', help='Path of the folder that contains the screen images')
    parser.add_argument('result_folder_path', help='Path of the CSV file that contains the results of all the OBs')
 
    args = parser.parse_args()

    config = yaml.load(open(args.config_file, 'r'), Loader=yaml.Loader)
    print(config)

    model = CLIPModel.from_pretrained("openai/clip-vit-base-patch32")
    checkpoint = torch.load(config['checkpoint_file'])
    model.load_state_dict(checkpoint)
   
    
    processor = CLIPProcessor.from_pretrained("openai/clip-vit-base-patch32")

    Path(args.result_folder_path).mkdir(parents=True, exist_ok=True)
        
    # yaml.dump(config, open(os.path.join(args.output_dir, 'config.yaml'), 'w'))    

    device = torch.device("cuda:1") if torch.cuda.is_available() else torch.device("cpu")
    model.to(device)

 

    # Add arguments to the parse

    all_ob_results_file_path = os.path.join(args.result_folder_path, args.exp_name + '.csv')
    all_ob_results_with_details_file_path = os.path.join(args.result_folder_path, args.exp_name +
                                                         '_with_details.csv')



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
                         'h@1', 'h@2', 'h@3', 'h@4', 'h@5', 'h@6', 'h@7', 'h@8','h@9', 'h@10'])

    result_of_all_obs = []
    test_bug_counter = 0
    print(args.ob_file_path)
    with open(args.ob_file_path, 'r') as json_file:
        data = json.load(json_file)
      
        for bug_id, bug_details in data.items():
            print(f'Bug-ID: {bug_id}')

            # bug_screens_path = os.path.join(component_images_folder_path, bug_id, "*.png")
            for ob_id, ob_details in bug_details.items():
                if ob_details["ob_type_sc"] == "S":
                    continue

                screen_dict_list = ob_details["screens"]

                
                for screen_dict in screen_dict_list:
                    ob_query_list = []
                    ground_truth = []
                    screen_id = screen_dict["screen_id"]
                    component_list = screen_dict["components"]
                    for component_id in component_list:
                        ground_truth.append(str(component_id))
                    if ground_truth.__len__() == 0:
                        continue
                    ob_query = RealOBQuery(bug_id, ob_id, ob_details["ob_in_title"], ob_details["ob_type_sc"], ob_details["ob_rating"], ob_details["ob_text"], ground_truth)
                    # Create OB query list by adding all the OB queries
                    ob_query_list.append(ob_query)
                    component_images_path = os.path.join(args.component_images_folder_path, bug_id, screen_id, "*.png")
                             
            
                    result_of_obs_in_one_bug= get_image_ranking(component_images_path, ob_query_list, model, device)
        

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


