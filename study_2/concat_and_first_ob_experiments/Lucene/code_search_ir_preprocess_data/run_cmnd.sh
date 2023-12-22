#! /bin/bash
export JAVA_HOME=$(/usr/libexec/java_home)
mvn package -DskipTests

: '
List of experiments:
1. Concat-OB-3-Screens: Conduct Bug Localization (Study 2) using top 3 screens from Buggy UI Localization (Study 1) with concat OB
2. Concat-OB-4-Screens: Conduct Bug Localization (Study 2) using top 4 screens from Buggy UI Localization (Study 1) with concat OB
3. First-OB-3-Screens: Conduct Bug Localization (Study 2) using top 3 screens from Buggy UI Localization (Study 1) with first OB
4. First-OB-4-Screens: Conduct Bug Localization (Study 2) using top 4 screens from Buggy UI Localization (Study 1) with first OB
'

exp_names=("Concat-OB-3-Screens" "Concat-OB-4-Screens" "First-OB-3-Screens" "First-OB-4-Screens")

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

  model_names=("BugLocator" "LUCENE")

  for model_name in ${model_names[@]}; do
    if [[ "${model_name}" == "BugLocator" ]]; then
      export content_types=("Title" "Content")
    elif [[ "${model_name}" == "LUCENE" ]]; then
      export content_types=("Content" "Code")
    fi
    for content_type in ${content_types[@]}; do
      if [[ "$content_type" == "Content" ]]; then

        corpus_type=GUI_State_and_All_GUI_Component_IDs
        if [[ "${model_name}" == "BugLocator" ]]; then
          # TODO: Provide the absolute path of data/BugReportsContents
          bug_reports_folder=X/Y/Z/data/BugReportsContents
        elif [[ "${model_name}" == "LUCENE" ]]; then
          # TODO: Provide the absolute path of data/BugReports
          bug_reports_folder=X/Y/Z/data/BugReports
        fi

        # TODO: Provide the absolute path of FilteredUnfilteredFiles
        query_infos_file=X/Y/Z/FilteredUnfilteredFiles/$exp_name

        if [[ "${model_name}" == "BugLocator" ]]; then
          # TODO: Provide the absolute path of data/PreprocessedData
          preprocessed_query_folder=X/Y/Z/data/PreprocessedData/$exp_name/Content
        elif [[ "${model_name}" == "LUCENE" ]]; then
          # TODO: Provide the absolute path of data/PreprocessedData
          preprocessed_query_folder=X/Y/Z/data/PreprocessedData/$exp_name/Query
        fi
        rm -rf ${preprocessed_query_folder}
        mkdir -p ${preprocessed_query_folder}


        for i in ${!screens[@]}; do
          "$JAVA_HOME/bin/java" -cp target/code_search_ir-1.0.jar MainClass -br ${bug_reports_folder} \
          -qinfo ${query_infos_file} -s ${screens[$i]} -c ${corpus_type} -preq ${preprocessed_query_folder} \
          -ctype ${content_type}
        done
      elif [[ "${content_type}" == "Title" ]];
      then
        # TODO: Provide the absolute path of data/BugReportsTitles
        bug_reports_titles=X/Y/Z/data/BugReportsTitles
        # TODO: Provide the absolute path of data/PreprocessedData
        preprocessed_titles_folder=X/Y/Z/data/PreprocessedData/$exp_name/Title
        rm -rf ${preprocessed_titles_folder}
        mkdir -p ${preprocessed_titles_folder}

        for i in ${!screens[@]}; do
          "$JAVA_HOME/bin/java" -cp target/code_search_ir-1.0.jar MainClass -br ${bug_reports_titles} \
          -s ${screens[$i]} -preq ${preprocessed_titles_folder} \
          -ctype ${content_type}
        done
      elif [[ "${content_type}" == "Code" ]];
      then
        # TODO: Provide the absolute path of data/PreprocessedData
        preprocessed_code_folder=X/Y/Z/data/PreprocessedData/$exp_name/Code
        rm -rf ${preprocessed_code_folder}
        mkdir -p ${preprocessed_code_folder}
        # TODO: Provide the absolute path of data/BuggyProjects
        buggy_projects=X/Y/Z/data/BuggyProjects

        "$JAVA_HOME/bin/java" -cp target/code_search_ir-1.0.jar MainClass \
          -preq ${preprocessed_code_folder} -bp ${buggy_projects}\
          -ctype ${content_type} -s ${screens}
      fi
    done
  done
done