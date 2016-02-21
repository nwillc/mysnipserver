#!/bin/bash

SCRIPT_DIR=$(cd $(dirname ${0}) && pwd -P)

cd ${SCRIPT_DIR}

[ -f env.sh ] && source env.sh

echo Rebuild server...
./gradlew -q stage -x test

echo Start server...
java  -cp build/staging:build/staging/* -Djava.awt.headless=true com.github.nwillc.mysnipserver.MySnipServer $*

