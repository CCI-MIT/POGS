#!/bin/bash

set -e

if [ ${BUILD_TYPE} == 'deploy' ]; then
#   ./mvnw clean package -B -T 3
   ./mvnw clean compile package install -B
fi
