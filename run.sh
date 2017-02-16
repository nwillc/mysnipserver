#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}" )" > /dev/null 2>&1 && pwd -P)"

cd ${SCRIPT_DIR}

[ -f env.sh ] && source env.sh

echo Rebuild server...
./gradlew -q clean oneJar -x test
[ $? != 0 ] && exit 1
JAVA_OPTS="-Djava.awt.headless=true -Xmx50m"

echo Start server...
java ${JAVA_OPTS[@]} -jar build/libs/*-standalone.jar $*

