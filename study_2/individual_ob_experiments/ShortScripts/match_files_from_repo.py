import glob
import pandas as pd
import os, shutil
import argparse

class MatchFiles:
    def __init__(self, args):
        self.corpus_path = "Screen-" + args['screens'] + "/" + "Corpus-" + args['corpus'] 
        print(self.corpus_path)
        if args['operations']=='Filtering+Boosting':
            self.subpath = self.corpus_path + "/Boosting-" + args['query']
            self.filename_path = args['filtering_boosting_filenames'] + "/" + self.subpath
            self.updated_repo_path = args['filtered_boosted_repo'] + "/" + self.subpath
        elif args['operations']=='Filtering':
            self.filename_path = args['filtering_boosting_filenames'] + "/" + self.corpus_path 
            self.updated_repo_path = args['filtered_boosted_repo'] + "/" + self.corpus_path 

    def get_cur_java_files(self, parent_directory):
        all_files = []
        
        for filename in sorted(glob.glob(f'{parent_directory}/**/*.java', recursive = True)):
            all_files.append(filename)

        return all_files

    def get_all_java_files(self, bug_id):
        parent_directory = args['buggy_project_dir'] + "/bug-" + bug_id
        all_java_files = self.get_cur_java_files(parent_directory)

        return all_java_files

    def get_ranked_files(self, bug_id, ob_id, filename):
        file_list_df = pd.read_csv(filename)
        files_for_bug_id = file_list_df.loc[(file_list_df['Bug-ID']==int(bug_id)) & (file_list_df['OB-ID']==int(ob_id)), 'FilePaths'].values.tolist()

        return files_for_bug_id

    def get_files_on_query_matching(self, bug_issue_id, ob_id, csv_file):
        filtered_files_stored_file = self.filename_path + "/" + csv_file
        filtered_files = self.get_ranked_files(bug_issue_id, ob_id, filtered_files_stored_file)

        file_list = []
        for item_file in filtered_files:
            # Changing deafult filepaths with the filepaths in the buggy projects that we are working on
            file_list.append(item_file.replace(args['buggy_project_dir_in_csv'], args['buggy_project_dir']))

        return file_list

    def match_files_separated(self, all_java_files, bug_id, ob_id, filtered_files, query_matching_type):
        for file in all_java_files:
            if file in filtered_files:
                new_path = file.replace(args['buggy_project_dir'], self.updated_repo_path + "/" + query_matching_type)
                new_path = new_path.replace("bug-"+str(bug_id), "bug-"+str(bug_id)+"/ob-"+str(ob_id))
                try:
                    shutil.copy(file, new_path)
                except IOError as io_err:
                    os.makedirs(os.path.dirname(new_path))
                    shutil.copy(file, new_path)

    def get_mapping_info(self):
        if args['exp_name'] == 'Individual-OB-3-Screens':
            df = pd.read_csv("../../data/Mapping-Info/blip_zs_sr_crash_ob_top_3_screens.csv", sep=';')
        elif args['exp_name'] == 'Individual-OB-4-Screens':
            df = pd.read_csv("../../data/Mapping-Info/blip_zs_sr_crash_ob_top_4_screens.csv", sep=';')
        return df

    def main(self):
        df = self.get_mapping_info()

        for _, row in df.iterrows():
            bug_id = row["Bug-ID"].strip("Bug")
            ob_id = row["OB-ID"]

            all_java_files = self.get_all_java_files(bug_id)

            if args['operations']=='Filtering+Boosting':
                matched_files = self.get_files_on_query_matching(bug_id, ob_id, "Match_Query_File_List.csv")
                self.match_files_separated(all_java_files, bug_id, ob_id, matched_files, "MatchedQueryFiles")

                unmatched_files = self.get_files_on_query_matching(bug_id, ob_id, "Not_Match_Query_File_List.csv")
                self.match_files_separated(all_java_files, bug_id, ob_id, unmatched_files, "NotMatchedQueryFiles")

            elif args['operations']=='Filtering':
                files_corpus = self.get_files_on_query_matching(bug_id, ob_id, "Files_In_Corpus.csv")
                self.match_files_separated(all_java_files, bug_id, ob_id, files_corpus, "FilteredFiles")


if __name__ == '__main__':
    # https://stackoverflow.com/questions/7427101/simple-argparse-example-wanted-1-argument-3-results
    parser = argparse.ArgumentParser()

    parser.add_argument('-c','--corpus', help='Description for boosting', required=True)
    parser.add_argument('-s', '--screens', help='Number of screens', required=True)
    parser.add_argument('-q', '--query', help='Description for query', required=False)
    parser.add_argument('-bpd', '--buggy_project_dir', help='Buggy Projects Directory', required=True)
    parser.add_argument('-fbfile', '--filtering_boosting_filenames', help='Filtering Boosting Filenames', required=True)
    parser.add_argument('-fbr', '--filtered_boosted_repo', help='Filtered Boosted Repos', required=True)
    parser.add_argument('-bpdcsv', '--buggy_project_dir_in_csv', help='Buggy Projects Directory Path in CSV', required=True)
    parser.add_argument('-ops','--operations', help='Operations', required=True)
    parser.add_argument('-en','--exp_name', help='Experiment Name', required=True)

    args = vars(parser.parse_args())

    matchFiles = MatchFiles(args)
    matchFiles.main()
