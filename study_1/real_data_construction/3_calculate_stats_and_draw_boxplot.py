import csv
import os
import sys
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd


def get_stats_and_draw_boxplot(number_list, file_name):
    color = {
        "boxes": "DarkGreen",
        "whiskers": "DarkOrange",
        "medians": "Red",
        "caps": "Gray",
    }

    file_name = file_name.replace(".csv", "")
    if file_name.endswith("_caus_man_ob"):
        file_name = file_name.replace("_caus_man_ob", "")

    print(f"Statistics of {file_name}:")
    print(f"========================================")
    print(f"Mean: {np.mean(number_list)}")
    print(f"Median: {np.median(number_list)}")
    print(f"Max: {np.max(number_list)}")
    print(f"Min: {np.min(number_list)}")
    print(f"Total: {np.sum(number_list)}")
    print(f"\n\n")

    # Set the figure size.
    plt.rcParams["figure.figsize"] = [10, 7]
    plt.rcParams["figure.autolayout"] = True
    plt.rcParams["boxplot.meanline"] = True
    plt.rcParams["boxplot.showmeans"] = True

    # Pandas dataframe.
    n_query_data = pd.DataFrame({f"{file_name}": number_list})

    # Plot the dataframe.
    n_query_data[[f'{file_name}']].plot(kind='box', color=color, legend=True)

    # Display the plot.
    plt.show()


def get_query_stats(csv_file_path):
    print(f"Processing {csv_file_path}...")
    file_name = csv_file_path.split("/")[-1]
    df = pd.read_csv(csv_file_path, delimiter=';')
    n_tasks = df["Task-No."].count()
    n_title_ob = df["Title-OB?"].sum()
    n_non_title_ob = n_tasks - n_title_ob
    n_hard_ob = (df["OB-Category"] == 'Hard').sum()
    n_easy_ob = (df["OB-Category"] == 'Easy').sum()
    if file_name.startswith("cr"):
        avg_gt = df["#-Component-in-GT"].mean()
        median_gt = df["#-Component-in-GT"].median()
        avg_corpus = df["#-Component-in-Corpus"].mean()
        median_corpus = df["#-Component-in-Corpus"].median()

    if file_name.startswith("sr"):
        avg_gt = df["#-Screen-in-GT"].mean()
        median_gt = df["#-Screen-in-GT"].median()
        avg_corpus = df["#-Screen-in-Corpus"].mean()
        median_corpus = df["#-Screen-in-Corpus"].median()

    print(f"#Retrieval Tasks: {n_tasks}")
    print(f"#Ret. Tasks w Title Query: {n_title_ob}")
    print(f"#Ret. Tasks w Non-Title Query: {n_non_title_ob}")
    print(f"#Ret. Tasks w Hard Query: {n_hard_ob}")
    print(f"#Ret. Tasks w Easy Query: {n_easy_ob}")
    print(f"Avg. of #GT: {avg_gt}")
    print(f"Median of #GT: {median_gt}")
    print(f"Avg. of #Corpus: {avg_corpus}")
    print(f"Median of #Corpus: {median_corpus}")
    print(f"---------------------------")

    bug_type_df = df.groupby("Bug-Type")
    print(bug_type_df["Bug-Type"].count())
    print(f"Total number of tasks: {n_tasks}")
    ob_category_df = df.groupby("OB-Category")
    print(ob_category_df["OB-Category"].count())
    ob_rating_df = df.groupby("OB-Rating")
    print(ob_rating_df["OB-Rating"].count())

    # get # of OB across OB-Category and OB-Rating
    df_2 = df.groupby(["OB-Rating", "Title-OB?"]).agg({"Task-No.": "count"})
    df_2.to_csv("stats.csv", sep=';', mode='w')


if __name__ == "__main__":
    screen_info_folder_path = './real_data/dataset_info/screen_info'
    component_info_folder_path = './real_data/dataset_info/component_info'
    query_info_folder_path = './real_data/dataset_info/query_info'
    caus_man_query_info_file_path = 'real_data/dataset_info/query_info/sl_query_details.csv'

    # Get statistics of the query details
    print("Statistics of the query details:")
    query_info_files = os.listdir(query_info_folder_path)
    query_info_files.sort()
    for csv_file in query_info_files:
        # Ignore the files that are not CSV files
        if not csv_file.endswith(".csv"):
            continue
        if not csv_file.__contains__("query_details"):
            continue
        csv_file_path = os.path.join(query_info_folder_path, csv_file)
        print(csv_file)
        get_query_stats(csv_file_path)

    # Get statistics of the number of screens
    print("Statistics of the number of screens per Bug:")
    for csv_file in os.listdir(screen_info_folder_path):
        # Ignore the files that are not CSV files
        if not csv_file.endswith(".csv"):
            continue
        csv_file_path = os.path.join(screen_info_folder_path, csv_file)
        df = pd.read_csv(csv_file_path, delimiter=';')
        n_screen_list = df['# of Screens'].values
        get_stats_and_draw_boxplot(n_screen_list, csv_file)

    # Get statistics of the number of components
    print("Statistics of the number of components per Screen:")
    for csv_file in os.listdir(component_info_folder_path):
        # Ignore the files that are not CSV files
        if not csv_file.endswith(".csv"):
            continue
        csv_file_path = os.path.join(component_info_folder_path, csv_file)
        df = pd.read_csv(csv_file_path, delimiter=';')
        n_component_list = df['# of Components'].values
        get_stats_and_draw_boxplot(n_component_list, csv_file)

    # Get statistics of the number of queries per Bug and per Screen
    print("Statistics of the number of queries per Bug:")
    for csv_file in os.listdir(query_info_folder_path):
        # Ignore the files that are not CSV files
        if not csv_file.endswith(".csv"):
            continue
        if csv_file.__contains__("query_details"):
            continue
        csv_file_path = os.path.join(query_info_folder_path, csv_file)
        df = pd.read_csv(csv_file_path, delimiter=';')
        n_query_list = df['# of Total OBs'].values
        get_stats_and_draw_boxplot(n_query_list, csv_file)
