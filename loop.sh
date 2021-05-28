#!/bin/bash

while true; do
SERVICE="node foundryvtt"
if pgrep -x "$SERVICE" >/dev/null
then
    echo "$SERVICE is running"
else
    echo "$SERVICE stopped"
    node /home/ec2-user/foundryvtt/resources/app/main.js --dataPath=/home/ec2-user/foundrydata
fi
done
