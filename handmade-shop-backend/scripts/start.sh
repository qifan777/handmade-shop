APP_NAME=handmade-server
APP_ENV=prod
APP_NETWORK=1panel-network
APP_PORT=9912
cat > Dockerfile << EOF
# 基础镜像是jdk17
FROM openjdk:17
# 作者的信息，可以不填
MAINTAINER http://www.jarcheng.top/blog
WORKDIR ${APP_NAME}
# 注意这里，这个地方访问的是build时指定的资源路径。而不是当前文件夹下的文件。
# 将资源路径内的的jar包夹复制到镜像内并且重命名为app.jar。
COPY ./app.jar ./app.jar
# 镜像实例化，即启动容器后的运行命令，我们这边就是启动服务
ENTRYPOINT ["java","-jar", "/${APP_NAME}/app.jar"]
EOF

docker build -t ${APP_NAME}:1.0 . --build-arg APP_NAME=${APP_NAME}
docker stop ${APP_NAME}
docker rm ${APP_NAME}
docker run -p ${APP_PORT}:${APP_PORT} \
    -d \
    --name ${APP_NAME} \
    --network=${APP_NETWORK} \
    --network-alias=${APP_NAME} \
    -e spring.profiles.active=${APP_ENV} \
    -e TZ="Asia/Shanghai" \
    --restart=always \
    -v /home/${APP_NAME}/log:/${APP_NAME}/log \
    ${APP_NAME}:1.0
