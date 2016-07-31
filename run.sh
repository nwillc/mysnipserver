#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}" )" > /dev/null 2>&1 && pwd -P)"

cd ${SCRIPT_DIR}

[ -f env.sh ] && source env.sh

echo Rebuild server...
./gradlew -q stage -x test

CLASSPATH="build/staging:build/staging/*"

if hash cygpath 2>/dev/null; then
   CLASSPATH=$(cygpath --path --mixed "$CLASSPATH")
fi

JAVA_OPTS=-Djava.awt.headless=true -XX:+UnlockCommercialFeatures -XX:+FlightRecorder

echo Start server...
java  -cp "${CLASSPATH}" ${JAVA_OPTS} com.github.nwillc.mysnipserver.MySnipServer $*

