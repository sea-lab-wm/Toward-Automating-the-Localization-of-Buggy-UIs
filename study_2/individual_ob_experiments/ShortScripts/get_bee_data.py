import requests
import json
import pandas as pd
import re
import xml.etree.ElementTree as ET
import os

def get_labels_from_text(text, values):
    #pattern = re.compile(r"[.] ")
    #text_splits = pattern.split(text)
    text_splits = text.split(". ", 1)
    for key, value in values.items():
        if len(text_splits)>1 and value['text'] == text_splits[1]:
            return value['labels']
        if value['text']==text:
            return value['labels']
    # print('tr')
    # print(text)
    # print(text_splits)
    return -1

# bug_ids = ["2", "8", "10", "18", "19", "21", "44", "53", "71", "117", "128", "129", "130",
                # "135", "191", "201", "206", "209", "256", "1066", "1067", "1073", "1096", "1145", "1146",
                # "1147", "1149", "1151", "1152", "1202", "1205", "1207", "1214", "1215", "1223", "1224",
                # "1226", "1299", "1399", "1406", "1430", "1441", "1445", "1481", "1645"]

bug_ids = ["130"]

for bug_id in bug_ids:
    #Read text file
    with open('BugLocalization/FaultLocalizationCode/data/BugReports/bug_report_' + bug_id + '.txt') as f:
        contents = f.read()
        #print(contents)

    #Read XML file
    # with open('BugLocalization/FaultLocalizationCode/data/BugReportsMarked/ParsedBugReports/Bug2/familyfinance#1.5.5-Debug.xml', 'r') as f:
    #     parsed_xml = f.read()

    api_url = "http://rocco.cs.wm.edu:21203/api"
    myobj = {"text":contents}

    response = requests.post(api_url, json=myobj)
    print(response)

    json_data = response.json()
    print(json_data)

    df = pd.DataFrame(json_data)
    bee_values = df['bug_report']

    #print(values)

    # for key, value in bee_values.items():
    #     print(value['text'])
    #     print(value['labels'])

    #print(parsed_xml)
    parsed_bug_report_dir = 'BugLocalization/FaultLocalizationCode/data/BugReportsMarked/ParsedBugReports/Bug' + bug_id

    input_xml = ""
    for filename in os.listdir(parsed_bug_report_dir):
        if filename.endswith('.xml'):
            input_xml = os.path.join(parsed_bug_report_dir, filename)

    tree = ET.parse(input_xml)
    root = tree.getroot()

    for item in root.iter('title'):
        labels = get_labels_from_text(item.text, bee_values)
        if labels == -1:
            print(bug_id)
            print(item.text)
            continue
        for label in labels:
            if label == 'OB':
                item.attrib['ob'] = 'x'
            elif label == 'EB':
                item.attrib['eb'] = 'x'
            elif label == 'SR':
                item.attrib['sr'] = 'x'

    for item in root.iter('st'):
        #print(item.text)
        labels = get_labels_from_text(item.text, bee_values)
        #print(labels)
        if labels == -1:
            print(bug_id)
            print(item.text)
            continue
        for label in labels:
            if label == 'OB':
                item.attrib['ob'] = 'x'
            elif label == 'EB':
                item.attrib['eb'] = 'x'
            elif label == 'SR':
                item.attrib['sr'] = 'x'

    output_dir = 'Test/Bug' + bug_id

    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    tree.write(output_dir + '/familyfinance#1.5.5-Debug.xml')

