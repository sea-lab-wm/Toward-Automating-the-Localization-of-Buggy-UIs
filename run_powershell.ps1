# ==============================
# Study 1: Buggy UI Localization
# ==============================

# Enable verbose output
$VerbosePreference = "Continue"

# JAVA_HOME (find installed JDK)
$JAVA_HOME = (Get-Command java | Select-Object -ExpandProperty Source) -replace "\\bin\\java.exe",""
Write-Verbose "JAVA_HOME = $JAVA_HOME"

# REPOSITORIES_PATH
$REPOSITORIES_PATH = "required_projects"

$CUR_DIR = (Get-Location).Path
$APPCORE_REPO_PATH = "$REPOSITORIES_PATH\appcore"
$TXT_ANALYZER_REPO_PATH = "$REPOSITORIES_PATH\text-analyzer"
$IR_ENGINE_PATH = "$CUR_DIR\study_1\lucene"

# --------------------------
# Project building (Gradle)
# --------------------------

Set-Location "$APPCORE_REPO_PATH\appcore"
./gradlew.bat clean testClasses install

Set-Location "$TXT_ANALYZER_REPO_PATH\text-analyzer"
./gradlew.bat clean testClasses install

# -------------------------------
# Install additional libraries
# -------------------------------

Set-Location "$IR_ENGINE_PATH\lib"
mvn install:install-file `
    -Dfile=ir4se-fwk-0.0.2.jar `
    -DgroupId=edu.wayne.cs.severe `
    -DartifactId=ir4se-fwk `
    -Dversion=0.0.2 `
    -Dpackaging=jar

Set-Location $IR_ENGINE_PATH
mvn package -DskipTests

& "$JAVA_HOME\bin\java.exe" -cp "target\ir_engine-1.0.jar" ScreenLocalization

Set-Location "$CUR_DIR\study_1\lucene"
& "$JAVA_HOME\bin\java.exe" -cp "target\ir_engine-1.0.jar" ComponentLocalization

Set-Location $CUR_DIR

# -------------------------------
# Sentence-BERT
# -------------------------------

Write-Host "Performing Screen Localization (SL)"
python study_1/sentence_bert/screen_and_component_localization.py SL

Write-Host "Performing Component Localization (CL)"
python study_1/sentence_bert/screen_and_component_localization.py CL

# -------------------------------
# CLIP
# -------------------------------
Write-Host "Performing Screen Localization (SL) using CLIP"
python study_1/clip/screen_localization.py

Write-Host "Performing Component Localization (CL) using CLIP"
python study_1/clip/component_localization.py

# -------------------------------
# BLIP
# -------------------------------
Write-Host "Performing Screen Localization (SL) using BLIP"
python study_1/blip/screen_localization.py

Write-Host "Performing Component Localization (CL) using BLIP"
python study_1/blip/component_localization.py

# -------------------------------
# Result Summary Generation
# -------------------------------
python study_1/results/create_fine_grained_results.py
python study_1/results/add_synthetic_bugs_column.py
python study_1/results/create_results_summary.py

Write-Host "Study 1 completed successfully."
