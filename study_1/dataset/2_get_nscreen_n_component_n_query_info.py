import csv
import json
import os
import re

bug_id_list = [2, 8, 10, 11, 18, 19, 21, 22, 44, 45, 53, 54, 55, 56, 71, 76, 84, 87, 91, 92, 106, 110, 117, 128, 129,
               130, 135, 158, 159, 160, 162, 168, 191, 192, 193, 199, 200, 201, 206, 209, 248, 256, 271, 275, 1028,
               1033, 1066, 1067, 1073, 1089, 1096, 1130, 1146, 1147, 1150, 1151, 1197, 1198, 1202, 1205, 1207, 1213,
               1214, 1215, 1222, 1223, 1224, 1226, 1228, 1299, 1389, 1399, 1402, 1403, 1406, 1425, 1428, 1430, 1441,
               1445, 1446, 1481, 1563, 1568, 1640, 1641, 1645]


def get_n_queries_per_bug_and_query_details(screen_components_folder_path, query_file_path, n_queries_per_bug_file_path, sl_query_details_file_path, cl_query_details_file_path):
    # Create a CSV file for storing the number of queries per app
    with open(n_queries_per_bug_file_path, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile, delimiter=';')
        writer.writerow(['Bug-Name', '# of Total OBs'])

    # Create a CSV file for storing SR query details
    with open(sl_query_details_file_path, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile, delimiter=';')
        writer.writerow(
            ['Task-No.', 'Bug-ID', 'OB-ID', 'Title-OB?', 'Bug-Type', 'OB-Category', 'OB-Rating', 'OB-Text-Length',
             '#-Screen-in-GT', '#-Screen-in-Corpus'])

    # Create a CSV file for storing CR query details
    with open(cl_query_details_file_path, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile, delimiter=';')
        writer.writerow(
            ['Task-No.', 'Bug-ID', 'OB-ID', 'Screen-ID', 'Title-OB?', 'Bug-Type', 'OB-Category', 'OB-Rating', 'OB-Text-Length',
             '#-Component-in-GT', '#-Component-in-Corpus'])

    sr_query_number = 0
    cr_query_number = 0
    with open(query_file_path, 'r') as json_file:
        data = json.load(json_file)
        # print(data)
        for bug_id, bug_details in data.items():
            print(f'Bug-ID: {bug_id}')
            screen_component_file_path = os.path.join(screen_components_folder_path, bug_id + ".json")

            # Read the JSON file
            with open(screen_component_file_path, 'r') as json_file:
                # Load the JSON file
                screen_components_data = json.load(json_file)
                # Get the screen IDs
                n_screen_in_corpus = len(screen_components_data.keys()) - 1  # NOTE: "app-name" is not a screen ID

            n_ob = 0

            # bug_screens_path = os.path.join(screen_folder_path, bug_id, "*.png")
            for ob_id, ob_details in bug_details.items():
                n_ob += 1

                sr_ob = 0
                cr_ob = 0
                screen_dict_list = ob_details["screens"]
                if len(screen_dict_list) == 0:
                    continue
                sr_query_number += 1
                n_screen_ground_truth = len(screen_dict_list)
                #n_component_ground_truth = 0
                for screen_dict in screen_dict_list:
                    if len(screen_dict["components"]) == 0:
                        continue
                    cr_query_number += 1
                    # n_screen_ground_truth += 1
                    n_component_ground_truth = len(screen_dict["components"])
                    n_component_in_gt = len(screen_components_data[screen_dict["screen_id"]])

                    # Write the query details of CR queries in the CSV file
                    with open(cl_query_details_file_path, 'a', newline='') as csvfile:
                        writer = csv.writer(csvfile, delimiter=';')
                        writer.writerow(
                            [cr_query_number, bug_id, ob_id, screen_dict["screen_id"], ob_details["ob_in_title"], ob_details["bug_type"], ob_details["ob_category"], ob_details["ob_rating"],
                             len(re.findall(r'\w+', ob_details["ob_text"])), n_component_ground_truth, n_component_in_gt])

                # Write the query details of SR queries in the CSV file
                with open(sl_query_details_file_path, 'a', newline='') as csvfile:
                    writer = csv.writer(csvfile, delimiter=';')
                    writer.writerow(
                        [sr_query_number, bug_id, ob_id, ob_details["ob_in_title"], ob_details["bug_type"], ob_details["ob_category"], ob_details["ob_rating"],
                         len(re.findall(r'\w+', ob_details["ob_text"])), n_screen_ground_truth, n_screen_in_corpus])

            # Write the number of queries per app in the CSV file
            with open(n_queries_per_bug_file_path, 'a', newline='') as csvfile:
                writer = csv.writer(csvfile, delimiter=';')
                writer.writerow([bug_id, n_ob])
    print('Reading and processing all apps completed.\n\n')


