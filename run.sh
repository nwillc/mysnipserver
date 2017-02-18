#!/bin/bash

# Initialize our own variables
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}" )" > /dev/null 2>&1 && pwd -P)"
SCRIPT_NAME=$(basename "${BASH_SOURCE[0]}")
REBUILD=""
VERBOSE=""
JAR_NAME='mysnipserver*-standalone.jar'

cd ${SCRIPT_DIR}

help_text() {
    code=${1}
    cat << EOU
     Usage: ${SCRIPT_NAME}
        -r              Rebuild the server.
        -v              Turn on verbose messages.
        --              Pass remaining arguments along to server.
EOU
    exit ${code}
}

# A POSIX variable
OPTIND=1         # Reset in case getopts has been used previously in the shell.



while getopts "h?vr" OPT; do
    case "$OPT" in
    h|\?)
        help_text 0
        ;;
    v)
        VERBOSE=1
        ;;
    r)
        REBUILD='true'
        ;;
    esac
done

shift $((OPTIND-1))

[ "$1" = "--" ] && shift

[ -n "${VERBOSE}" ] && echo "REBUILD=${REBUILD}, Passing along: ${@}"

[ -f env.sh ] && source env.sh

if [ -n "${REBUILD}" ]; then
    echo Rebuild server...
    ./gradlew -q clean oneJar -x test
    [ $? != 0 ] && exit 1
fi

JAVA_OPTS="-Djava.awt.headless=true -Xmx100m"
JAR=$(ls -1t $(find . -name ${JAR_NAME}) | head -1)

echo Start server...
[ -n "${VERBOSE}" ] && echo java ${JAVA_OPTS[@]} -jar ${JAR} ${@}
java ${JAVA_OPTS[@]} -jar ${JAR} ${@}
