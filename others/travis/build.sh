#!/bin/bash

set -e

if [ ${BUILD_TYPE} == 'deploy' ]; then
#   ./mvnw clean package -B -T 3
   ./mvnw clean package -B
else
   if [ ${RUN_SLOW_TESTS} == 'true' ]; then
#       ./mvnw clean test -B -T 3 -Pslow-tests,skip-frontend
       ./mvnw clean test -B -Pslow-tests,skip-frontend
   else
#       ./mvnw clean test -B -T 3
       ./mvnw clean test -B
   fi
fi
