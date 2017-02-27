#!/bin/bash
SCRIPT_NAME=$(basename "${BASH_SOURCE[0]}")
SERVER="nwillc.freeddns.com"
USER=mysnip
OPTIND=1
OPTERR=0

help_text() {
    code=${1}
    cat << EOU
     Usage: ${SCRIPT_NAME}
        -s server             Target server.
EOU
    exit ${code}
}

while getopts "h?s:u:" OPT; do
    case "$OPT" in
    h)
        help_text 0
        ;;
    v)
        VERBOSE=1
        ;;
    s)
        SERVER="${OPTARG}"
        ;;
     u)
        USER="${OPTARG}"
        ;;
    \?)
        help_text 1
    esac
done

shift $((OPTIND-1))

[ "$1" = "--" ] && shift

KEYFILE=$(mktemp /tmp/key.XXXXXX)
trap "rm -rf ${KEYFILE}" EXIT

openssl enc -aes-256-cbc -salt -S $OSSL_SALT -K $OSSL_KEY -iv $OSSL_IV -d -in .key/mysnip.enc -out ${KEYFILE}
chmod 0400 ${KEYFILE}
scp -o StrictHostKeyChecking=false -i ${KEYFILE} build/libs/*-standalone.jar ${USER}@${SERVER}:/home/mysnip/libs
ssh -o StrictHostKeyChecking=false -i ${KEYFILE} ${USER}@${SERVER} 'kill -USR2 $(cat ~/reloader.pid)'