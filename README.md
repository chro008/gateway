et 网关服务

构建方法
1. 在服务根目录 gradle build。即可构建服务的jar包。生成目录在build/libs下
2. docker build -t gateway:v1.0 .
   docker tag gateway:v1.0 registry.sensetime.com/smt/gateway:v1.0
   docker push  registry.sensetime.com/smt/gateway:v1.0
 