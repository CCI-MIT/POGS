#!/usr/bin/env bash
cat /opt/etherpad-lite/temp/APIAddedFunctions.js >> /opt/etherpad-lite/src/node/db/API.js
sed -i '136i, "getRevisionDate"           : ["padID", "rev"], "getRevisionAuthor"           : ["padID", "rev"]' /opt/etherpad-lite/src/node/handler/APIHandler.js
sed -i '44i authorColors=true;' /opt/etherpad-lite/src/node/utils/ExportHtml.js
cat /opt/etherpad-lite/src/static/custom/pad.css >> /opt/etherpad-lite/src/static/skins/no-skin/pad.css

