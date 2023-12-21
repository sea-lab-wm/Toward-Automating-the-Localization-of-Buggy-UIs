#! /bin/bash
export JAVA_HOME=`/usr/libexec/java_home -v 1.11`
mvn package -DskipTests

# boosted_queries=("GUI_States" "Interacted_GUI_Component_IDs" "GUI_State_and_Interacted_GUI_Component_IDs"
# 	"All_GUI_Component_IDs" "GUI_State_and_All_GUI_Component_IDs")

# screens=("4" "3" "2")
# corpus_types=("All_Java_Files" "GUI_State_and_All_GUI_Component_IDs" "All_GUI_Component_IDs"
# 	"GUI_States" "Interacted_GUI_Component_IDs" "GUI_State_and_Interacted_GUI_Component_IDs")

#export screens=("4" "3" "2")
export screen="4" 

export content_type="Content"

if [[ "$content_type" == "Content" ]]; then
	corpus_type=GUI_State_and_All_GUI_Component_IDs
	#bug_reports_folder=BugLocalization/FaultLocalizationCode/data/BugReports
	#bug_reports_folder=BugLocalization/FaultLocalizationCode/data/BugReportsContents
	query_infos_file=BugLocalization/FaultLocalizationCode/SourceCodeMapping/MappingAndroidProject/results-screen4
	#preprocessed_query_folder=BugLocalization/FaultLocalizationCode/data/PreprocessedData/PreprocessedQueries-round7
	preprocessed_query_folder=BugLocalization/FaultLocalizationCode/data/PreprocessedData-Mapping/PreprocessedQueries

	java -cp target/code_search_ir-1.0.jar MainClass \
		-qinfo ${query_infos_file} -s ${screen} -c ${corpus_type} -preq ${preprocessed_query_folder} \
		-ctype ${content_type} 
		
elif [[ "${content_type}" == "Title" ]];
then
	bug_reports_titles=BugLocalization/FaultLocalizationCode/data/BugReportsTitles
	preprocessed_titles_folder=BugLocalization/FaultLocalizationCode/data/PreprocessedData/PreprocessedTitles-round7

	for i in ${!screens[@]}; do
		java -cp target/code_search_ir-1.0.jar MainClass -br ${bug_reports_titles} \
		-s ${screens[$i]} -preq ${preprocessed_titles_folder} \
		-ctype ${content_type} 
	done
elif [[ "${content_type}" == "Code" ]];
then
	corpus_type=GUI_State_and_All_GUI_Component_IDs
	query_infos_file=BugLocalization/FaultLocalizationCode/SourceCodeMapping/MappingAndroidProject/results-bug53
	preprocessed_code_folder=BugLocalization/FaultLocalizationCode/data/PreprocessedData-Mapping/PreprocessedCode
	buggy_projects=BugLocalization/BuggyProjects

	java -cp target/code_search_ir-1.0.jar MainClass \
		-preq ${preprocessed_code_folder} -bp ${buggy_projects} -qinfo ${query_infos_file} -c ${corpus_type} \
		-ctype ${content_type} -s ${screen}

fi
