import copy
import json
import os
import pandas as pd
import sys


# These 79 bugs are used in the bug localization study (study_2).
bugs_used_in_bl = [2, 8, 10, 11, 18, 19, 44, 45, 53, 54, 55, 56, 71, 76, 84, 87, 92, 106, 110, 117, 128,
                   129, 130, 135, 158, 159, 160, 162, 168, 191, 192, 193, 199, 200, 201, 206, 209, 248,
                   256, 271, 275, 1028, 1073, 1089, 1096, 1130, 1146, 1147, 1150, 1151, 1198, 1202, 1205,
                   1207, 1213, 1214, 1215, 1222, 1223, 1224, 1228, 1299, 1389, 1399, 1402, 1403, 1406,
                   1425, 1428, 1430, 1441, 1445, 1446, 1481, 1563, 1568, 1640, 1641, 1645]


def split_to_int_list(string_data):
    if pd.isna(string_data):
        return []

    return [(int(x.strip()) if x.strip().isdigit() else x.strip()) for x in string_data.split(",")]


def write_data(ob_dir, query_data, output_file):
    query_data_path = os.path.join(ob_dir, output_file)
    with open(query_data_path, "w") as f:
        json.dump(query_data, f, indent=4)


if __name__ == "__main__":
    base_path = "real_data_construction"

    output_dir = os.path.join(base_path, "real_data")
    screen_components_dir = os.path.join(output_dir, "screen_components")
    screen_images_dir = os.path.join(output_dir, "screen_images")
    ob_dir = os.path.join(output_dir, "ob")
    component_images_dir = os.path.join(output_dir, "component_images")

    # ----------------------------------

    ground_truth_path = os.path.join(
        base_path, "Real dataset - ground truth  - Data.csv")

    ground_truth_data = pd.read_csv(ground_truth_path)

    # select only the columns we need
    ground_truth_data = ground_truth_data[["done", "Bug Report ID", "OB ID", "OB XML ID", "OB", "Bug type (final)",
                                           "OB category (final)", "OB Rating (Final)", "Screens (Causing) Final",
                                           "Components (Causing) Final", "Screens (Manifesting) Final",
                                           "Components (Manifesting) Final"]]

    # print head rows
    print(ground_truth_data.head())

    query_data = {}
    previous_ob_record = None

    # loop through each row
    for index, row in ground_truth_data.iterrows():
        # ------------------------------------------------
        # get the bug report id
        bug_report_id = row["Bug Report ID"]
        # get the ob id
        ob_id = row["OB ID"]
        # get the ob xml id
        ob_xml_id = row["OB XML ID"]
        # get the ob text
        ob_text = row["OB"]
        # get the bug type
        bug_type = row["Bug type (final)"]
        # get the ob category
        ob_category = row["OB category (final)"]
        # get the ob rating
        ob_rating = row["OB Rating (Final)"]
        # get the screen id causing
        screen_id_causing = row["Screens (Causing) Final"]
        # get the component ids causing
        component_ids_causing = row["Components (Causing) Final"]
        # get the screen id manifesting
        screen_id_manifesting = row["Screens (Manifesting) Final"]
        # get the component ids manifesting
        component_ids_manifesting = row["Components (Manifesting) Final"]

        if screen_id_causing == "None" and component_ids_causing == "None":
            continue

        # ------------------------------------------------

        if not pd.isna(bug_report_id):
            bug_report_id = "Bug" + str(int(bug_report_id))

        # if ob_id is not NA, convert to int
        if not pd.isna(ob_id):
            ob_id = int(ob_id)

        # if ob_rating is not NA, convert to int
        if not pd.isna(ob_rating):
            ob_rating = int(ob_rating)

        # if ob_xml_id is not NA, convert to int
        if not pd.isna(ob_xml_id):
            ob_xml_id = int(ob_xml_id)

        # ------------------------------------------------

        # add the bug id to the query data
        if not pd.isna(bug_report_id) and bug_report_id not in query_data:
            query_data[bug_report_id] = {}

        # ------------------------------------------------

        # set the current ob record
        current_ob_record = {
            "done": False if pd.isna(row["done"]) else True,
            "bug_report_id": bug_report_id,
            "ob_id": ob_id,
            "ob_xml_id": ob_xml_id,
            "ob_in_title": 1 if 0 == ob_xml_id else 0,
            "bug_type": bug_type,
            "ob_category": ob_category,
            "ob_rating": ob_rating,
            "ob_text": ob_text,
            "screen_id_causing": screen_id_causing,
            "component_ids_causing": component_ids_causing,
            "screen_id_manifesting": screen_id_manifesting,
            "component_ids_manifesting": component_ids_manifesting
        }

        # if it is not the first record
        if previous_ob_record is not None:

            # if the current bug id is NA, then we use the previous record, and change the screen/component data
            if pd.isna(bug_report_id):
                current_ob_record = previous_ob_record.copy()

                # set the screen id causing, component ids causing, screen id manifesting, component ids manifesting
                current_ob_record["screen_id_causing"] = screen_id_causing
                current_ob_record["component_ids_causing"] = component_ids_causing
                current_ob_record["screen_id_manifesting"] = screen_id_manifesting
                current_ob_record["component_ids_manifesting"] = component_ids_manifesting

        # ------------------------------------------------

        if current_ob_record["done"]:

            if current_ob_record["ob_id"] not in query_data[current_ob_record["bug_report_id"]]:
                # if current_ob_record["screen_id_causing"] != current_ob_record["screen_id_causing"]:
                #     continue
                # if current_ob_record["screen_id_manifesting"] != current_ob_record["screen_id_manifesting"]:
                #     continue

                ob_data = {
                    "done": current_ob_record["done"],
                    "ob_id": current_ob_record["ob_id"],
                    "ob_xml_id": ob_xml_id,
                    "ob_in_title": 1 if 0 == ob_xml_id else 0,
                    "bug_type": current_ob_record["bug_type"],
                    "ob_category": current_ob_record["ob_category"],
                    "ob_rating": current_ob_record["ob_rating"],
                    "ob_text": current_ob_record["ob_text"],
                    "screens_causing": [
                        {
                            "screen_id_causing": current_ob_record["screen_id_causing"],
                            "component_ids_causing": split_to_int_list(current_ob_record["component_ids_causing"])
                        }
                    ],
                    "screens_manifesting": [
                        {
                            "screen_id_manifesting": current_ob_record["screen_id_manifesting"],
                            "component_ids_manifesting": split_to_int_list(current_ob_record["component_ids_manifesting"])
                        }
                    ]
                }

                query_data[bug_report_id][current_ob_record["ob_id"]] = ob_data
            else:
                ob_data = query_data[current_ob_record["bug_report_id"]][current_ob_record["ob_id"]]

                # if current_ob_record["screen_id_causing"] != current_ob_record["screen_id_causing"]:
                #     continue
                ob_data["screens_causing"].append(
                    {
                        "screen_id_causing": current_ob_record["screen_id_causing"],
                        "component_ids_causing": split_to_int_list(current_ob_record["component_ids_causing"])
                    }
                )

                # if current_ob_record["screen_id_manifesting"] != current_ob_record["screen_id_manifesting"]:
                #     continue
                ob_data["screens_manifesting"].append(
                    {
                        "screen_id_manifesting": current_ob_record["screen_id_manifesting"],
                        "component_ids_manifesting": split_to_int_list(current_ob_record["component_ids_manifesting"])
                    }
                )

        # ------------------------------------------------

        previous_ob_record = current_ob_record

    # ------------------------------------------------

    # write the query data to json file
    write_data(ob_dir, query_data, "raw-obs.json")

    # ------------------------------------------------

    # write the query data considering both causing and manifesting screen/components for app crash type bugs and
    # manifesting screen/components for others
    for bug_report_id, bug_data in query_data.items():
        for ob_id, ob_data in bug_data.items():

            new_screens = []

            # Add manifesting screens
            for screen in ob_data["screens_manifesting"]:
                # Skip if the screen is NaN
                if screen["screen_id_manifesting"] != screen["screen_id_manifesting"]:
                    continue
                new_screen = {
                    "screen_id": screen["screen_id_manifesting"],
                    "components": sorted(screen["component_ids_manifesting"])
                }
                new_screens.append(new_screen)

            new_screen_ids = [s["screen_id"] for s in new_screens]

            if ob_data["bug_type"] == "App crashes":
                # Add causing screens
                for screen in ob_data["screens_causing"]:
                    # Skip if the screen is NaN
                    if screen["screen_id_causing"] != screen["screen_id_causing"]:
                        continue
                    # Add the causing screen components to the manifesting screen components if the screen exists in the
                    # new_screens list
                    for s in new_screens:
                        # print(s["screen_id"], screen["screen_id_manifesting"])
                        if screen["screen_id_causing"] == s["screen_id"]:
                            # Append the manifesting components to the causing components
                            s["components"] += screen["component_ids_causing"]
                            # Remove duplicates
                            s["components"] = sorted(list(set(s["components"])))

                    # Add the manifesting screen with components if it doesn't exist in the new_screens list
                    if screen["screen_id_causing"] not in new_screen_ids:
                        new_screen = {
                            "screen_id": screen["screen_id_causing"],
                            "components": sorted(screen["component_ids_causing"])
                        }
                        new_screens.append(new_screen)

            query_data[bug_report_id][ob_id]["screens"] = new_screens

            # Delete the causing and manifesting screen dicts from the ob data
            del ob_data["screens_causing"]
            del ob_data["screens_manifesting"]

    write_data(ob_dir, query_data, "obs.json")

    # ------------------------------------------------

    # write the concatenated query data for crash OBs
    concatenated_query_data = {}
    for bug_report_id, bug_data in query_data.items():
        bug_id = int(bug_report_id.replace("Bug", ""))
        if bug_id not in bugs_used_in_bl:
            continue

        new_ob_id = ""
        concatenated_ob_text = ""
        for ob_id, ob_data in bug_data.items():

            new_ob_id += str(ob_id)
            concatenated_ob_text = concatenated_ob_text + " " + ob_data["ob_text"]
            ob_in_title = ob_data["ob_in_title"]
            bug_type = ob_data["bug_type"]
            ob_category = ob_data["ob_category"]
            ob_rating = ob_data["ob_rating"]
            screens = ob_data["screens"]

        # print(bug_report_id)
        # print(new_ob_id, concatenated_ob_text)

        new_ob_data = {
            "ob_id": new_ob_id,
            "ob_in_title": ob_in_title,
            "bug_type": bug_type,
            "ob_category": ob_category,
            "ob_rating": ob_rating,
            "ob_text": concatenated_ob_text,
            "screens": screens
        }
        new_query_dict = {new_ob_id: new_ob_data}

        concatenated_query_data[bug_report_id] = new_query_dict

    # print(len(concatenated_query_data))
    write_data(ob_dir, concatenated_query_data, "concat-obs.json")
