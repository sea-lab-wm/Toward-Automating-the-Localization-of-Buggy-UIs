This directory contains all the necessary codes to reproduce the bug localization experiments of study 2 considering the individual OB from buggy UI localization (study 2) results.

### Steps
```SourceCodeMapping/MappingAndroidProject/filter_files_cmnd.sh``` : Get all the filterted corpus and files which we need to boost

```ShortScripts/match_files_from_repo.sh```: Copy and paste files to a new directory based on query matching

```Lucene/code_search_ir_preprocess_data/run_cmnd.sh```: Preprocess queries

## Lucene
```Lucene/code_search_ir_lucene_graph/run_cmnd.sh```: Run to get all results for Lucene
