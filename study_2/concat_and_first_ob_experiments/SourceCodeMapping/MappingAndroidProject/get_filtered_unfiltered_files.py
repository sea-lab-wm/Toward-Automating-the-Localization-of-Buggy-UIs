import json
import http.client
import re
import csv
import argparse
import os

from helpers_code_mapping import CodeMappingHelper
from write_results import WriteResults
from file_analysis import FileAnalysis
from read_files import ReadFiles


class SoureCodeMapping:

    def __init__(self, args):
        self.codeMappingHelper = CodeMappingHelper()
        self.writeResults = WriteResults()
        self.fileAnalysis = FileAnalysis()
        self.readFiles = ReadFiles()
        self.corpusType = args[
            'corpus']  # All_Java_Files or GUI_State_and_All_GUI_Component_IDs or All_GUI_Component IDs
        self.query_name = args['query']
        self.number_of_screens = int(args['screens'])

    def get_query_name(self):
        return self.query_name

    def get_corpus_flag(self):
        return self.filteredCorpusFlag

    def get_files_gui_state_all_component_ids(self, parent_directory, list_of_activities, all_comp_ids):
        gui_state_files = self.fileAnalysis.get_filtered_files(parent_directory, list_of_activities)

        all_comp_id_related_files = self.fileAnalysis.get_files_if_term_exists(parent_directory, all_comp_ids)
        all_comp_id_related_files = self.codeMappingHelper.get_files_that_not_exist_in_first_list(
            all_comp_id_related_files, gui_state_files)

        filtered_files = []
        filtered_files.extend(gui_state_files)
        filtered_files.extend(all_comp_id_related_files)

        return filtered_files

    def get_all_component_id_related_files(self, parent_directory, all_comp_ids):
        all_comp_id_related_files = self.fileAnalysis.get_files_if_term_exists(parent_directory, all_comp_ids)

        return all_comp_id_related_files

    def get_files_gui_state_interacted_component_ids(self, parent_directory, list_of_activities,
                                                     list_of_interacted_comp_ids):
        gui_state_files = self.fileAnalysis.get_filtered_files(parent_directory, list_of_activities)

        # Retrieve interacted component id related files and remove those files where filenames matches with the activities/fragments
        intercated_comp_id_related_files = self.fileAnalysis.get_files_if_term_exists(parent_directory,
                                                                                      list_of_interacted_comp_ids)
        intercated_comp_id_related_files = self.codeMappingHelper.get_files_that_not_exist_in_first_list(
            intercated_comp_id_related_files, gui_state_files)

        filtered_files = []
        filtered_files.extend(gui_state_files)
        filtered_files.extend(intercated_comp_id_related_files)

        return filtered_files

    def main(self):
        # print(args['result'])
        if args['exp_name'] == 'Concat-OB-3-Screens':
            bug_ids_states = [("2", [100, 89, 69]), ("8", [30, 41, 35]), ("10", [17, 64, 78]), ("18", [31, 14, 21]),
                              ("19", [8, 50, 33]), ("44", [18, 9, 26]), ("53", [19, 12, 73]), ("117", [11, 13, 15]),
                              ("128", [28, 47, 31]), ("129", [47, 46, 72]), ("130", [3, 4, 15]), ("135", [32, 15, 29]),
                              ("191", [10, 62, 65]), ("206", [15, 54, 24]), ("209", [43, 59, 45]), ("256", [19, 9, 18]),
                              ("1073", [8, 7, 9]), ("1096", [10, 14, 11]), ("1202", [19, 9, 8]), ("1207", [4, 5, 9]),
                              ("1214", [4, 2, 16]), ("1215", [17, 26, 22]), ("1224", [80, 14, 68]),
                              ("1299", [10, 51, 14]), ("1399", [15, 35, 10]), ("1430", [11, 10, 19]),
                              ("1441", [37, 6, 19]), ("1481", [1, 25, 55]), ("1645", [31, 29, 34]),
                              ("54", [12, 41, 14]), ("76", [7, 41, 6]), ("92", [7, 4, 5]), ("158", [46, 23, 31]),
                              ("160", [32, 21, 25]), ("162", [1, 7, 5]), ("192", [17, 7, 9]), ("199", [18, 7, 20]),
                              ("200", [4, 3, 7]), ("1150", [7, 6, 11]), ("1198", [7, 16, 21]), ("1228", [12, 15, 11]),
                              ("1389", [6, 25, 3]), ("1425", [14, 13, 30]), ("1446", [12, 19, 13]), ("1568", [6, 4, 2]),
                              ("71", [46, 23, 4]), ("201", [38, 43, 53]), ("1146", [2, 6, 3]), ("1147", [17, 29, 22]),
                              ("1151", [4, 1, 19]), ("1205", [80, 82, 78]), ("1406", [10, 9, 7]), ("1445", [6, 5, 15]),
                              ("45", [9, 11, 21]), ("106", [37, 25, 33]), ("110", [12, 10, 11]), ("168", [7, 2, 1]),
                              ("248", [5, 45, 21]), ("1563", [7, 9, 6]), ("1223", [20, 21, 59]), ("1641", [3, 5, 43]),
                              ("11", [2, 1, 3]), ("55", [12, 14, 26]), ("56", [14, 13, 12]), ("1213", [31, 10, 30]),
                              ("1222", [11, 10, 13]), ("1428", [13, 10, 23]), ("84", [1, 17, 4]), ("87", [33, 6, 21]),
                              ("159", [31, 27, 33]), ("193", [7, 11, 2]), ("271", [12, 11, 10]), ("275", [6, 2, 9]),
                              ("1028", [11, 12, 10]), ("1089", [14, 1, 5]), ("1130", [12, 1, 10]),
                              ("1402", [18, 4, 16]), ("1403", [1, 2, 5]), ("1640", [3, 6, 18])]

        elif args['exp_name'] == 'Concat-OB-4-Screens':
            bug_ids_states = [("2", [100, 89, 69, 10]), ("8", [30, 41, 35, 34]), ("10", [17, 64, 78, 48]),
                              ("18", [31, 14, 21, 85]), ("19", [8, 50, 33, 27]), ("44", [18, 9, 26, 23]),
                              ("53", [19, 12, 73, 62]), ("117", [11, 13, 15, 10]), ("128", [28, 47, 31, 27]),
                              ("129", [47, 46, 72, 83]), ("130", [3, 4, 15, 2]), ("135", [32, 15, 29, 14]),
                              ("206", [15, 54, 24, 83]), ("209", [43, 59, 45, 86]), ("256", [19, 9, 18, 7]),
                              ("1073", [8, 7, 9, 1]), ("1096", [10, 14, 11, 9]), ("1202", [19, 9, 8, 2]),
                              ("1207", [4, 5, 9, 3]), ("1214", [4, 2, 16, 22]), ("1215", [17, 26, 22, 42]),
                              ("1224", [80, 14, 68, 64]), ("1299", [10, 51, 14, 12]), ("1399", [15, 35, 10, 13]),
                              ("1430", [11, 10, 19, 14]), ("1441", [37, 6, 19, 28]), ("1481", [1, 25, 55, 6]),
                              ("1645", [31, 29, 34, 3]), ("54", [12, 41, 14, 43]), ("76", [7, 41, 6, 4]),
                              ("92", [7, 4, 5, 22]), ("158", [46, 23, 31, 41]), ("160", [32, 21, 25, 46]),
                              ("162", [1, 7, 5, 33]), ("192", [17, 7, 9, 3]), ("199", [18, 7, 20, 8]),
                              ("200", [4, 3, 7, 10]), ("1150", [7, 6, 11, 3]), ("1198", [7, 16, 21, 48]),
                              ("1228", [12, 15, 11, 24]), ("1389", [6, 25, 3, 26]), ("1425", [14, 13, 30, 28]),
                              ("1446", [12, 19, 13, 6]), ("1568", [6, 4, 2, 11]), ("71", [46, 23, 4, 8]),
                              ("201", [38, 43, 53, 34]), ("1146", [2, 6, 3, 27]), ("1147", [17, 29, 22, 1]),
                              ("1151", [4, 1, 19, 18]), ("1205", [80, 82, 78, 39]), ("1406", [10, 9, 7, 28]),
                              ("1445", [6, 5, 15, 9]), ("45", [9, 11, 21, 10]), ("106", [37, 25, 33, 22]),
                              ("110", [12, 10, 11, 3]), ("168", [7, 2, 1, 29]), ("248", [5, 45, 21, 4]),
                              ("1563", [7, 9, 6, 2]), ("1223", [20, 21, 59, 50]), ("1641", [3, 5, 43, 40]),
                              ("55", [12, 14, 26, 24]), ("56", [14, 13, 12, 5]), ("1213", [31, 10, 30, 34]),
                              ("1222", [11, 10, 13, 12]), ("1428", [13, 10, 23, 9]), ("84", [1, 17, 4, 3]),
                              ("87", [33, 6, 21, 1]), ("159", [31, 27, 33, 32]), ("193", [7, 11, 2, 17]),
                              ("271", [12, 11, 10, 4]), ("275", [6, 2, 9, 25]), ("1028", [11, 12, 10, 8]),
                              ("1089", [14, 1, 5, 8]), ("1130", [12, 1, 10, 2]), ("1402", [18, 4, 16, 14]),
                              ("1403", [1, 2, 5, 25]), ("1640", [3, 6, 18, 9])]

        elif args['exp_name'] == 'First-OB-3-Screens':
            bug_ids_states = [("10", [17, 64, 78]), ("1028", [12, 11, 10]), ("106", [10, 15, 27]), ("1073", [8, 7, 36]),
                              ("1089", [14, 1, 5]), ("1096", [11, 14, 10]), ("11", [2, 3, 1]), ("110", [5, 12, 3]),
                              ("1130", [12, 1, 11]), ("1146", [2, 6, 3]), ("1147", [17, 29, 37]), ("1150", [7, 11, 3]),
                              ("1151", [4, 1, 33]), ("117", [11, 13, 15]), ("1198", [19, 21, 16]), ("1202", [19, 9, 8]),
                              ("1205", [39, 14, 80]), ("1207", [25, 14, 4]), ("1213", [31, 30, 10]),
                              ("1214", [2, 4, 16]), ("1215", [22, 17, 26]), ("1222", [11, 10, 20]),
                              ("1223", [20, 50, 21]), ("1224", [80, 14, 64]), ("1228", [11, 12, 15]),
                              ("128", [28, 47, 43]), ("129", [47, 46, 83]), ("1299", [10, 51, 14]), ("130", [1, 2, 4]),
                              ("135", [32, 15, 29]), ("1389", [3, 26, 6]), ("1399", [15, 2, 35]), ("1402", [4, 16, 2]),
                              ("1403", [1, 2, 5]), ("1406", [9, 10, 42]), ("1425", [14, 13, 30]),
                              ("1428", [13, 10, 23]), ("1430", [19, 11, 10]), ("1441", [19, 37, 6]),
                              ("1445", [6, 5, 9]), ("1446", [12, 19, 13]), ("1481", [25, 3, 4]), ("1563", [7, 9, 6]),
                              ("1568", [2, 4, 14]), ("158", [40, 23, 41]), ("159", [31, 27, 33]), ("160", [32, 21, 25]),
                              ("162", [1, 7, 53]), ("1640", [3, 6, 18]), ("1641", [3, 5, 43]), ("1645", [29, 31, 3]),
                              ("168", [7, 2, 1]), ("18", [31, 14, 84]), ("19", [8, 50, 33]), ("191", [10, 62, 65]),
                              ("192", [7, 5, 9]), ("193", [7, 11, 2]), ("199", [9, 10, 8]), ("2", [100, 35, 89]),
                              ("200", [4, 3, 7]), ("201", [43, 6, 34]), ("206", [15, 36, 35]), ("209", [43, 59, 45]),
                              ("248", [5, 45, 21]), ("256", [19, 7, 9]), ("271", [12, 11, 10]), ("275", [6, 2, 9]),
                              ("44", [18, 9, 26]), ("45", [11, 9, 10]), ("53", [12, 19, 7]), ("54", [3, 14, 12]),
                              ("55", [14, 26, 12]), ("56", [14, 13, 12]), ("71", [10, 5, 4]), ("76", [41, 4, 7]),
                              ("8", [30, 41, 35]), ("84", [1, 14, 10]), ("87", [33, 43, 12]), ("92", [7, 5, 4])]

        elif args['exp_name'] == 'First-OB-4-Screens':
            bug_ids_states = [("10", [17, 64, 78, 48]), ("1028", [12, 11, 10, 8]), ("106", [10, 15, 27, 11]),
                              ("1073", [8, 7, 36, 1]), ("1089", [14, 1, 5, 8]), ("1096", [11, 14, 10, 9]),
                              ("110", [5, 12, 3, 9]), ("1130", [12, 1, 11, 2]), ("1146", [2, 6, 3, 33]),
                              ("1147", [17, 29, 37, 34]), ("1150", [7, 11, 3, 6]), ("1151", [4, 1, 33, 7]),
                              ("117", [11, 13, 15, 10]), ("1198", [19, 21, 16, 48]), ("1202", [19, 9, 8, 2]),
                              ("1205", [39, 14, 80, 82]), ("1207", [25, 14, 4, 5]), ("1213", [31, 30, 10, 34]),
                              ("1214", [2, 4, 16, 28]), ("1215", [22, 17, 26, 42]), ("1222", [11, 10, 20, 12]),
                              ("1223", [20, 50, 21, 59]), ("1224", [80, 14, 64, 68]), ("1228", [11, 12, 15, 24]),
                              ("128", [28, 47, 43, 48]), ("129", [47, 46, 83, 4]), ("1299", [10, 51, 14, 12]),
                              ("130", [1, 2, 4, 3]), ("135", [32, 15, 29, 14]), ("1389", [3, 26, 6, 25]),
                              ("1399", [15, 2, 35, 10]), ("1402", [4, 16, 2, 18]), ("1403", [1, 2, 5, 25]),
                              ("1406", [9, 10, 42, 8]), ("1425", [14, 13, 30, 28]), ("1428", [13, 10, 23, 11]),
                              ("1430", [19, 11, 10, 14]), ("1441", [19, 37, 6, 28]), ("1445", [6, 5, 9, 10]),
                              ("1446", [12, 19, 13, 6]), ("1481", [25, 3, 4, 34]), ("1563", [7, 9, 6, 5]),
                              ("1568", [2, 4, 14, 6]), ("158", [40, 23, 41, 46]), ("159", [31, 27, 33, 32]),
                              ("160", [32, 21, 25, 46]), ("162", [1, 7, 53, 33]), ("1640", [3, 6, 18, 16]),
                              ("1641", [3, 5, 43, 40]), ("1645", [29, 31, 3, 5]), ("168", [7, 2, 1, 29]),
                              ("18", [31, 14, 84, 21]), ("19", [8, 50, 33, 27]), ("192", [7, 5, 9, 2]),
                              ("193", [7, 11, 2, 17]), ("199", [9, 10, 8, 20]), ("2", [100, 35, 89, 69]),
                              ("200", [4, 3, 7, 13]), ("201", [43, 6, 34, 40]), ("206", [15, 36, 35, 54]),
                              ("209", [43, 59, 45, 86]), ("248", [5, 45, 21, 4]), ("256", [19, 7, 9, 18]),
                              ("271", [12, 11, 10, 4]), ("275", [6, 2, 9, 25]), ("44", [18, 9, 26, 23]),
                              ("45", [11, 9, 10, 21]), ("53", [12, 19, 7, 55]), ("54", [3, 14, 12, 43]),
                              ("55", [14, 26, 12, 8]), ("56", [14, 13, 12, 20]), ("71", [10, 5, 4, 46]),
                              ("76", [41, 4, 7, 5]), ("8", [30, 41, 35, 34]), ("84", [1, 14, 10, 5]),
                              ("87", [33, 43, 12, 21]), ("92", [7, 5, 4, 27])]

        # print("Bug Ids and States: ", bug_ids_states)
        # print(f"# of Bug Ids and States: {len(bug_ids_states)}")
        corpus_folder = args['result'] + "/" + "Screen-" + str(
            self.number_of_screens) + "/" + "Corpus-" + self.corpusType
        result_folder = []
        print(corpus_folder)
        os.makedirs(corpus_folder, exist_ok=True)

        query_filename = corpus_folder + "/Queries.csv"
        self.writeResults.write_header_for_query(query_filename)

        if args['operations'] == "Filtering+Boosting":
            result_folder = corpus_folder + "/Boosting-" + self.query_name
            os.makedirs(result_folder, exist_ok=True)
            matched_files_stored_filename = result_folder + "/Match_Query_File_List.csv"
            self.writeResults.write_header_for_file_list(matched_files_stored_filename)

            not_matched_files_stored_filename = result_folder + "/Not_Match_Query_File_List.csv"
            self.writeResults.write_header_for_file_list(not_matched_files_stored_filename)

        files_in_corpus_filename = corpus_folder + "/Files_In_Corpus.csv"
        self.writeResults.write_header_for_file_list(files_in_corpus_filename)

        for issue_id, app_final_state in bug_ids_states:
            bug_id = issue_id

            # TODO: Copy the absolute path of study_2/data/BuggyProjects and replace it with the ".../data/BuggyProjects"
            parent_directory = "/Volumes/Education/GitHub/Toward-Automating-the-Localization-of-Buggy-UIs-Anonymized/study_2/data/BuggyProjects/bug-" + bug_id
            all_java_files = self.readFiles.get_all_java_files(bug_id)

            # TODO: Copy the absolute path of study_2/data/TraceReplayer-Data and replace it with the ".../data/TraceReplayer-Data"
            json_file = open('/Volumes/Education/GitHub/Toward-Automating-the-Localization-of-Buggy-UIs-Anonymized/study_2/data/TraceReplayer-Data/TR' + bug_id + '/Execution-1.json')
            data = json.load(json_file)

            list_of_activities = []
            list_of_fragments = []
            list_of_interacted_comp_ids = []
            all_comp_ids = []

            final_state = app_final_state

            interacted_comp_states = []
            # Provide the sequence IDs of TR data
            screen_states = [int(x) for x in final_state]

            for step in data['steps']:
                if step['sequenceStep'] in interacted_comp_states:
                    interacted_comp_ids_step = self.codeMappingHelper.get_interacted_component_ids_for_step(step)
                    list_of_interacted_comp_ids.extend(interacted_comp_ids_step)

                if step['sequenceStep'] in screen_states:
                    activity = self.codeMappingHelper.get_screen_activity(step)
                    list_of_activities.append(activity)
                    cur_fragment = self.codeMappingHelper.get_screen_fragment(step)
                    list_of_fragments.append(cur_fragment)

                    step_comp_ids = self.codeMappingHelper.get_all_component_ids_unprocessed(
                        step['screen']['dynGuiComponents'])
                    all_comp_ids.extend(step_comp_ids)

            json_file.close()
            list_of_activities = self.codeMappingHelper.clean_query(list_of_activities)
            list_of_fragments = self.codeMappingHelper.clean_query(list_of_fragments)

            list_of_interacted_comp_ids = self.codeMappingHelper.clean_query(list_of_interacted_comp_ids)
            all_comp_ids = self.codeMappingHelper.clean_query(all_comp_ids)

            self.writeResults.write_row_for_query(query_filename, bug_id, list_of_activities, list_of_fragments,
                                                  list_of_interacted_comp_ids, all_comp_ids)
            list_of_activities.extend(list_of_fragments)

            files_in_corpus = []

            # This segment is to check whether we apply filtering or not and build our corpus based on that criteria
            if self.corpusType == "All_Java_Files":
                files_in_corpus = all_java_files
            elif self.corpusType == "GUI_State_and_All_GUI_Component_IDs":
                files_in_corpus = self.get_files_gui_state_all_component_ids(parent_directory, list_of_activities,
                                                                             all_comp_ids)
            elif self.corpusType == "All_GUI_Component_IDs":
                files_in_corpus = self.get_all_component_id_related_files(parent_directory, all_comp_ids)
            elif self.corpusType == "GUI_States":
                files_in_corpus = self.fileAnalysis.get_filtered_files(parent_directory, list_of_activities)
            elif self.corpusType == "Interacted_GUI_Component_IDs":
                files_in_corpus = self.fileAnalysis.get_files_if_term_exists(parent_directory,
                                                                             list_of_interacted_comp_ids)
            elif self.corpusType == "GUI_State_and_Interacted_GUI_Component_IDs":
                files_in_corpus = self.get_files_gui_state_interacted_component_ids(parent_directory,
                                                                                    list_of_activities,
                                                                                    list_of_interacted_comp_ids)

            # Store all files in corpus in corpus filelist
            self.writeResults.write_file_list_each_row(bug_id, files_in_corpus, files_in_corpus_filename, "Corpus")

            if args['operations'] == "Filtering+Boosting":
                # This segment is to separate those files that we boost
                if self.query_name == "GUI_State_and_Interacted_GUI_Component_IDs":
                    matched_files = self.get_files_gui_state_interacted_component_ids(parent_directory,
                                                                                      list_of_activities,
                                                                                      list_of_interacted_comp_ids)
                    matched_files = self.codeMappingHelper.get_files_if_exist_in_corpus(matched_files, files_in_corpus)
                    self.writeResults.write_file_list_each_row(bug_id, matched_files, matched_files_stored_filename,
                                                               "ActivityFragments/Interacted")

                elif self.query_name == "GUI_States":
                    matched_files = self.fileAnalysis.get_filtered_files(parent_directory, list_of_activities)
                    matched_files = self.codeMappingHelper.get_files_if_exist_in_corpus(matched_files, files_in_corpus)
                    self.writeResults.write_file_list_each_row(bug_id, matched_files, matched_files_stored_filename,
                                                               "ActivityFragments")

                elif self.query_name == "GUI_State_and_All_GUI_Component_IDs":
                    matched_files = self.get_files_gui_state_all_component_ids(parent_directory, list_of_activities,
                                                                               all_comp_ids)
                    matched_files = self.codeMappingHelper.get_files_if_exist_in_corpus(matched_files, files_in_corpus)
                    self.writeResults.write_file_list_each_row(bug_id, matched_files, matched_files_stored_filename,
                                                               "ActivityFragments/AllGUI")

                elif self.query_name == "Interacted_GUI_Component_IDs":
                    matched_files = self.fileAnalysis.get_files_if_term_exists(parent_directory,
                                                                               list_of_interacted_comp_ids)
                    matched_files = self.codeMappingHelper.get_files_if_exist_in_corpus(matched_files, files_in_corpus)
                    self.writeResults.write_file_list_each_row(bug_id, matched_files, matched_files_stored_filename,
                                                               "Interacted")

                elif self.query_name == "All_GUI_Component_IDs":
                    matched_files = self.get_all_component_id_related_files(parent_directory, all_comp_ids)
                    matched_files = self.codeMappingHelper.get_files_if_exist_in_corpus(matched_files, files_in_corpus)
                    self.writeResults.write_file_list_each_row(bug_id, matched_files, matched_files_stored_filename,
                                                               "All")

                not_matched_files = self.codeMappingHelper.get_files_that_not_exist_in_first_list(files_in_corpus,
                                                                                                  matched_files)
                self.writeResults.write_file_list_each_row(bug_id, not_matched_files, not_matched_files_stored_filename,
                                                           "NotMatched")


if __name__ == '__main__':
    parser = argparse.ArgumentParser()

    parser.add_argument('-c', '--corpus', help='Description for boosting', required=True)
    parser.add_argument('-q', '--query', help='Description for query', required=False)
    parser.add_argument('-r', '--result', help='Description for results', required=True)
    parser.add_argument('-s', '--screens', help='Number of screens', required=True)
    parser.add_argument('-ops', '--operations', help='Operations', required=True)
    parser.add_argument('-en', '--exp_name', help='Experiment Name', required=True)
    args = vars(parser.parse_args())

    sourcodeCodeMapping = SoureCodeMapping(args)
    sourcodeCodeMapping.main()
