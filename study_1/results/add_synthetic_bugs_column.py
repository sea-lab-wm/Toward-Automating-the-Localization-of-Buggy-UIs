import os
import sys

import pandas as pd


synthetic_bug_types = ["Component is not displayed (correctly)", "Data is not listed in the right sorting/order", "GUI refresh issue", "Showing data in wrong format", "Text in Component disappears", "Textual error in screen", "Unclickable component", "Undesirable component", "Uneditable component", "Unsupported theme in component", "Wrong information is displayed", "Wrong theme in screen"]
non_synthetic_bug_types = ["App crashes", "Component disappear", "Data is not displayed", "Text in a component is misaligned", "Undesirable  behavior "]


def add_synthetic_bugs_column_with_detailed_results(result_file_path):
    detailed_result_df = pd.read_csv(result_file_path, sep=";")
    if 'Bug-Category' in detailed_result_df.columns:
        detailed_result_df.drop(columns=['Bug-Category'], inplace=True)
    bug_category = []
    bug_type_column = detailed_result_df['Bug-Type']

    for bug_type in bug_type_column:
        if bug_type in synthetic_bug_types:
            bug_category.append('Synthetic Bug')
        elif bug_type in non_synthetic_bug_types:
            bug_category.append('Non-Synthetic Bug')
        else:
            bug_category.append('Unknown')

    detailed_result_df.insert(loc=4, column='Bug-Category', value=bug_category)
    detailed_result_df.to_csv(result_file_path, sep=";", index=False)


def call_function_for_all_exp(result_folder_paths):
    for i in range(len(result_folder_paths)):
        result_folder_path = result_folder_paths[i]

        for result_file in os.listdir(result_folder_path):
            if result_file.endswith('with_details.csv'):
                result_file_path = os.path.join(result_folder_path, result_file)
                print(f"Processing {result_file_path}")
                add_synthetic_bugs_column_with_detailed_results(result_file_path)


if __name__ == '__main__':
    sr_result_folder_paths = ['./study_1/results/SL']
    cr_result_folder_paths = ['./study_1/results/CL']

    call_function_for_all_exp(sr_result_folder_paths)
    call_function_for_all_exp(cr_result_folder_paths)
