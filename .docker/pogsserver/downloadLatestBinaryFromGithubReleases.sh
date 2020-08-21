cd app/
LOCATION=$(curl -s https://api.github.com/repos/CCI-MIT/POGS/releases/latest \
| grep "/pogs-0.0.1-SNAPSHOT.jar" \
| cut -d : -f 2,3 \
| tr -d \"
)\
; curl -L -o pogs.jar $LOCATION