#! /bin/bash
export JAVA_HOME=`/usr/libexec/java_home -v 1.11`
mvn package -DskipTests

: '
List of experiments:
1. Individual-OB-3-Screens: Conduct Bug Localization (Study 2) using top 3 screens from Buggy UI Localization (Study 1) with individual OB
2. Individual-OB-4-Screens: Conduct Bug Localization (Study 2) using top 4 screens from Buggy UI Localization (Study 1) with individual OB
'

exp_names=("Individual-OB-3-Screens" "Individual-OB-4-Screens")

export content_types=("Content" "Code")

# shellcheck disable=SC2068
for exp_name in ${exp_names[@]}; do
  if [[ $exp_name == "Individual-OB-3-Screens" ]]; then
    screens=("3")
  elif [[ $exp_name == "Individual-OB-4-Screens" ]]; then
    screens=("4")
  fi

  for content_type in ${content_types[@]}; do
    if [[ "$content_type" == "Content" ]]; then
      corpus_type=GUI_State_and_All_GUI_Component_IDs
      #bug_reports_folder=BugLocalization/FaultLocalizationCode/data/BugReports
      #bug_reports_folder=BugLocalization/FaultLocalizationCode/data/BugReportsContents
      # TODO: Copy the absolute path of FilteredUnfilteredFiles folder and replace it in the query_infos_file variable. Keep the "/$exp_name" at the end.
      query_infos_file=/Volumes/Education/GitHub/Toward-Automating-the-Localization-of-Buggy-UIs-Anonymized/study_2/individual_ob_experiments/FilteredUnfilteredFiles/$exp_name
      # TODO: Copy the absolute path of study_2/data/PreprocessedData and replace it in the preprocessed_query_folder variable. Keep the "/$exp_name/Query" at the end.
      preprocessed_query_folder=/Volumes/Education/GitHub/Toward-Automating-the-Localization-of-Buggy-UIs-Anonymized/study_2/data/PreprocessedData/$exp_name/Query
      rm -rf ${preprocessed_query_folder}
      mkdir -p ${preprocessed_query_folder}

      java -cp target/code_search_ir-1.0.jar MainClass \
        -qinfo ${query_infos_file} -s ${screens} -c ${corpus_type} -preq ${preprocessed_query_folder} \
        -ctype ${content_type}

    elif [[ "${content_type}" == "Title" ]];
    then
      # TODO: Copy the absolute path of study_2/data/BugReportsTitles and replace it in the bug_reports_titles variable.
      bug_reports_titles=/Volumes/Education/GitHub/Toward-Automating-the-Localization-of-Buggy-UIs-Anonymized/study_2/data/BugReportsTitles
      # TODO: Copy the absolute path of study_2/data/PreprocessedData and replace it in the preprocessed_title_folder variable. Keep the "/$exp_name/Title" at the end.
      preprocessed_titles_folder=/Volumes/Education/GitHub/Toward-Automating-the-Localization-of-Buggy-UIs-Anonymized/study_2/data/PreprocessedData/$exp_name/Title
      rm -rf ${preprocessed_titles_folder}
      mkdir -p ${preprocessed_titles_folder}

      for i in ${!screens[@]}; do
        java -cp target/code_search_ir-1.0.jar MainClass -br ${bug_reports_titles} \
        -s ${screens[$i]} -preq ${preprocessed_titles_folder} \
        -ctype ${content_type}
      done
    elif [[ "${content_type}" == "Code" ]];
    then
      corpus_type=GUI_State_and_All_GUI_Component_IDs
      # TODO: Copy the absolute path of study_2/data/PreprocessedData and replace it in the preprocessed_code_folder variable. Keep the "/$exp_name/Code" at the end.
      preprocessed_code_folder=/Volumes/Education/GitHub/Toward-Automating-the-Localization-of-Buggy-UIs-Anonymized/study_2/data/PreprocessedData/$exp_name/Code
      rm -rf ${preprocessed_code_folder}
      mkdir -p ${preprocessed_code_folder}
      # TODO: Copy the absolute path of data/BuggyProjects and replace it in the buggy_projects variable.
      buggy_projects=/Volumes/Education/GitHub/Toward-Automating-the-Localization-of-Buggy-UIs-Anonymized/study_2/data/BuggyProjects

      java -cp target/code_search_ir-1.0.jar MainClass \
        -preq ${preprocessed_code_folder} -bp ${buggy_projects} -qinfo ${query_infos_file} -c ${corpus_type} \
        -ctype ${content_type} -s ${screens}

    fi
  done
done