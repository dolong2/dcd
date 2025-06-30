#!/bin/bash
source /home/dolong2/dcd/dcd.env
cd /home/dolong2/dcd && nohup java -jar /home/dolong2/dcd/server-0.0.1-SNAPSHOT.jar > /home/dolong2/dcd/dcd.log 2>&1 &
docker exec dcd-nginx nginx -s reload