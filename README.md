# Summary
This is the replication package for the ISSTA'24 paper titled "Toward the Automated Localization of Buggy Mobile App UIs from Bug Descriptions".
Authors: Antu Saha, Yang Song, Junayed Mahmud, Ying Zhou, Kevin Moran, and Oscar Chaparro.
 
<mark> Note: Please see the latest version (currently v4) for the most updated artifacts. </mark>

# Getting Started
This section contains the steps to set up the environment and reproduce the results of Buggy UI Localization tasks (Study 1) of the paper. Note that the Buggy UI Localization tasks (screen and component localization) are the main focus of the paper. All the experiments of study 1 can be run in 30 minutes using the following instructions. We already provided the results of all the experiments. Rerunning the experiments will overwrite the results.
## Setting up the Environment
1. Install JDK 11+ and Apache Maven(3.6.3). Add both JDK and Maven to your PATH environment variable.
2. Install Python 3.
3. Install pip.
4. Install required packages from requirements.txt file. Run the following command: ```pip install -r requirement.txt```
## Reproducing the Results of Buggy UI Localization (Study 1) 
1. Run ```run_cmnd.sh``` for reproducing the results with this command: ```sh run_cmnd.sh```. If you use windows, please create a similar script. This script will run all the experiments of Study 1. The experiments can be run in 30 minutes using this script. For running each experiment separately, please comment out the commands of other experiments.
2. The results will be stored in the ```results``` directory where ```SL``` folder will contain screen localization results and ```CL``` will contain component localization results. The results are already provided (as CSV files). Rerunning the experiments will overwrite the results.
3. The results summary will be saved in ```study_1/results/results_summary.xlsx``` file. The file is already provided. Rerunning the script will overwrite the results summary.
# Detailed Description
This section contains the detailed description of the directories and files in this repository.
## study_1: Buggy UI Localization
This directory contains all the necessary codes, data, and results to reproduce the Buggy UI Localization (SL: Screen Localization and CL: Component Localization), i.e., Study 1. It contains the following subdirectories:
1. ```lucene:``` contains the codes to run the buggy UI localization (SL and CL) with LUCENE.
    * ```src/main/java/ScreenLocalization.java:``` this java file will perform the Screen Localization (SL) with LUCENE.
    * ```src/main/java/ComponentLocalization.java:``` this java file will perform the Component Localization (CL) with LUCENE.
    * ```lib:``` this folder contains the required libraries.
    * ```required_projects:``` this folder contains required projects for performing SL and CL.
2. ```sentence_bert:``` contains the codes to run the buggy UI localization (SL and CL) with SBERT.
    * ```screen_and_component_localization.py:``` this python script will perform the Screen Localization (SL) and Component Localization (CL) with SBERT.
    * ```utils.py:``` this script contains necessary user-defined functions for performing SL and CL.
    * ```evaluation_metrics.py:``` this script contains the necessary functions for measuring the performance of the models for SL and CL.
3. ```clip:``` contains the codes to run the buggy UI localization (SL and CL) with CLIP.
    * ```screen_localization.py:``` this python script will perform the Screen Localization (SL) with CLIP.
    * ```component_localization.py:``` this python script will perform the Component Localization (CL) with CLIP.
    * ```utils.py:``` this script contains necessary user-defined functions for performing SL and CL.
    * ```evaluation_metrics.py:``` this script contains the necessary functions for measuring the performance of the models for SL and CL.
4. ```blip:``` contains the codes to run the buggy UI localization (SL and CL) with BLIP.
5. ```dataset:``` contains the real data, data statistics, and codes to prepare queries for conducting SL and CL.
    * ```real_data:``` contains the constructed real data for conducting SL and CL.
      * ```component_images:``` contains the component images for each bug report and each screen. This is used to conduct the CL experiments with CLIP and BLIP.
      * ```dataset_info:``` contains the information about the dataset (screen, component, and query info). 
      * ```ob:``` contains the queries (OB description) from all the bug reports. ```obs.json``` was used to conduct the SL and CL experiments.
      * ```screen_components:``` contains the textual information of the screens for each bug report. This is used to conduct the SL experiments with LUCENE and SBERT.
      * ```screen_images:``` contains the screen images for each bug report. This is used to conduct the SL experiments with CLIP and BLIP.
6. ```results:``` contains the results of the buggy UI localization (two separate folders for SL and CL experiment results) with LUCENE, SBERT, CLIP, and BLIP. The results are already provided (as CSV files). Rerunning the experiments will overwrite the results. Additionally, we provided three python script to create fine-grained results and results summary.
## study_2: Buggy Code Localization Utilizing Buggy UI Localization
This directory contains all the necessary codes, data, and results to reproduce the Buggy Code Localization (Study 2) experiments utilizing Buggy UI Localization. It contains the following subdirectories:
1. ```concat_and_first_ob_experiments:``` contains the codes to reproduce the results of the experiments with Concat OB and First OB. It also contains the results. Please see the README.md file inside this directory for more details with steps to run the experiments.
2. ```individual_ob_experiments:``` contains the codes to reproduce the results of the experiments with Individual OB. Please see the README.md file inside this directory for more details with steps to run the experiments.
3. ```data:``` contains the data required to run the experiments. ```PreprocessedData``` folder will contain the preprocessed data after running the experiments.
4. ```FinalResults:``` contains the results of the experiments. The results are already provided (as CSV files). Rerunning the experiments will overwrite the results.
5. ```ResultsSummary:``` contains the script to generate the results summary. The results summary is already provided (as xlsx files). Rerunning the script will overwrite the results summary.
## Other Files: Besides the above directories, this repository contains the following files:
1. ```Paper.pdf``` is the accepted version of the paper titled "Toward the Automated Localization of Buggy Mobile App UIs from Bug Descriptions". Note that this is not the final version of the paper. We will submit the final version in the camera-ready deadline.
2. ```requirements.txt``` contains the required packages to run the experiments.
3. ```run_cmnd.sh``` is an automated script to run the experiments of Study 1. The experiments can be run in 30 minutes using this script.
4. ```readme.md``` is the current file that contains the detailed description of the directories and files in this repository. This file also contains the instructions to set up the environment and reproduce the results of the Buggy UI Localization tasks (Study 1).