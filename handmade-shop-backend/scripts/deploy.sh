#!/usr/bin/env bash
cd ..
cd ./scripts
ssh -i ./ce.pem root@www.arttalks.fun  'cd /home/handmade-server && rm app.jar'
scp -i ./ce.pem ../target/handmade-shop-backend-1.0-SNAPSHOT.jar  root@www.arttalks.fun:/home/handmade-server/app.jar
scp -i ./ce.pem ./start.sh  root@www.arttalks.fun:/home/handmade-server/start.sh
ssh -i ./ce.pem root@www.arttalks.fun  'cd /home/handmade-server && /usr/bin/bash start.sh'
