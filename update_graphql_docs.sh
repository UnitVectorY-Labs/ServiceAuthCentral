#!/bin/bash

# This requires grqphql-markdown to be installed
# https://github.com/exogen/graphql-markdown

cp ./server-manage/src/main/resources/graphql/schema.graphqls ./server-manage/src/main/resources/graphql/schema.gql

graphql-markdown  --no-title --no-toc --update-file ./docs/contributorguide/apireference.md ./server-manage/src/main/resources/graphql/schema.gql

rm ./server-manage/src/main/resources/graphql/schema.gql

