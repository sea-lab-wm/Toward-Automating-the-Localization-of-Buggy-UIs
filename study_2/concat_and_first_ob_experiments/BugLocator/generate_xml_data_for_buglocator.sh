#! /bin/bash
: '
List of experiments:
1. Concat-OB-3-Screens: Conduct Bug Localization (Study 2) using top 3 screens from Buggy UI Localization (Study 1) with concat OB
2. Concat-OB-4-Screens: Conduct Bug Localization (Study 2) using top 4 screens from Buggy UI Localization (Study 1) with concat OB
3. First-OB-3-Screens: Conduct Bug Localization (Study 2) using top 3 screens from Buggy UI Localization (Study 1) with first OB
4. First-OB-4-Screens: Conduct Bug Localization (Study 2) using top 4 screens from Buggy UI Localization (Study 1) with first OB
'

exp_names=("Concat-OB-3-Screens" "Concat-OB-4-Screens" "First-OB-3-Screens" "First-OB-4-Screens")
export query_reformulation_list=("GUI_States" "Interacted_GUI_Component_IDs" "GUI_State_and_Interacted_GUI_Component_IDs"
	"All_GUI_Component_IDs" "GUI_State_and_All_GUI_Component_IDs")
export jsonFilePath=../../data/JSON-Files-All


# shellcheck disable=SC2068
for exp_name in ${exp_names[@]}; do
  if [[ $exp_name == "Concat-OB-3-Screens" ]]; then
    screen_list=("3")
  elif [[ $exp_name == "Concat-OB-4-Screens" ]]; then
    screen_list=("4")
  elif [[ $exp_name == "First-OB-3-Screens" ]]; then
    screen_list=("3")
  elif [[ $exp_name == "First-OB-4-Screens" ]]; then
    screen_list=("4")
  fi

  export preprocessedDataPath=../../data/PreprocessedData/$exp_name
  export generated_data_path=../../data/PreprocessedData/BugLocatorQuery-$exp_name
  rm -rf $generated_data_path
  mkdir $generated_data_path

  # shellcheck disable=SC2068
  for k in ${!query_reformulation_list[@]}; do
    for l in ${!screen_list[@]}; do
      echo "Generate Data: Q-${query_reformulation_list[$k]}#S-${screen_list[$l]}"
      python3 generateXMLData.py \
        -q ${query_reformulation_list[$k]} -s ${screen_list[$l]}  \
              -prep_data $preprocessedDataPath -gen_data $generated_data_path \
              -jpath $jsonFilePath
    done
  done
done