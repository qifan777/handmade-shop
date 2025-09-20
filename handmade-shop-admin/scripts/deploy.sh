#!/usr/bin/env bash
cd ..
# lf换行符
if npm run build-only ; then
cd ./scripts
  tar -czvf handmade-admin.tar ../dist
  ssh -i ./ce.pem root@www.arttalks.fun   'cd /home/handmade-admin && sudo rm handmade-admin.tar'
  scp -i ./ce.pem handmade-admin.tar  root@www.arttalks.fun:/home/handmade-admin/handmade-admin.tar
  scp -i ./ce.pem ./start.sh  root@www.arttalks.fun:/home/handmade-admin/start-handmade-admin.sh
  ssh -i ./ce.pem root@www.arttalks.fun  'cd /home/handmade-admin && sudo /usr/bin/bash ./start-handmade-admin.sh'
fi


