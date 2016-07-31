#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}" )" > /dev/null 2>&1 && pwd -P)"

cd ${SCRIPT_DIR}

[ -f env.sh ] && source env.sh

echo Rebuild server...
mvn -q clean package dependency:copy-dependencies -DskipTests

CLASSPATH="target/mysnipserver-1.1.5.jar:target/dependency/*"

if hash cygpath 2>/dev/null; then
   CLASSPATH=$(cygpath --path --mixed "$CLASSPATH")
fi

echo Start server...
java  -cp "${CLASSPATH}" -Djava.awt.headless=true com.github.nwillc.mysnipserver.MySnipServer $*

