import csv
import os
import json
import torch
from utils import RealOBQuery, get_documents_ranking, calculate_metrics, create_screen_documents, create_component_documents
from evaluation_metrics import reciprocal_rank, average_precision, hit_rate_at_k
import argparse
from sentence_transformers import SentenceTransformer


def perform_retrieval():
    results_of_n_obs, scores_of_n_obs = \
        get_documents_ranking(documents_list, ob_query_list, model, device)

    for j in range(ob_query_list.__len__()):
        with open(result_with_details_file_path, 'a', newline='') as csvfile:
            writer = csv.writer(csvfile, delimiter=';')
            writer.writerow([ob_query_list[j].bug_id, ob_query_list[j].ob_id, ob_query_list[j].ob_text, ob_query_list[j].ob_in_title,
                             ob_query_list[j].bug_type, ob_query_list[j].ob_category, ob_query_list[j].ob_rating,
                             ob_query_list[j].ground_truth, scores_of_n_obs[j],
                             results_of_n_obs[j].index(1) + 1,
                             reciprocal_rank(results_of_n_obs[j]),
                             average_precision(results_of_n_obs[j]),
                             hit_rate_at_k(results_of_n_obs[j], 1),
                             hit_rate_at_k(results_of_n_obs[j], 2),
                             hit_rate_at_k(results_of_n_obs[j], 3),
                             hit_rate_at_k(results_of_n_obs[j], 4),
                             hit_rate_at_k(results_of_n_obs[j], 5),
                             hit_rate_at_k(results_of_n_obs[j], 6),
                             hit_rate_at_k(results_of_n_obs[j], 7),
                             hit_rate_at_k(results_of_n_obs[j], 8),
                             hit_rate_at_k(results_of_n_obs[j], 9),
                             hit_rate_at_k(results_of_n_obs[j], 10)])
    return results_of_n_obs


if __name__ == "__main__":
    # Create an ArgumentParser object
    parser = argparse.ArgumentParser(description='Screen/Component Localization by SBERT with Real dataset')
    # Add arguments to the parser
    parser.add_argument('task_name', help='Name of the Task (SL/CL)')
    # Parse the command line arguments
    args = parser.parse_args()

    screen_components_folder_path = './study_1/real_data_construction/real_data/screen_components'
    # Query file paths
    ob_file_path = './study_1/real_data_construction/real_data/ob/obs.json'

    # Results file paths for writing the results
    # print(os.getcwd())
    result_file_path = f'./study_1/results/{args.task_name}/SBERT_results.csv'
    result_with_details_file_path = f'./study_1/results/{args.task_name}/SBERT_results_with_details.csv'

    # Set the device
    device = "cuda" if torch.cuda.is_available() else "cpu"
    print(f"Device: {device}")

    # Define the model by loading the pre-trained model
    model = SentenceTransformer('sentence-transformers/msmarco-distilbert-base-v3', device=device)
    model.eval()

    # Create CSV file for writing the results of all OBs
    with open(result_file_path, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile, delimiter=';')
        writer.writerow(['# of Queries', 'MRR', 'MAP', 'h@1', 'h@2', 'h@3', 'h@4', 'h@5', 'h@6', 'h@7', 'h@8', 'h@9',
                         'h@10'])

    # Create CSV file for writing the results of all OBs with details
    with open(result_with_details_file_path, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile, delimiter=';')
        writer.writerow(['Bug-ID', 'OB-ID', 'OB-Text', 'OB-in-Title?', 'Bug-Type', 'OB-Category', 'OB-Rating', 'Ground-Truth',
                         'Ranked-Documents', 'First-Rank', 'Reciprocal-Rank', 'Average-Precision', 'h@1', 'h@2', 'h@3',
                         'h@4', 'h@5', 'h@6', 'h@7', 'h@8', 'h@9', 'h@10'])

    result_of_all_obs = []
    test_bug_counter = 0
    obs_with_no_ground_truth_path_list = []

    with open(ob_file_path, 'r') as json_file:
        data = json.load(json_file)
        for bug_id, bug_details in data.items():
            print(f'Bug-ID: {bug_id}')
            ob_query_list = []
            bug_screen_components_path = os.path.join(screen_components_folder_path, bug_id + ".json")
            if args.task_name == 'SL':
                documents_list = create_screen_documents(bug_screen_components_path)

            for ob_id, ob_details in bug_details.items():
                screen_dict_list = ob_details["screens"]
                # print(f'# of screens: {len(screen_dict_list)}')
                if screen_dict_list.__len__() == 0:
                    continue

                if args.task_name == 'CL':
                    for screen_dict in screen_dict_list:
                        ob_query_list = []
                        ground_truth = []
                        screen_id = screen_dict["screen_id"]
                        component_list = screen_dict["components"]
                        for component_id in component_list:
                            ground_truth.append(str(component_id))
                        if ground_truth.__len__() == 0:
                            continue
                        # print("Ground Truth: ", ground_truth)
                        ob_query = RealOBQuery(bug_id, ob_id, ob_details["ob_in_title"], ob_details["bug_type"], ob_details["ob_category"], ob_details["ob_rating"], ob_details["ob_text"], ground_truth)
                        # Create OB query list by adding all the OB queries
                        ob_query_list.append(ob_query)
                        documents_list = create_component_documents(bug_screen_components_path, screen_id)
                        results_of_n_obs = perform_retrieval()

                        for result in results_of_n_obs:
                            result_of_all_obs.append(result)

                elif args.task_name == 'SL':
                    ground_truth = []
                    for screen_dict in screen_dict_list:
                        ground_truth.append(screen_dict["screen_id"])
                    if ground_truth.__len__() == 0:
                        continue

                    ob_query = RealOBQuery(bug_id, ob_id, ob_details["ob_in_title"], ob_details["bug_type"], ob_details["ob_category"], ob_details["ob_rating"], ob_details["ob_text"], ground_truth)
                    # Create OB query list by adding all the OB queries
                    ob_query_list.append(ob_query)

            if args.task_name == 'SL':
                results_of_n_obs = perform_retrieval()

                for result in results_of_n_obs:
                    result_of_all_obs.append(result)

            test_bug_counter += 1
            print(f'# of bugs completed: {test_bug_counter}\n')

        print(f'# of OBs: {result_of_all_obs.__len__()}')
        # Calculate metrics for all OBs
        mrr, map, hit_1, hit_2, hit_3, hit_4, hit_5, hit_6, hit_7, hit_8, hit_9, hit_10 = \
            calculate_metrics(result_of_all_obs)

        # Write results of all OBs to the CSV file
        with open(result_file_path, 'a', newline='') as csvfile:
            writer = csv.writer(csvfile, delimiter=';')
            writer.writerow(
                [result_of_all_obs.__len__(), mrr, map, hit_1, hit_2, hit_3, hit_4, hit_5, hit_6, hit_7, hit_8, hit_9,
                 hit_10])
