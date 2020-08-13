#!/bin/bash

# Set additional environment variables for travis

if [ ${RUN_SLOW_TESTS} == 'false' ] && ([ ${TRAVIS_BRANCH} == 'homolog' ] || [ ${TRAVIS_BRANCH} == 'master' ] || [ ${TRAVIS_BRANCH} == 'develop' ] || [[ $TRAVIS_BRANCH =~ ^deploy/.*$ ]]); then
    export BUILD_TYPE=deploy
    echo "Running deploy build"
else
    export BUILD_TYPE=test
    echo "Running test build"
fi
