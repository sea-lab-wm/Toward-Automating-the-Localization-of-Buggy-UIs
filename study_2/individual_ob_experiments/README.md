# Summary
This directory contains all the necessary codes to reproduce the buggy code localization (study 2) experiments considering the individual OB from buggy UI localization (study 1) results.
## List of experiments:
1. Individual-OB-3-Screens: Conduct Bug Localization (Study 2) using top 3 screens from Buggy UI Localization (Study 1) with individual OB
2. Individual-OB-4-Screens: Conduct Bug Localization (Study 2) using top 4 screens from Buggy UI Localization (Study 1) with individual OB
# How to reproduce results of all 2 experiments?
## Prerequisites
1. Install JDK 11+ and Apache Maven(3.6.3). Add both JDK and Maven to your PATH environment variable.
2. Update the paths (provide the absolute paths) of the following code files (paths that need to be updated are marked by TODO comments):
    * ```study_2/individual_ob_experiments/SourceCodeMapping/MappingAndroidProject/filter_files_cmnd.sh```
    * ```study_2/individual_ob_experiments/ShortScripts/match_files_from_repo.sh```
    * ```study_2/individual_ob_experiments/Lucene/code_search_ir_preprocess_data/run_cmnd.sh```
    * ```study_2/individual_ob_experiments/Lucene/code_search_ir_lucene_graph/run_cmnd.sh```
## Steps
Run the following scripts in the given order to reproduce the results of all 4 experiments:
1. ```SourceCodeMapping/MappingAndroidProject/filter_files_cmnd.sh``` : Get all the filtered corpus and files which we need to boost. The outcome of this step will be saved into ```FilteredUnfilteredFiles``` directory for all 2 experiments.
2. ```ShortScripts/match_files_from_repo.sh```: Copy and paste files to a new directory based on query matching. The outcome of this step will be saved into ```FilteredBoostedProjects``` directory for all 2 experiments.
3. ```Lucene/code_search_ir_preprocess_data/run_cmnd.sh```: Preprocess queries. The outcome of this step will be saved into ```study_2/data/PreprocessedData``` directory for all 2 experiments.
### Lucene
4. ```Lucene/code_search_ir_lucene_graph/run_cmnd.sh```: Run to get bug localization results (study 2) with Lucene for all the 2 experiments. The results will be saved into ```results``` directory for all 2 experiments.
### Process Results
5. ```FinalResultComputation/get_final_results_lucene.py```: Run to process the LUCENE results of all 4 experiments. The final results will be saved into ```study_2/FinalResults/LUCENE``` directory for all 4 experiments.
6. ```study_2/ResultsSummary/create_results_spreadsheet.py```: Run to create spreadsheet for results summary of all experiments. The spreadsheets will be saved into ```study_2/ResultsSummary``` directory for both LUCENE and BugLocator.
