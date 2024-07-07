import pandas as pd
import os


def write_categorized_results(result_file_path, output_file_path):
    exp_name = result_file_path.split('/')[-1].replace('_all_obs_with_details.csv', '')
    df = pd.read_csv(result_file_path, delimiter=';')
    # print(df.columns)

    # Group by OB-Category, OB-Rating, OB-in-Title?
    if 'Can-Retrieve-Any-Document?' in df:
        group_by_category_rating_title = df.groupby(['OB-Category', 'OB-Rating', 'OB-in-Title?']).agg(
            {'OB-ID': 'count', 'Reciprocal-Rank': 'mean', 'Average-Precision': 'mean', 'h@1': 'mean', 'h@2': 'mean',
             'h@3': 'mean', 'h@4': 'mean', 'h@5': 'mean', 'h@6': 'mean', 'h@7': 'mean', 'h@8': 'mean', 'h@9': 'mean',
             'h@10': 'mean', 'Can-Retrieve-Any-Document?': 'sum'})
    else:
        group_by_category_rating_title = df.groupby(['OB-Category', 'OB-Rating', 'OB-in-Title?']).agg(
            {'OB-ID': 'count', 'Reciprocal-Rank': 'mean', 'Average-Precision': 'mean', 'h@1': 'mean', 'h@2': 'mean',
             'h@3': 'mean', 'h@4': 'mean', 'h@5': 'mean', 'h@6': 'mean', 'h@7': 'mean', 'h@8': 'mean', 'h@9': 'mean',
             'h@10': 'mean'})
        group_by_category_rating_title.insert(loc=13, column='Can-Retrieve-Any-Document?',
                                              value=df.groupby(['OB-Category', 'OB-Rating', 'OB-in-Title?']).agg(
                                                  {'OB-ID': 'count'}))

    group_by_category_rating_title.insert(loc=0, column='Bug-Category', value="All")
    group_by_category_rating_title.insert(loc=1, column='Experiment Name', value=exp_name)

    group_by_category_rating_title.rename(columns={'OB-ID': '# of Retrievals'}, inplace=True)
    group_by_category_rating_title.rename(columns={'Reciprocal-Rank': 'MRR'}, inplace=True)
    group_by_category_rating_title.rename(columns={'Average-Precision': 'MAP'}, inplace=True)
    group_by_category_rating_title.rename(columns={'Can-Retrieve-Any-Document?': '# of Success'}, inplace=True)

    avg_hits = (group_by_category_rating_title['h@1'] + group_by_category_rating_title['h@2'] + group_by_category_rating_title[
        'h@3'] + group_by_category_rating_title['h@4'] + group_by_category_rating_title['h@5'] + group_by_category_rating_title[
                    'h@6'] + group_by_category_rating_title['h@7'] + group_by_category_rating_title['h@8'] +
                group_by_category_rating_title['h@9'] + group_by_category_rating_title['h@10']) / 10
    group_by_category_rating_title.insert(loc=15, column='Avg. Hits', value=avg_hits)

    if '# of Success' in group_by_category_rating_title:
        n_failed_cases = group_by_category_rating_title['# of Retrievals'] - group_by_category_rating_title['# of Success']
        group_by_category_rating_title.insert(loc=17, column='# of Failure', value=n_failed_cases)

    # print(group_by_category_rating_title.columns)
    if not os.path.exists(output_file_path):
        group_by_category_rating_title.to_csv(output_file_path, sep=';', mode='w')
    else:
        group_by_category_rating_title.to_csv(output_file_path, sep=';', mode='a', header=False)


    # Group by OB-Category, OB-Rating
    if 'Can-Retrieve-Any-Document?' in df:
        group_by_category_rating = df.groupby(['OB-Category', 'OB-Rating']).agg(
            {'OB-ID': 'count', 'Reciprocal-Rank': 'mean', 'Average-Precision': 'mean', 'h@1': 'mean', 'h@2': 'mean',
             'h@3': 'mean', 'h@4': 'mean', 'h@5': 'mean', 'h@6': 'mean', 'h@7': 'mean', 'h@8': 'mean', 'h@9': 'mean',
             'h@10': 'mean', 'Can-Retrieve-Any-Document?': 'sum'})
    else:
        group_by_category_rating = df.groupby(['OB-Category', 'OB-Rating']).agg(
            {'OB-ID': 'count', 'Reciprocal-Rank': 'mean', 'Average-Precision': 'mean', 'h@1': 'mean', 'h@2': 'mean',
             'h@3': 'mean', 'h@4': 'mean', 'h@5': 'mean', 'h@6': 'mean', 'h@7': 'mean', 'h@8': 'mean', 'h@9': 'mean',
             'h@10': 'mean'})
        group_by_category_rating.insert(loc=13, column='Can-Retrieve-Any-Document?', value=df.groupby(['OB-Category', 'OB-Rating']).agg({'OB-ID': 'count'}))
    group_by_category_rating.insert(loc=0, column='OB-in-Title?', value='All')
    group_by_category_rating.insert(loc=1, column='Bug-Category', value="All")
    group_by_category_rating.insert(loc=2, column='Experiment Name', value=exp_name)

    group_by_category_rating.rename(columns={'OB-ID': '# of Retrievals'}, inplace=True)
    group_by_category_rating.rename(columns={'Reciprocal-Rank': 'MRR'}, inplace=True)
    group_by_category_rating.rename(columns={'Average-Precision': 'MAP'}, inplace=True)
    group_by_category_rating.rename(columns={'Can-Retrieve-Any-Document?': '# of Success'}, inplace=True)

    avg_hits = (group_by_category_rating['h@1'] + group_by_category_rating['h@2'] + group_by_category_rating['h@3'] + group_by_category_rating['h@4'] + group_by_category_rating['h@5'] + group_by_category_rating['h@6'] + group_by_category_rating['h@7'] + group_by_category_rating['h@8'] + group_by_category_rating['h@9'] + group_by_category_rating['h@10'])/10
    group_by_category_rating.insert(loc=16, column='Avg. Hits', value=avg_hits)

    if '# of Success' in group_by_category_rating:
        n_failed_cases = group_by_category_rating['# of Retrievals'] - group_by_category_rating['# of Success']
        group_by_category_rating.insert(loc=18, column='# of Failure', value=n_failed_cases)
    # print(group_by_category_rating.columns)
    if not os.path.exists(output_file_path):
        group_by_category_rating.to_csv(output_file_path, sep=';', mode='w')
    else:
        group_by_category_rating.to_csv(output_file_path, sep=';', mode='a', header=False,)



    # Group by OB-in-Title?
    if 'Can-Retrieve-Any-Document?' in df:
        group_by_title = df.groupby('OB-in-Title?').agg(
            {'OB-ID': 'count', 'Reciprocal-Rank': 'mean', 'Average-Precision': 'mean', 'h@1': 'mean', 'h@2': 'mean',
             'h@3': 'mean', 'h@4': 'mean', 'h@5': 'mean', 'h@6': 'mean', 'h@7': 'mean', 'h@8': 'mean', 'h@9': 'mean',
             'h@10': 'mean', 'Can-Retrieve-Any-Document?': 'sum'})
    else:
        group_by_title = df.groupby('OB-in-Title?').agg(
            {'OB-ID': 'count', 'Reciprocal-Rank': 'mean', 'Average-Precision': 'mean', 'h@1': 'mean', 'h@2': 'mean',
             'h@3': 'mean', 'h@4': 'mean', 'h@5': 'mean', 'h@6': 'mean', 'h@7': 'mean', 'h@8': 'mean', 'h@9': 'mean',
             'h@10': 'mean'})
        group_by_title.insert(loc=13, column='Can-Retrieve-Any-Document?',
                              value=df.groupby('OB-in-Title?').agg({'OB-ID': 'count'}))

    # print(group_by_ob_type)
    group_by_title = group_by_title.reset_index()

    group_by_title.rename(columns={'OB-ID': '# of Retrievals'}, inplace=True)
    group_by_title.insert(loc=0, column='OB-Category', value='All')
    group_by_title.insert(loc=1, column='OB-Rating', value='All')
    group_by_title.insert(loc=3, column='Bug-Category', value="All")

    # group_by_ob_title.move_column('OB-in-Title?', 2)
    # title_column = group_by_ob_title.pop('OB-in-Title?')
    # group_by_ob_title.insert(loc=2, column='OB-in-Title?', value=title_column)

    group_by_title.insert(loc=4, column='Experiment Name', value=exp_name)
    group_by_title.rename(columns={'Reciprocal-Rank': 'MRR'}, inplace=True)
    group_by_title.rename(columns={'Average-Precision': 'MAP'}, inplace=True)

    group_by_title.rename(columns={'Can-Retrieve-Any-Document?': '# of Success'}, inplace=True)

    avg_hits = (group_by_title['h@1'] + group_by_title['h@2'] + group_by_title['h@3'] + group_by_title[
        'h@4'] + group_by_title['h@5'] + group_by_title['h@6'] + group_by_title['h@7'] + group_by_title[
                    'h@8'] + group_by_title['h@9'] + group_by_title['h@10']) / 10
    group_by_title.insert(loc=18, column='Avg. Hits', value=avg_hits)

    if '# of Success' in group_by_title:
        n_failed_cases = group_by_title['# of Retrievals'] - group_by_title['# of Success']
        group_by_title.insert(loc=20, column='# of Failure', value=n_failed_cases)
    # print(group_by_title.columns)
    group_by_title.to_csv(output_file_path, sep=';', mode='a', header=False, index=False)

    # Group by OB-Rating
    if 'Can-Retrieve-Any-Document?' in df:
        group_by_rating = df.groupby('OB-Rating').agg(
            {'OB-ID': 'count', 'Reciprocal-Rank': 'mean', 'Average-Precision': 'mean', 'h@1': 'mean', 'h@2': 'mean',
             'h@3': 'mean', 'h@4': 'mean', 'h@5': 'mean', 'h@6': 'mean', 'h@7': 'mean', 'h@8': 'mean', 'h@9': 'mean',
             'h@10': 'mean', 'Can-Retrieve-Any-Document?': 'sum'})
    else:
        group_by_rating = df.groupby('OB-Rating').agg(
            {'OB-ID': 'count', 'Reciprocal-Rank': 'mean', 'Average-Precision': 'mean', 'h@1': 'mean', 'h@2': 'mean',
             'h@3': 'mean', 'h@4': 'mean', 'h@5': 'mean', 'h@6': 'mean', 'h@7': 'mean', 'h@8': 'mean', 'h@9': 'mean',
             'h@10': 'mean'})
        group_by_rating.insert(loc=13, column='Can-Retrieve-Any-Document?',
                               value=df.groupby('OB-Rating').agg({'OB-ID': 'count'}))

    # print(group_by_ob_type)
    group_by_rating = group_by_rating.reset_index()

    group_by_rating.rename(columns={'OB-ID': '# of Retrievals'}, inplace=True)
    group_by_rating.insert(loc=0, column='OB-Category', value='All')
    group_by_rating.insert(loc=2, column='OB-in-Title?', value='All')
    group_by_rating.insert(loc=3, column='Bug-Category', value="All")

    # group_by_ob_title.move_column('OB-Rating', 2)
    # title_column = group_by_ob_title.pop('OB-Rating')
    # group_by_ob_title.insert(loc=2, column='OB-Rating', value=title_column)

    group_by_rating.insert(loc=4, column='Experiment Name', value=exp_name)
    group_by_rating.rename(columns={'Reciprocal-Rank': 'MRR'}, inplace=True)
    group_by_rating.rename(columns={'Average-Precision': 'MAP'}, inplace=True)

    group_by_rating.rename(columns={'Can-Retrieve-Any-Document?': '# of Success'}, inplace=True)

    avg_hits = (group_by_rating['h@1'] + group_by_rating['h@2'] + group_by_rating['h@3'] + group_by_rating[
        'h@4'] + group_by_rating['h@5'] + group_by_rating['h@6'] + group_by_rating['h@7'] + group_by_rating[
                    'h@8'] + group_by_rating['h@9'] + group_by_rating['h@10']) / 10
    group_by_rating.insert(loc=18, column='Avg. Hits', value=avg_hits)

    if '# of Success' in group_by_rating:
        n_failed_cases = group_by_rating['# of Retrievals'] - group_by_rating['# of Success']
        group_by_rating.insert(loc=20, column='# of Failure', value=n_failed_cases)
    # print(group_by_rating.columns)
    group_by_rating.to_csv(output_file_path, sep=';', mode='a', header=False, index=False)


    # Group by OB-Category
    if 'Can-Retrieve-Any-Document?' in df:
        group_by_ob_category = df.groupby('OB-Category').agg(
            {'OB-ID': 'count', 'Reciprocal-Rank': 'mean', 'Average-Precision': 'mean', 'h@1': 'mean', 'h@2': 'mean',
             'h@3': 'mean', 'h@4': 'mean', 'h@5': 'mean', 'h@6': 'mean', 'h@7': 'mean', 'h@8': 'mean', 'h@9': 'mean',
             'h@10': 'mean', 'Can-Retrieve-Any-Document?': 'sum'})
    else:
        group_by_ob_category = df.groupby('OB-Category').agg(
            {'OB-ID': 'count', 'Reciprocal-Rank': 'mean', 'Average-Precision': 'mean', 'h@1': 'mean', 'h@2': 'mean',
             'h@3': 'mean', 'h@4': 'mean', 'h@5': 'mean', 'h@6': 'mean', 'h@7': 'mean', 'h@8': 'mean', 'h@9': 'mean',
             'h@10': 'mean'})
        group_by_ob_category.insert(loc=13, column='Can-Retrieve-Any-Document?',
                                    value=df.groupby('OB-Category').agg({'OB-ID': 'count'}))

    # print(group_by_ob_type)
    group_by_ob_category = group_by_ob_category.reset_index()

    group_by_ob_category.rename(columns={'OB-ID': '# of Retrievals'}, inplace=True)
    group_by_ob_category.insert(loc=1, column='OB-Rating', value='All')
    group_by_ob_category.insert(loc=2, column='OB-in-Title?', value='All')
    group_by_ob_category.insert(loc=3, column='Bug-Category', value="All")

    # group_by_ob_title.move_column('OB-Rating', 2)
    # title_column = group_by_ob_title.pop('OB-Rating')
    # group_by_ob_title.insert(loc=2, column='OB-Rating', value=title_column)

    group_by_ob_category.insert(loc=4, column='Experiment Name', value=exp_name)
    group_by_ob_category.rename(columns={'Reciprocal-Rank': 'MRR'}, inplace=True)
    group_by_ob_category.rename(columns={'Average-Precision': 'MAP'}, inplace=True)

    group_by_ob_category.rename(columns={'Can-Retrieve-Any-Document?': '# of Success'}, inplace=True)

    avg_hits = (group_by_ob_category['h@1'] + group_by_ob_category['h@2'] + group_by_ob_category['h@3'] + group_by_ob_category[
        'h@4'] + group_by_ob_category['h@5'] + group_by_ob_category['h@6'] + group_by_ob_category['h@7'] + group_by_ob_category[
                    'h@8'] + group_by_ob_category['h@9'] + group_by_ob_category['h@10']) / 10
    group_by_ob_category.insert(loc=18, column='Avg. Hits', value=avg_hits)

    if '# of Success' in group_by_ob_category:
        n_failed_cases = group_by_ob_category['# of Retrievals'] - group_by_ob_category['# of Success']
        group_by_ob_category.insert(loc=20, column='# of Failure', value=n_failed_cases)
    # print(group_by_ob_title.columns)
    group_by_ob_category.to_csv(output_file_path, sep=';', mode='a', header=False, index=False)


    # Group by Bug-Category
    if 'Can-Retrieve-Any-Document?' in df:
        group_by_bug_category = df.groupby('Bug-Category').agg(
            {'OB-ID': 'count', 'Reciprocal-Rank': 'mean', 'Average-Precision': 'mean', 'h@1': 'mean', 'h@2': 'mean',
             'h@3': 'mean', 'h@4': 'mean', 'h@5': 'mean', 'h@6': 'mean', 'h@7': 'mean', 'h@8': 'mean', 'h@9': 'mean',
             'h@10': 'mean', 'Can-Retrieve-Any-Document?': 'sum'})
    else:
        group_by_bug_category = df.groupby('Bug-Category').agg(
            {'OB-ID': 'count', 'Reciprocal-Rank': 'mean', 'Average-Precision': 'mean', 'h@1': 'mean', 'h@2': 'mean',
             'h@3': 'mean', 'h@4': 'mean', 'h@5': 'mean', 'h@6': 'mean', 'h@7': 'mean', 'h@8': 'mean', 'h@9': 'mean',
             'h@10': 'mean'})
        group_by_bug_category.insert(loc=13, column='Can-Retrieve-Any-Document?',
                                     value=df.groupby('Bug-Category').agg({'OB-ID': 'count'}))

    # print(group_by_ob_type)
    group_by_bug_category = group_by_bug_category.reset_index()

    group_by_bug_category.rename(columns={'OB-ID': '# of Retrievals'}, inplace=True)
    group_by_bug_category.insert(loc=0, column='OB-Category', value='All')
    group_by_bug_category.insert(loc=1, column='OB-Rating', value='All')
    group_by_bug_category.insert(loc=2, column='OB-in-Title?', value='All')

    # group_by_ob_title.move_column('OB-Rating', 2)
    # title_column = group_by_ob_title.pop('OB-Rating')
    # group_by_ob_title.insert(loc=2, column='OB-Rating', value=title_column)

    group_by_bug_category.insert(loc=4, column='Experiment Name', value=exp_name)
    group_by_bug_category.rename(columns={'Reciprocal-Rank': 'MRR'}, inplace=True)
    group_by_bug_category.rename(columns={'Average-Precision': 'MAP'}, inplace=True)

    group_by_bug_category.rename(columns={'Can-Retrieve-Any-Document?': '# of Success'}, inplace=True)

    avg_hits = (group_by_bug_category['h@1'] + group_by_bug_category['h@2'] + group_by_bug_category['h@3'] + group_by_bug_category[
        'h@4'] + group_by_bug_category['h@5'] + group_by_bug_category['h@6'] + group_by_bug_category['h@7'] + group_by_bug_category[
                    'h@8'] + group_by_bug_category['h@9'] + group_by_bug_category['h@10']) / 10
    group_by_bug_category.insert(loc=18, column='Avg. Hits', value=avg_hits)

    if '# of Success' in group_by_bug_category:
        n_failed_cases = group_by_bug_category['# of Retrievals'] - group_by_bug_category['# of Success']
        group_by_bug_category.insert(loc=20, column='# of Failure', value=n_failed_cases)
    # print(group_by_ob_title.columns)
    group_by_bug_category.to_csv(output_file_path, sep=';', mode='a', header=False, index=False)


def call_function_for_all_exp(result_folder_paths, output_file_paths):
    for i in range(len(result_folder_paths)):
        result_folder_path = result_folder_paths[i]
        output_file_path = output_file_paths[i]

        if os.path.exists(output_file_path):
            os.remove(output_file_path)

        for result_file in os.listdir(result_folder_path):
            if result_file.endswith('with_details.csv'):
                result_file_path = os.path.join(result_folder_path, result_file)
                print(f"Processing {result_file_path}")
                write_categorized_results(result_file_path, output_file_path)


if __name__ == '__main__':
    sr_result_folder_paths = ['./study_1/results/SL']
    sr_output_file_paths = ['./study_1/results/SL/Cat.csv']

    cr_result_folder_paths = ['./study_1/results/CL']
    cr_output_file_paths = ['./study_1/results/CL/Cat.csv']

    call_function_for_all_exp(sr_result_folder_paths, sr_output_file_paths)
    call_function_for_all_exp(cr_result_folder_paths, cr_output_file_paths)


