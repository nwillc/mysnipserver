#!/bin/bash

SCRIPT_DIR=$(cd $(dirname ${0}) && pwd -P)

cd ${SCRIPT_DIR}

./gradlew -q stage

java -cp build/staging:build/staging/* com.github.nwillc.mysnipserver.MySnipServer $*
