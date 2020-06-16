#!/bin/bash

sed -i 's/http:\/\/localhost:9001/http:\/\/nodeserver:9001/g' /app/plugins/typingEtherpadPlugin/taskAfterWork.js
sed -i 's/http:\/\/localhost:9001/http:\/\/nodeserver:9001/g' /app/plugins/typingEtherpadPlugin/taskBeforeWork.js
sed -i 's/http:\/\/localhost:8082/http:\/\/pythonserver:8082/g' /app/plugins/typingEtherpadPlugin/pluginProperties.yml
sed -i 's/http:\/\/localhost:9001/http:\/\/nodeserver:9001/g' /app/plugins/typingEtherpadPlugin/taskExport.js


sed -i 's/http:\/\/localhost:9001/http:\/\/nodeserver:9001/g' /app/plugins/typingInColorsPlugin/taskAfterWork.js
sed -i 's/http:\/\/localhost:9001/http:\/\/nodeserver:9001/g' /app/plugins/typingInColorsPlugin/taskBeforeWork.js
sed -i 's/http:\/\/localhost:8082/http:\/\/pythonserver:8082/g' /app/plugins/typingInColorsPlugin/pluginProperties.yml
sed -i 's/http:\/\/localhost:9001/http:\/\/nodeserver:9001/g' /app/plugins/typingInColorsPlugin/taskExport.js