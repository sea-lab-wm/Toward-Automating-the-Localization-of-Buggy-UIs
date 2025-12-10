#!/bin/bash

### ==============================
### Study 1: Buggy UI Localization
### ==============================

## Screen localization (SL) and component localization (CL) using LUCENE
## ---------------------------------------------------------------------

set -x #echo on

# JAVA_HOME path (JRE/JDK 11+)
# Fixed for Linux - detect OS and set JAVA_HOME accordingly
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    export JAVA_HOME=$(/usr/libexec/java_home)
else
    # Linux - find Java home
    if [ -n "$JAVA_HOME" ]; then
        # Use existing JAVA_HOME if set
        export JAVA_HOME="$JAVA_HOME"
    else
        # Auto-detect Java home on Linux
        export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
    fi
fi

export CUR_DIR=`pwd`
export IR_ENGINE_PATH=$CUR_DIR/study_1/lucene

# Install dependencies from pre-built JARs in target/dependency
# (required_projects directory is not included in the repository)
echo "Installing Maven dependencies..."
cd $IR_ENGINE_PATH
if [ -f "target/dependency/appcore-1.1.jar" ]; then
    mvn install:install-file -Dfile=target/dependency/appcore-1.1.jar -DgroupId=seers -DartifactId=appcore -Dversion=1.1 -Dpackaging=jar
fi
if [ -f "target/dependency/text-analyzer-1.2.jar" ]; then
    mvn install:install-file -Dfile=target/dependency/text-analyzer-1.2.jar -DgroupId=seers -DartifactId=text-analyzer -Dversion=1.2 -Dpackaging=jar
fi

# install additional libraries
cd $IR_ENGINE_PATH/lib
mvn install:install-file -Dfile=ir4se-fwk-0.0.2.jar -DgroupId=edu.wayne.cs.severe -DartifactId=ir4se-fwk -Dversion=0.0.2 -Dpackaging=jar
cd $IR_ENGINE_PATH && mvn package -DskipTests
"$JAVA_HOME"/bin/java -cp target/ir_engine-1.0.jar ScreenLocalization
cd $CUR_DIR/study_1/lucene
$JAVA_HOME/bin/java -cp target/ir_engine-1.0.jar ComponentLocalization
cd $CUR_DIR


## Screen localization (SL) and component localization (CL) using Sentence-BERT
## ----------------------------------------------------------------------------
echo "Performing Screen Localization (SL)"
python3 study_1/sentence_bert/screen_and_component_localization.py SL

echo "Performing Component Localization (CL)"
python3 study_1/sentence_bert/screen_and_component_localization.py CL

## Screen localization (SL) and component localization (CL) using CLIP
## -------------------------------------------------------------------
echo "Performing Screen Localization (SL) using CLIP"
python3 study_1/clip/screen_localization.py

echo "Performing Component Localization (CL) using CLIP"
python3 study_1/clip/component_localization.py

## Screen localization (SL) and component localization (CL) using BLIP
## -------------------------------------------------------------------
echo "Performing Screen Localization (SL) using BLIP"
python3 study_1/blip/screen_localization.py

echo "Performing Component Localization (CL) using BLIP"
python3 study_1/blip/component_localization.py


## Result Summary Generation
## -------------------------
python3 study_1/results/create_fine_grained_results.py
python3 study_1/results/add_synthetic_bugs_column.py
python3 study_1/results/create_results_summary.py