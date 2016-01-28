#!/bin/bash

SCRIPT_DIR=$(cd $(dirname ${0}) && pwd -P)

cd ${SCRIPT_DIR}

[ -f env.sh ] && source env.sh

echo Rebuild server...
./gradlew -q stage -x test

LIB_DIR=build/staging
IFS=":"
if [[ ${OSTYPE} == "cygwin" ]] ; then
	IFS=";"
fi
export CLASSPATH=${LIB_DIR}$(JARS=(${LIB_DIR}/*.jar); IFS=${IFS} ; echo "${JAR_FILE}${IFS}${JARS[*]}")

echo Start server...
java -Djava.awt.headless=true com.github.nwillc.mysnipserver.MySnipServer $*

