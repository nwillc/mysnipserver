#!/bin/sh

. ./env.sh

# rhc env set ORCH_API_KEY=${ORCH_API_KEY} CLIENT_ID=${CLIENT_ID} -a snippets
rhc env set MONGO_DB_SERVER=${MONGO_DB_SERVER} MONGO_DB_PORT=${MONGO_DB_PORT} -a snippets
rhc env set MONGO_DB_USER=${MONGO_DB_USER} MONGO_DB_PASSWORD=${MONGO_DB_PASSWORD} -a snippets