#!/bin/bash

set -e

if [ ${BUILD_TYPE} == 'deploy' ]; then
   ./mvnw clean package -B
fi
