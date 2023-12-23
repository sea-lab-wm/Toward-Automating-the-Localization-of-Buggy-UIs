# Summary
This is the replication package for the submitted ISSTA-24 paper "Toward the Automated Localization of Buggy Mobile App UIs
from Bug Descriptions". 

# Study 1: Buggy UI Localization
This directory contains all the necessary codes, data, and results to reproduce the Buggy UI Localization (SL: Screen Localization and CL: Component Localization), i.e., Study 1. It contains the following subdirectories:
1. ```lucene:``` contains the codes to run the buggy UI localization (SL and CL) with LUCENE.
2. ```sentence_bert:``` contains the codes to run the buggy UI localization (SL and CL) with SBERT.
3. ```clip:``` contains the codes to run the buggy UI localization (SL and CL) with CLIP.
4. ```blip:``` contains the codes to run the buggy UI localization (SL and CL) with BLIP.
5. ```real_data_construction:``` contains the real data, data statistics, and codes to prepare queries for conducting SL and CL.
6. ```results:``` contains the results of the buggy UI localization (SL and CL) with LUCENE, SBERT, CLIP, and BLIP.
# Study 2: Bug Localization Utilizing Buggy UI Localization
This directory contains all the necessary codes, data, and results to reproduce the Bug Localization experiments utilizing Buggy UI Localization, i.e., Study 1. It contains the following subdirectories:
1. ```concat_and_first_ob_experiments:``` contains the codes to reproduce the results of the experiments with Concat OB and First OB. It also contains the results. Please see the README.md file inside this directory for more details.
2. ```individual_ob_experiments:``` contains the codes to reproduce the results of the experiments with Individual OB. Please see the README.md file inside this directory for more details.