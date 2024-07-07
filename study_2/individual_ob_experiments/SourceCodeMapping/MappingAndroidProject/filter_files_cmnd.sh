: '
python get_filtered_unfiltered_files.py -s <number_of_screens> -c <corpus> -q <files_to_boost> -r <result_folder>

List of experiments:
1. Individual-OB-3-Screens: Conduct Bug Localization (Study 2) using top 3 screens from Buggy UI Localization (Study 1) with individual OB
2. Individual-OB-4-Screens: Conduct Bug Localization (Study 2) using top 4 screens from Buggy UI Localization (Study 1) with individual OB

Here, 
number_of_screens = Number of screens, n to get activities/fragments and we will have (n-1) interacted component ids
	Possible values: 4 or 3 or 2

corpus = Type of corpus we will use
	Possible values: 
	All_Java_Files or GUI_State_and_All_GUI_Component_IDs or All_GUI_Component_IDs or GUI_States 
	or Interacted_GUI_Component_IDs or GUI_State_and_Interacted_GUI_Component_IDs

query = Files that we boost based on matching query
	Possible values:
	GUI_State_and_Interacted_GUI_Component_IDs or GUI_States or GUI_State_and_All_GUI_Component_IDs 
	or Interacted_GUI_Component_IDs or All_GUI_Component_IDs

results = Folder where we will store the results 
'
exp_names=("Individual-OB-3-Screens" "Individual-OB-4-Screens")
corpus_types=("All_Java_Files" "GUI_State_and_All_GUI_Component_IDs" "All_GUI_Component_IDs" "GUI_States")
boosted_queries=("GUI_States" "All_GUI_Component_IDs" "GUI_State_and_All_GUI_Component_IDs")

# shellcheck disable=SC2068
for exp_name in ${exp_names[@]}; do
  if [[ $exp_name == "Individual-OB-3-Screens" ]]; then
    screens=("3")
  elif [[ $exp_name == "Individual-OB-4-Screens" ]]; then
    screens=("4")
  fi

  results_folder=../../FilteredUnfilteredFiles/$exp_name
  rm -rf ${results_folder}
  mkdir ${results_folder}

  # TODO: Copy the absolute path of study_2/data/BuggyProjects and replace it with buggy_project_dir
  buggy_project_dir=/Volumes/Education/GitHub/Toward-Automating-the-Localization-of-Buggy-UIs-Anonymized/study_2/data/BuggyProjects
  trace_replacer_data_dir=../../../data/TraceReplayer-Data

  for i in ${!screens[@]}; do
    for j in ${!corpus_types[@]}; do
      for k in ${!boosted_queries[@]}; do
        python3 get_filtered_unfiltered_files.py -s ${screens[$i]} \
        -c ${corpus_types[$j]} -q ${boosted_queries[$k]} -r ${results_folder} \
        -ops Filtering+Boosting -bpr ${buggy_project_dir} -tr ${trace_replacer_data_dir} -en ${exp_name}
      done
      python3 get_filtered_unfiltered_files.py -s ${screens[$i]} \
        -c ${corpus_types[$j]} -r ${results_folder} \
        -ops Filtering -bpr ${buggy_project_dir} -tr ${trace_replacer_data_dir} -en ${exp_name}
    done
  done
done

