import glob
import pandas as pd
import os, shutil

def get_cur_java_files(parent_directory):
    all_files = []
    
    for filename in sorted(glob.glob(f'{parent_directory}/**/*.java', recursive = True)):
        all_files.append(filename)

    return all_files

def get_all_java_files(bug_id):
    parent_directory = "Files/bug-" + bug_id
    all_java_files = get_cur_java_files(parent_directory)

    return all_java_files

bug_ids_states = [("2",41), ("8",14), ("10",15), ("18",21), ("19",5), ("44",21),
                ("53",18), ("71",10), ("117",11), ("128",28), ("129", 33), ("130",2),
                ("135",14), ("191",1), ("201",11), ("206",14), ("209",50), ("256",19),
                ("1073",8), ("1096",14), ("1146",6),
                ("1147",7), ("1151",5), ("1202",11), ("1205",22), ("1207",13),
                ("1214",13), ("1215",31), ("1223",81), ("1224",39),
                ("1299",20), ("1399",14), ("1406",20), ("1430",21), ("1441",18),
                ("1445",14), ("1481",16), ("1645",6),
                #new ones
                ("45",22), ("54",10), ("76",6), ("92",4), ("101",8),("106",11),("110",5),
                ("158",10), ("160",14), ("162",6), ("168",3), ("192",12),("199",11),
                ("200",9), ("248",45), ("1150",11), ("1198",20),
                ("1228",24),("1389",2),("1425",18),("1446",18),("1563",7),("1568",8),("1641",9)]

#bug_ids_states = [("2",41)]


def delete_files_filtered(all_java_files, filtered_files):
    for file in all_java_files:
        if file in filtered_files:
            #print(file)
            new_path = file.replace("BuggyProjectsBL", "Boosting-GUI+Interacted-Projects/BuggyProjectsQuery")
            print(new_path)
            try:
                shutil.copy(file, new_path)
            except IOError as io_err:
                os.makedirs(os.path.dirname(new_path))
                shutil.copy(file, new_path)

def delete_files_unfiltered(all_java_files, filtered_files):
    for file in all_java_files:
        if file in filtered_files:
            #print(file)
            new_path = file.replace("BuggyProjectsBL", "Boosting-GUI+Interacted-Projects/BuggyProjectsNotQuery")
            print(new_path)
            try:
                shutil.copy(file, new_path)
            except IOError as io_err:
                os.makedirs(os.path.dirname(new_path))
                shutil.copy(file, new_path)
total = 0
for issue_id, app_final_state in bug_ids_states:
    bug_id = issue_id
    print("Bug Id: " + bug_id)

    all_java_files = get_all_java_files(bug_id)
    print("Count java files: " + str(len(all_java_files)))
    total += len(all_java_files)
    #print(all_java_files)
    # filtered_files = get_filtered_files(bug_id)
    # #print(filtered_files)
    # unfiltered_files = get_unfiltered_files(bug_id)

    # delete_files_filtered(all_java_files, filtered_files)
    # delete_files_unfiltered(all_java_files, unfiltered_files)

print("Total: " + str(total))