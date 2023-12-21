#! /bin/bash
#export JAVA_HOME=`/usr/libexec/java_home -v 1.11`
mvn package -DskipTests

export preprocessedDataPath=../../data/PreprocessedData-Mapping/PreprocessedQueries
export preprocessedCodePath=../../data/PreprocessedData-Mapping/PreprocessedCode
export jsonFilePath=../../data/JSON-Files-All
#Change this path
#export buggy_project_dir=BugLocalization/Backup/BuggyProjects
#export buggy_project_dir=BugLocalization/BuggyProjects
export buggy_project_dir=BugLocalization/BuggyProjects
#export buggy_project_dir=BugLocalization/FaultLocalizationCode/Projects/BuggyProjects-round5

#Change this path
#export filtered_boosted_files_in_repo=BugLocalization/Backup/FilteredBoostedProjects
#export filtered_boosted_files_in_repo=BugLocalization/FilteredBoostedProjects
export filtered_boosted_files_in_repo=BugLocalization/FilteredBoostedProjects-screen4
#export filtered_boosted_files_in_repo=BugLocalization/FaultLocalizationCode/Projects/FilteredBoostedProjects-round5

#The temporary results will be saved here
export result_folder=results-screen1 

#Final Results with the proper format will be saved here
export final_ranks_folder=BugLocalization/FaultLocalizationCode/FinalResultComputation/AllMappingResults/Lucene/Screen-4
export filtered_boosted_filenames=BugLocalization/FaultLocalizationCode/SourceCodeMapping/MappingAndroidProject/results-screen4

# This path will not be changed
export preprocessed_code_dir=BugLocalization/BuggyProjects

export filtering_list=("GUI_States" 
	"All_GUI_Component_IDs" "GUI_State_and_All_GUI_Component_IDs")
export boosting_list=("GUI_States" 
	"All_GUI_Component_IDs" "GUI_State_and_All_GUI_Component_IDs")
export query_reformulation_list=("GUI_States"
	"All_GUI_Component_IDs" "GUI_State_and_All_GUI_Component_IDs")
export screen_list=("4")

# export filtering_list=("GUI_States" "GUI_State_and_All_GUI_Component_IDs")
# export boosting_list=("GUI_States")
# export query_reformulation_list=("GUI_States")
# export screen_list=("4")

#For Boosting
for j in ${!boosting_list[@]}; do 
	for k in ${!query_reformulation_list[@]}; do
		for l in ${!screen_list[@]}; do 
			echo "Boosting: B-${boosting_list[$j]}#Q-${query_reformulation_list[$k]}#S-${screen_list[$l]}"

			java -cp target/code_search_ir-1.0.jar MainClass -b ${boosting_list[$j]} \
				-q ${query_reformulation_list[$k]} -s ${screen_list[$l]}  \
				-rf $result_folder \
				-bpd $buggy_project_dir -pcd ${preprocessed_code_dir}\
				-fbr $filtered_boosted_files_in_repo -preq $preprocessedDataPath -jpath $jsonFilePath \
				-ops Boosting -prec $preprocessedCodePath -franks $final_ranks_folder \
				-fbfilenames $filtered_boosted_filenames
		done
	done
done


#No filtering or boosting, query reformulation only
for k in ${!query_reformulation_list[@]}; do
	for l in ${!screen_list[@]}; do 
		echo "Query Reformulation: Q-${query_reformulation_list[$k]}#S-${screen_list[$l]}"

		java -cp target/code_search_ir-1.0.jar MainClass \
				-q ${query_reformulation_list[$k]} -s ${screen_list[$l]}  \
				-rf $result_folder \
				-bpd $buggy_project_dir -pcd ${preprocessed_code_dir}\
				-fbr $filtered_boosted_files_in_repo -preq $preprocessedDataPath -jpath $jsonFilePath \
				-ops QueryReformulation -prec $preprocessedCodePath -franks $final_ranks_folder \
				-fbfilenames $filtered_boosted_filenames 
	done
done

#For Filtering and boosting
for i in ${!filtering_list[@]}; do
	if [[ ${filtering_list[$i]} == "GUI_States" ]]; then
		continue
	else
		boosting_gui_type=()
		index=0
		for j in ${!boosting_list[@]}; do
			if [[ $index -lt $i ]]; then
				boosting_gui_type+=(${boosting_list[j]})
				index=$((index+1))
			else
				break
			fi
		done
	fi

	for j in ${!boosting_gui_type[@]}; do 
		for k in ${!query_reformulation_list[@]}; do
			for l in ${!screen_list[@]}; do 
				echo "Filtering+Boosting: F-${filtering_list[$i]}#B-${boosting_gui_type[$j]}#Q-${query_reformulation_list[$k]}#S-${screen_list[$l]}"

				java -cp target/code_search_ir-1.0.jar MainClass \
					-f ${filtering_list[$i]} -b ${boosting_gui_type[$j]} \
					-q ${query_reformulation_list[$k]} -s ${screen_list[$l]}  \
					-rf $result_folder \
					-bpd $buggy_project_dir -pcd ${preprocessed_code_dir}\
					-fbr $filtered_boosted_files_in_repo -preq $preprocessedDataPath -jpath $jsonFilePath \
					-ops Filtering+Boosting -prec $preprocessedCodePath -franks $final_ranks_folder \
					-fbfilenames $filtered_boosted_filenames 
			done
		done
	done
	#echo "\n"
done


# # For Filtering
for i in ${!filtering_list[@]}; do
	for k in ${!query_reformulation_list[@]}; do
		for l in ${!screen_list[@]}; do 
			echo "Filtering: F-${filtering_list[$i]}#Q-${query_reformulation_list[$k]}#S-${screen_list[$l]}"

			java -cp target/code_search_ir-1.0.jar MainClass \
					-f ${filtering_list[$i]} \
					-q ${query_reformulation_list[$k]} -s ${screen_list[$l]}  \
					-rf $result_folder \
					-bpd $buggy_project_dir -pcd ${preprocessed_code_dir}\
					-fbr $filtered_boosted_files_in_repo -preq $preprocessedDataPath -jpath $jsonFilePath \
					-ops Filtering -prec $preprocessedCodePath -franks $final_ranks_folder \
					-fbfilenames $filtered_boosted_filenames
		done
	done
done