def get_n_screens_per_bug_and_n_components_per_screen(screen_components_path, n_screens_per_bug_file_path,
                                                      n_components_per_screen_file_path):
    # Create CSV file for storing the number of screens per app
    with open(n_screens_per_bug_file_path, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile, delimiter=';')
        writer.writerow(['Bug-ID', '# of Screens'])

    # Create CSV file for storing the number of components per screen
    with open(n_components_per_screen_file_path, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile, delimiter=';')
        writer.writerow(['Bud-ID', 'Screen-ID', '# of Components'])

    # Iterate over all bugs
    for file in os.listdir(screen_components_path):
        # Get the name of the bug
        bug_name = file.replace('.json', '')
        bug_id = int(bug_name.replace('Bug', ''))
        if bug_id not in bug_id_list:
            continue
        # Create JSON file path for accessing a particular JSON file
        json_file_path = os.path.join(screen_components_path, bug_name + '.json')

        # Read the JSON file
        with open(json_file_path, 'r') as json_file:
            # Load the JSON file
            data = json.load(json_file)
            # Get the screen IDs
            screen_ids = data.keys()

            screen_per_app_counter = len(screen_ids) - 1  # NOTE: "app-name" is not a screen ID
            # Iterate over all screen IDs
            for screen_id in screen_ids:
                # Ignore the "app-name" key
                if isinstance(data[screen_id], list):
                    # Write the number of components per screen in the CSV file
                    with open(n_components_per_screen_file_path, 'a', newline='') as csvfile:
                        writer = csv.writer(csvfile, delimiter=';')
                        writer.writerow([bug_name, screen_id, len(data[screen_id])])

            # Write the number of screens per app in the CSV file
            with open(n_screens_per_bug_file_path, 'a', newline='') as csvfile:
                writer = csv.writer(csvfile, delimiter=';')
                writer.writerow([bug_name, screen_per_app_counter])
    print('Reading and processing all apps completed.\n\n')


if __name__ == "__main__":
    print(len(bug_id_list))
    screen_components_folder_path = './real_data/screen_components'
    crash_queries_file_path = 'real_data/ob/obs.json'

    n_screen_per_bug_file_path = './real_data/dataset_info/screen_info/screens_per_bug.csv'
    n_component_per_screen_file_path = './real_data/dataset_info/component_info/components_per_screen.csv'

    n_queries_per_bug_file_path = './real_data/dataset_info/query_info/queries_per_bug.csv'
    sl_query_details_file_path = './real_data/dataset_info/query_info/sl_query_details.csv'
    cl_query_details_file_path = './real_data/dataset_info/query_info/cl_query_details.csv'

    get_n_screens_per_bug_and_n_components_per_screen(screen_components_folder_path, n_screen_per_bug_file_path,
                                                      n_component_per_screen_file_path)
    get_n_queries_per_bug_and_query_details(screen_components_folder_path, crash_queries_file_path, n_queries_per_bug_file_path, sl_query_details_file_path, cl_query_details_file_path)
