#!/usr/bin/env python3
import random
import json
import http.client
import re
import csv
import argparse
import os
import pandas as pd

from helpers_code_mapping import CodeMappingHelper
from write_results import WriteResults

from file_analysis import FileAnalysis


# ---- Globals ----

class SoureCodeMapping:

    def __init__(self, args):
        self.codeMappingHelper = CodeMappingHelper()
        self.writeResults = WriteResults()
        self.fileAnalysis = FileAnalysis()

        self.corpusType = args['corpus'] # All_Java_Files or GUI_State_and_All_GUI_Component_IDs or All_GUI_Component IDs

        self.query_name = args['query']
        self.number_of_screens = int(args['screens'])

    def get_query_name(self):
        return self.query_name

    def get_corpus_flag(self):
        return self.filteredCorpusFlag
    # ------------------------------------------------------------------------------
    # ---- Main ----

    def get_files_gui_state_all_component_ids(self, parent_directory, list_of_activities, all_comp_ids):
        gui_state_files = self.fileAnalysis.get_filtered_files(parent_directory, list_of_activities)

        all_comp_id_related_files = self.fileAnalysis.get_files_if_term_exists(parent_directory, all_comp_ids)
        all_comp_id_related_files = self.codeMappingHelper.get_files_that_not_exist_in_first_list(all_comp_id_related_files, gui_state_files)

        filtered_files = []
        filtered_files.extend(gui_state_files)
        filtered_files.extend(all_comp_id_related_files)

        return filtered_files

    def get_all_component_id_related_files(self, parent_directory, all_comp_ids):
        all_comp_id_related_files = self.fileAnalysis.get_files_if_term_exists(parent_directory, all_comp_ids)

        return all_comp_id_related_files

    def get_files_gui_state_interacted_component_ids(self, parent_directory, list_of_activities, list_of_interacted_comp_ids):
        gui_state_files = self.fileAnalysis.get_filtered_files(parent_directory, list_of_activities)

        # Retrieve interacted component id related files and remove those files where filenames matches with the activities/fragments
        intercated_comp_id_related_files = self.fileAnalysis.get_files_if_term_exists(parent_directory, list_of_interacted_comp_ids)
        intercated_comp_id_related_files = self.codeMappingHelper.get_files_that_not_exist_in_first_list(intercated_comp_id_related_files, gui_state_files)

        filtered_files = []
        filtered_files.extend(gui_state_files)
        filtered_files.extend(intercated_comp_id_related_files)

        return filtered_files
    
    def get_all_java_files(self, bug_id, project_dir):
        parent_directory = project_dir + "/bug-" + bug_id
        all_java_files = self.fileAnalysis.get_all_java_files(parent_directory)

        return all_java_files
    
    def get_mapping_info(self):
        if args['exp_name'] == 'Individual-OB-3-Screens':
            df = pd.read_csv("../../../data/Mapping-Info/blip_zs_sr_crash_ob_top_3_screens.csv", sep=';')
        elif args['exp_name'] == 'Individual-OB-4-Screens':
            df = pd.read_csv("../../../data/Mapping-Info/blip_zs_sr_crash_ob_top_4_screens.csv", sep=';')
        return df

    def main(self):

        df = self.get_mapping_info()
        
        corpus_folder = args['result'] + "/" + "Screen-" + str(self.number_of_screens) + "/" + "Corpus-" + self.corpusType
        result_folder = []
        print(corpus_folder)
        os.makedirs(corpus_folder, exist_ok=True)

        query_filename = corpus_folder  + "/Queries.csv"
        self.writeResults.write_header_for_mapping_query(query_filename)

        if args['operations']=="Filtering+Boosting":
            result_folder = corpus_folder + "/Boosting-" + self.query_name
            os.makedirs(result_folder, exist_ok=True)
            matched_files_stored_filename = result_folder + "/Match_Query_File_List.csv"
            self.writeResults.write_header_for_file_list(matched_files_stored_filename)

            not_matched_files_stored_filename = result_folder + "/Not_Match_Query_File_List.csv"
            self.writeResults.write_header_for_file_list(not_matched_files_stored_filename)

        files_in_corpus_filename = corpus_folder + "/Files_In_Corpus.csv"
        self.writeResults.write_header_for_file_list(files_in_corpus_filename)

        for _, row in df.iterrows():
            bug_id = row["Bug-ID"].strip("Bug")
            ob_id = row["OB-ID"]
            ob_text = row["OB-Text"]

            parent_directory = args['buggy_project_dir'] + "/bug-" + bug_id
            all_java_files = self.get_all_java_files(bug_id, args['buggy_project_dir'])

            json_file = open(args['trace_replayer_dir'] + '/TR' + bug_id +'/Execution-1.json')
            data = json.load(json_file)

            list_of_activities = []
            list_of_fragments = []
            list_of_interacted_comp_ids = []
            all_comp_ids = []

            seq_ids = row["Sequence-IDs"].strip("[]").split(", ")
            
            #3/4 screens
            interacted_comp_states = [int(x) for x in seq_ids]
            screen_states = [int(x) for x in seq_ids]

            #1 screen
            # interacted_comp_states = [int(seq_ids[0])]
            # screen_states = [int(seq_ids[0])]

            for step in data['steps']:
                if step['sequenceStep'] in interacted_comp_states:
                    interacted_comp_ids_step = self.codeMappingHelper.get_interacted_component_ids_for_step(step)
                    list_of_interacted_comp_ids.extend(interacted_comp_ids_step)

                if step['sequenceStep'] in screen_states:
                    activity = self.codeMappingHelper.get_screen_activity(step)
                    list_of_activities.append(activity)
                    cur_fragment = self.codeMappingHelper.get_screen_fragment(step)
                    list_of_fragments.append(cur_fragment)

                    step_comp_ids = self.codeMappingHelper.get_all_component_ids_unprocessed(step['screen']['dynGuiComponents'])
                    all_comp_ids.extend(step_comp_ids)

            json_file.close()
            list_of_activities = self.codeMappingHelper.clean_query(list_of_activities)
            list_of_fragments = self.codeMappingHelper.clean_query(list_of_fragments)
            
            list_of_interacted_comp_ids = self.codeMappingHelper.clean_query(list_of_interacted_comp_ids)
            all_comp_ids = self.codeMappingHelper.clean_query(all_comp_ids)
            
            self.writeResults.write_row_for_mapping_query(query_filename, bug_id, ob_id, ob_text, screen_states, list_of_activities, list_of_fragments, list_of_interacted_comp_ids, all_comp_ids)
            list_of_activities.extend(list_of_fragments)

            files_in_corpus = []

            # This segment is to check whether we apply filtering or not and build our corpus based on that criteria
            if self.corpusType == "All_Java_Files":
                files_in_corpus = all_java_files
            elif self.corpusType == "GUI_State_and_All_GUI_Component_IDs":
                files_in_corpus = self.get_files_gui_state_all_component_ids(parent_directory, list_of_activities, all_comp_ids)
            elif self.corpusType == "All_GUI_Component_IDs":
                files_in_corpus = self.get_all_component_id_related_files(parent_directory, all_comp_ids)
            elif self.corpusType == "GUI_States":
                files_in_corpus = self.fileAnalysis.get_filtered_files(parent_directory, list_of_activities)
        
            #Store all files in corpus in corpus filelist
            self.writeResults.write_file_list_each_row(bug_id, ob_id, ob_text, row["Sequence-IDs"], files_in_corpus, files_in_corpus_filename, "Corpus")

            if args['operations']=="Filtering+Boosting":
                # This segment is to separate those files that we boost
                if self.query_name == "GUI_States":
                    matched_files = self.fileAnalysis.get_filtered_files(parent_directory, list_of_activities)
                    matched_files = self.codeMappingHelper.get_files_if_exist_in_corpus(matched_files, files_in_corpus)
                    self.writeResults.write_file_list_each_row(bug_id, ob_id, ob_text, row["Sequence-IDs"], matched_files, matched_files_stored_filename, "ActivityFragments")

                elif self.query_name == "GUI_State_and_All_GUI_Component_IDs":
                    matched_files = self.get_files_gui_state_all_component_ids(parent_directory, list_of_activities, all_comp_ids)
                    matched_files = self.codeMappingHelper.get_files_if_exist_in_corpus(matched_files, files_in_corpus)
                    self.writeResults.write_file_list_each_row(bug_id, ob_id, ob_text, row["Sequence-IDs"], matched_files, matched_files_stored_filename, "ActivityFragments/AllGUI")

                elif self.query_name == "All_GUI_Component_IDs":
                    matched_files = self.get_all_component_id_related_files(parent_directory, all_comp_ids)
                    matched_files = self.codeMappingHelper.get_files_if_exist_in_corpus(matched_files, files_in_corpus)
                    self.writeResults.write_file_list_each_row(bug_id, ob_id, ob_text, row["Sequence-IDs"], matched_files, matched_files_stored_filename, "All")

                not_matched_files = self.codeMappingHelper.get_files_that_not_exist_in_first_list(files_in_corpus, matched_files)
                self.writeResults.write_file_list_each_row(bug_id, ob_id, ob_text, row["Sequence-IDs"], not_matched_files, not_matched_files_stored_filename, "NotMatched")

            
    # ------------------------------------------------------------------------------

if __name__ == '__main__':
    # https://stackoverflow.com/questions/7427101/simple-argparse-example-wanted-1-argument-3-results
    parser = argparse.ArgumentParser()
    
    parser.add_argument('-c','--corpus', help='Description for boosting', required=True)
    parser.add_argument('-q','--query', help='Description for query', required=False)
    parser.add_argument('-r','--result', help='Description for results', required=True)
    parser.add_argument('-s','--screens', help='Number of screens', required=True)
    parser.add_argument('-ops','--operations', help='Operations', required=True)
    parser.add_argument('-bpr','--buggy_project_dir', help='Buggy Projects Repository', required=True)
    parser.add_argument('-tr','--trace_replayer_dir', help='Trace Replayer Data Directory', required=True)
    parser.add_argument('-en', '--exp_name', help='Experiment Name', required=True)

    args = vars(parser.parse_args())

    sourcodeCodeMapping = SoureCodeMapping(args)
    sourcodeCodeMapping.main()



