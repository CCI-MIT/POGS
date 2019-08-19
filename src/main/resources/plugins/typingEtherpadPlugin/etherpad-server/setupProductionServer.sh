#!/usr/bin/env bash
echo "Getting etherpad version: develop"
curl \
    --location \
    --fail \
    --silent \
    --show-error \
    --output /home/pogs/binaries/etherpad-lite.tar.gz \
    https://github.com/ether/etherpad-lite/archive/develop.tar.gz && \
mkdir /home/pogs/binaries/etherpad-lite && \
echo "Unpackaging etherpad" && \
tar xf /home/pogs/binaries/etherpad-lite.tar.gz \
    --directory /home/pogs/binaries/etherpad-lite \
    --strip-components=1
echo "Deleting tar.gz"
rm /home/pogs/binaries/etherpad-lite.tar.gz
echo "Install dependencies"
sh /home/pogs/binaries/etherpad-lite/bin/installDeps.sh
echo "Copy settings.json to etherpad-lite"
cp  settings.json /home/pogs/binaries/etherpad-lite/
echo "Copy APIKEY.txt to etherpad-lite"
cp  APIKEY.txt /home/pogs/binaries/etherpad-lite/
echo "Copy custom pad css to etherpad-lite"
mkdir /home/pogs/binaries/etherpad-lite/src/static/custom
cp pad.css /home/pogs/binaries/etherpad-lite/src/static/custom/
cat APIAddedFunctions.js >> /home/pogs/binaries/etherpad-lite/src/node/db/API.js
sed -i '136i, "getRevisionDate"           : ["padID", "rev"], "getRevisionAuthor"           : ["padID", "rev"]' /home/pogs/binaries/etherpad-lite/src/node/handler/APIHandler.js
sed -i '44i authorColors=true;' /home/pogs/binaries/etherpad-lite/src/node/utils/ExportHtml.js
cat pad.css >> /home/pogs/binaries/etherpad-lite/src/static/skins/no-skin/pad.css
echo "As mysql root create database and user for etherpad:"
echo "CREATE DATABASE etherpad_lite_db CHARACTER SET utf8 COLLATE utf8_general_ci;"
echo "CREATE USER 'etherpad'@'localhost' IDENTIFIED BY 'password';"
echo "GRANT ALL PRIVILEGES ON etherpad_lite_db.* TO 'etherpad'@'localhost';"
echo "FLUSH PRIVILEGES;"

echo "Now update the settings.json file with mysql password configuration"

