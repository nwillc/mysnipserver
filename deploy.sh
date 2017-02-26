#!/bin/sh

KEYFILE=$(mktemp /tmp/key.XXXXXX)
trap "rm -rf ${KEYFILE}" EXIT

openssl enc -aes-256-cbc -salt -S $OSSL_SALT -K $OSSL_KEY -iv $OSSL_IV -d -in .key/mysnip.enc -out ${KEYFILE}
chmod 0400 ${KEYFILE}
scp -o StrictHostKeyChecking=false -i ${KEYFILE} build/libs/*-standalone.jar mysnip@192.168.1.12:/home/mysnip/libs
ssh -o StrictHostKeyChecking=false -i ${KEYFILE} mysnip@192.168.1.12 "~/bounce.sh"