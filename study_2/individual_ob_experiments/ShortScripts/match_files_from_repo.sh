export buggy_project_dir=BugLocalization/BuggyProjects
export filtering_boosting_filenames=BugLocalization/FaultLocalizationCode/SourceCodeMapping/MappingAndroidProject/results-screen4
export filtered_boosted_files_in_repo=BugLocalization/FilteredBoostedProjects-screen4

#This path will not be changed
export buggy_project_dir_in_csv=BugLocalization/BuggyProjects

screens=("4")
corpus_types=("All_Java_Files" "GUI_State_and_All_GUI_Component_IDs" "All_GUI_Component_IDs"
	"GUI_States")
boosted_queries=("GUI_States" "All_GUI_Component_IDs" "GUI_State_and_All_GUI_Component_IDs")

for i in ${!screens[@]}; do
	for j in ${!corpus_types[@]}; do
		for k in ${!boosted_queries[@]}; do
			python3 match_files_from_repo.py -s ${screens[$i]} \
			-c ${corpus_types[$j]} -q ${boosted_queries[$k]} \
			-bpd $buggy_project_dir -fbfile $filtering_boosting_filenames \
			-fbr $filtered_boosted_files_in_repo -bpdcsv $buggy_project_dir_in_csv -ops Filtering+Boosting
		done
		python3 match_files_from_repo.py -s ${screens[$i]} \
			-c ${corpus_types[$j]} \
			-bpd $buggy_project_dir -fbfile $filtering_boosting_filenames \
			-fbr $filtered_boosted_files_in_repo -bpdcsv $buggy_project_dir_in_csv -ops Filtering
	done
done