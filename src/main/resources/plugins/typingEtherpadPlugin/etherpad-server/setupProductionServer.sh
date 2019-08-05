#!/usr/bin/env bash
echo "Getting etherpad version: develop"
curl \
    --location \
    --fail \
    --silent \
    --show-error \
    --output /opt/etherpad-lite.tar.gz \
    https://github.com/ether/etherpad-lite/archive/develop.tar.gz && \
mkdir /home/carlosbp/binaries/etherpad-lite && \
echo "Unpackaging etherpad" && \
tar xf /opt/etherpad-lite.tar.gz \
    --directory /home/carlosbp/binaries/etherpad-lite \
    --strip-components=1
echo "Deleting tar.gz"
rm /home/carlosbp/binaries/etherpad-lite.tar.gz
echo "Install dependencies"
sh /home/carlosbp/binaries/etherpad-lite/bin/installDeps.sh
echo "Copy settings.json to etherpad-lite"
cp  settings.json /home/carlosbp/binaries/etherpad-lite/
echo "Copy APIKEY.txt to etherpad-lite"
cp  APIKEY.txt /home/carlosbp/binaries/etherpad-lite/