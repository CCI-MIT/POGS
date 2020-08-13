LOCATION=$(curl -s https://api.github.com/repos/CCI-MIT/POGS/releases/latest \
| grep "tag_name" \
| awk '{print "https://github.com/CCI-MIT/POGS/archive/" substr($2, 2, length($2)-3) ".zip"}') \
; curl -L -o pogs.jar $LOCATION