#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}" )" > /dev/null 2>&1 && pwd -P)"

cd ${SCRIPT_DIR}

[ -f env.sh ] && source env.sh

echo Rebuild server...
./gradlew -q clean oneJar -x test

JAVA_OPTS="-Djava.awt.headless=true -XX:+UnlockCommercialFeatures -XX:+FlightRecorder"

echo Start server...
java ${JAVA_OPTS} -jar build/libs/*-standalone.jar $*

