import xml.etree.ElementTree as ET
from xml.dom import minidom
import os
import re
import json
import argparse


def read_file_content(file_name):
    file_content = open(file_name, "r")
    file_content = file_content.read()
    return file_content


def get_preprocessed_contents(bug_id, preprocessed_data_path, content_name_dir, content_subpath,
                              query_reformulation_type, filename_prefix):
    if query_reformulation_type == "":
        content_file = preprocessed_data_path + "/" + content_name_dir + "/" + content_subpath + "/" + filename_prefix + bug_id + ".txt"
    else:
        content_file = preprocessed_data_path + "/" + content_name_dir + "/" + content_subpath + "/" + query_reformulation_type + "/" + filename_prefix + bug_id + ".txt"
    contents = read_file_content(content_file)

    if contents is None or len(contents) < 1:
        return ""
    return contents


def create_filepath(file):
    os.makedirs(os.path.dirname(file), exist_ok=True)


def create_xml_file(bug_id, bug_report_title, bug_report_content, xml_file, json_filepath):
    root = ET.Element("bugrepository", name="androR2")
    bug = ET.SubElement(root, "bug", id=str(bug_id), opendate="", fixdate="")
    buginformation = ET.SubElement(bug, "buginformation")

    summary = ET.SubElement(buginformation, "summary")
    summary.text = bug_report_title

    description = ET.SubElement(buginformation, "description")
    description.text = bug_report_content

    json_file = json_filepath + "/" + str(bug_id) + ".json"
    with open(json_file, 'r') as jfile:
        json_data = jfile.read()
    original_buggy_locations = json.loads(json_data)

    buggy_file_list = []
    for file_location in original_buggy_locations["bug_location"]:
        bug_location = file_location["file_name"]
        buggy_file_list.append(bug_location)
    buggy_unique_file_list = list(set(buggy_file_list))

    fixed_files = ET.SubElement(bug, "fixedFiles")
    for bug_location in buggy_unique_file_list:
        bug_file_words = bug_location.split("/")
        flag = False
        file_name = ""
        word_list = []
        for words in bug_file_words:
            if flag:
                word_list.append(words)
            if words == "java":
                flag = True

        if flag:
            bug_file = ET.SubElement(fixed_files, "file")
            file_name = '.'.join(word_list)
            bug_file.text = file_name

    xmlstr = minidom.parseString(ET.tostring(root)).toprettyxml(indent="   ")
    with open(xml_file, "w") as f:
        f.write(xmlstr)


def main(args):
    if args['screen'] == "3":
        bug_issue_ids = ["2", "1028", "8", "10", "11", "18", "19", "1563", "1568", "44", "45", "1073", "53", "54", "55",
                         "56", "1089", "71", "1096", "76", "84", "87", "92", "1640", "1641", "106", "1130", "1645", "110",
                         "117", "1146", "1147", "1150", "1151", "128", "129", "130", "135", "158", "159", "160", "162",
                         "168", "1198", "1202", "1205", "1207", "1213", "1214", "191", "192", "193", "1215", "1222", "199",
                         "200", "201", "1223", "1224", "1228", "206", "209", "248", "256", "271", "275", "1299", "1389",
                         "1399", "1402", "1403", "1406", "1425", "1428", "1430", "1441", "1445", "1446", "1481"]
    elif args['screen'] == "4":
        bug_issue_ids = ["2", "8", "10", "18", "19", "44", "53", "117", "128", "129", "130", "135", "206", "209", "256", "1073", "1096", "1202", "1207", "1214", "1215", "1224", "1299", "1399", "1430", "1441", "1481", "1645", "54", "76", "92", "158", "160", "162", "192", "199", "200", "1150", "1198", "1228", "1389", "1425", "1446", "1568", "71", "201", "1146", "1147", "1151", "1205", "1406", "1445", "45", "106", "110", "168", "248", "1563", "1223", "1641", "55", "56", "1213", "1222", "1428", "84", "87", "159", "193", "271", "275", "1028", "1089", "1130", "1402", "1403", "1640"]

    title_subpath = "Screen-" + args['screen'] + "/bug_report_titles"
    content_subpath = "Screen-" + args['screen'] + "/Preprocessed_with_" + args['query_reformulation']

    for bug_id in bug_issue_ids:
        bug_report_title = get_preprocessed_contents(bug_id, args['preprocessed_data'], "Title", title_subpath, "",
                                                     "bug_title_")

        bug_report_content = get_preprocessed_contents(bug_id, args['preprocessed_data'], "Content", content_subpath,
                                                       "bug_report_original", "bug_report_")

        orginal_xml_file = args[
                               'generated_data'] + "/" + content_subpath + "/bug_report_original" + "/bug_" + bug_id + ".xml"
        create_filepath(orginal_xml_file)
        create_xml_file(bug_id, bug_report_title, bug_report_content, orginal_xml_file, args['json_file_path'])

        replaced_query = get_preprocessed_contents(bug_id, args['preprocessed_data'], "Content", content_subpath,
                                                   "replaced_query", "bug_report_")
        replaced_query_xml_file = args[
                                      'generated_data'] + "/" + content_subpath + "/replaced_query" + "/bug_" + bug_id + ".xml"
        create_filepath(replaced_query_xml_file)
        create_xml_file(bug_id, "", replaced_query, replaced_query_xml_file, args['json_file_path'])

        query_expansion1 = get_preprocessed_contents(bug_id, args['preprocessed_data'], "Content", content_subpath,
                                                     "query_expansion_1", "bug_report_")
        query_expansion1_xml_file = args[
                                        'generated_data'] + "/" + content_subpath + "/query_expansion_1" + "/bug_" + bug_id + ".xml"
        create_filepath(query_expansion1_xml_file)
        create_xml_file(bug_id, bug_report_title, query_expansion1, query_expansion1_xml_file, args['json_file_path'])


if __name__ == "__main__":
    parser = argparse.ArgumentParser()

    parser.add_argument('-q', '--query_reformulation', help='Description for query reformuation', required=True)
    parser.add_argument('-s', '--screen', help='Number of screens', required=True)
    parser.add_argument('-prep_data', '--preprocessed_data', help='Preprocessed data', required=True)
    parser.add_argument('-gen_data', '--generated_data', help='The path where the generated data will be saved',
                        required=True)
    parser.add_argument('-jpath', '--json_file_path', help='Preprocessed data', required=True)

    args = vars(parser.parse_args())
    main(args)
