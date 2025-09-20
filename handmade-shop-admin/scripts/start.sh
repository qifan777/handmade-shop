mv ./handmade-admin.tar /opt/1panel/apps/openresty/openresty/www/sites/admin
cd /opt/1panel/apps/openresty/openresty/www/sites/admin
rm -rf index
tar -xzvf handmade-admin.tar
mv dist index