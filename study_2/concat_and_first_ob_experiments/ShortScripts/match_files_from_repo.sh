#! /bin/bash
: '
List of experiments:
1. Concat-OB-3-Screens: Conduct Bug Localization (Study 2) using top 3 screens from Buggy UI Localization (Study 1) with concat OB
2. Concat-OB-4-Screens: Conduct Bug Localization (Study 2) using top 4 screens from Buggy UI Localization (Study 1) with concat OB
3. First-OB-3-Screens: Conduct Bug Localization (Study 2) using top 3 screens from Buggy UI Localization (Study 1) with first OB
4. First-OB-4-Screens: Conduct Bug Localization (Study 2) using top 4 screens from Buggy UI Localization (Study 1) with first OB
'

exp_names=("Concat-OB-3-Screens" "Concat-OB-4-Screens" "First-OB-3-Screens" "First-OB-4-Screens")

# TODO: Copy the absolute path of study_2/data/BuggyProjects and replace it in the buggy_project_dir variable
export buggy_project_dir=/Volumes/Education/GitHub/Toward-Automating-the-Localization-of-Buggy-UIs-Anonymized/study_2/data/BuggyProjects
# TODO: Copy the absolute path of study_2/data/BuggyProjects and replace it in the buggy_project_dir_in_csv variable
export buggy_project_dir_in_csv=/Volumes/Education/GitHub/Toward-Automating-the-Localization-of-Buggy-UIs-Anonymized/study_2/data/BuggyProjects

corpus_types=("All_Java_Files" "GUI_State_and_All_GUI_Component_IDs" "All_GUI_Component_IDs"
"GUI_States" "Interacted_GUI_Component_IDs" "GUI_State_and_Interacted_GUI_Component_IDs")
boosted_queries=("GUI_States" "Interacted_GUI_Component_IDs" "GUI_State_and_Interacted_GUI_Component_IDs"
"All_GUI_Component_IDs" "GUI_State_and_All_GUI_Component_IDs")


# shellcheck disable=SC2068
for exp_name in ${exp_names[@]}; do
  if [[ $exp_name == "Concat-OB-3-Screens" ]]; then
    screens=("3")
  elif [[ $exp_name == "Concat-OB-4-Screens" ]]; then
    screens=("4")
  elif [[ $exp_name == "First-OB-3-Screens" ]]; then
    screens=("3")
  elif [[ $exp_name == "First-OB-4-Screens" ]]; then
    screens=("4")
  fi

  export filtering_boosting_filenames=../FilteredUnfilteredFiles/$exp_name
  export filtered_boosted_files_in_repo=../FilteredBoostedProjects/$exp_name
  rm -rf $filtered_boosted_files_in_repo
  mkdir -p $filtered_boosted_files_in_repo

  for i in ${!screens[@]}; do
    for j in ${!corpus_types[@]}; do
      for k in ${!boosted_queries[@]}; do
        python3 match_files_from_repo.py -s ${screens[$i]} \
        -c ${corpus_types[$j]} -q ${boosted_queries[$k]} \
        -bpd $buggy_project_dir -fbfile $filtering_boosting_filenames \
        -fbr $filtered_boosted_files_in_repo -bpdcsv $buggy_project_dir_in_csv -ops Filtering+Boosting -en $exp_name
      done
      python3 match_files_from_repo.py -s ${screens[$i]} \
        -c ${corpus_types[$j]} \
        -bpd $buggy_project_dir -fbfile $filtering_boosting_filenames \
        -fbr $filtered_boosted_files_in_repo -bpdcsv $buggy_project_dir_in_csv -ops Filtering -en $exp_name
    done
  done
done