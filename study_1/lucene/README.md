# How to build/run?

1. Install JDK 11+ and Apache Maven(3.6.3). Add both JDK and Maven to your PATH environment variable.

2. Clone the following repos within the same directory where Toward-Automating-the-Localization-of-Buggy-UIs-Anonymized folder is located:
* appcore (git@github.com:ojcchar/appcore.git)
* text-analyzer (git@github.com:ojcchar/text-analyzer.git)
* code_search_ir (git@github.com:ojcchar/code_search_ir.git)

3. Change the REPOSITORIES_PATH of lucene/build_run.sh script to the location of the repositories. If you use windows, you can create a similar script.

4. Run the script. If everything is ok, you shouldn't have any errors and the program should build/run successfully.
