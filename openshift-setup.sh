#!/bin/sh

. ./env.sh

rhc env set ORCH_API_KEY=${ORCH_API_KEY} CLIENT_ID=${CLIENT_ID} -a snippets