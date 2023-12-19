#!/bin/bash
set -x #echo on

# JAVA_HOME path (JRE/JDK 11+)
export JAVA_HOME=$(/usr/libexec/java_home)

# TODO: change this path to the location of the repositories
# REPOSITORIES_PATH: path to the folder where all the repositories are located
export REPOSITORIES_PATH=/Volumes/Education/GitHub

export CUR_DIR=`pwd`
export APPCORE_REPO_PATH=$REPOSITORIES_PATH/appcore
export TXT_ANALYZER_REPO_PATH=$REPOSITORIES_PATH/text-analyzer
export IR_ENGINE_PATH=$REPOSITORIES_PATH/Toward-Automating-the-Localization-of-Buggy-UIs-Anonymized/study_1/lucene

# project building
cd $APPCORE_REPO_PATH/appcore && ./gradlew clean testClasses install
cd $TXT_ANALYZER_REPO_PATH/text-analyzer && ./gradlew clean testClasses install

# install additional libraries
cd $IR_ENGINE_PATH/lib
mvn install:install-file -Dfile=ir4se-fwk-0.0.2.jar -DgroupId=edu.wayne.cs.severe -DartifactId=ir4se-fwk -Dversion=0.0.2 -Dpackaging=jar
cd $IR_ENGINE_PATH && mvn package -DskipTests
$JAVA_HOME/bin/java -cp target/ir_engine-1.0.jar ScreenLocalization
cd $CUR_DIR
$JAVA_HOME/bin/java -cp target/ir_engine-1.0.jar ComponentLocalization
cd $CUR_DIR







