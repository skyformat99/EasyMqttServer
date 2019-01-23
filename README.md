# EasyLinkerMqttServerV0.0.1：轻量级物联网MQTT服务器, 快速部署, 支持集群.
## 前言

```
本项目基于 https://gitee.com/recallcode/iot-mqtt-server/releases 开发，向作者表示感谢
MQTT文档: https://mcxiaoke.gitbooks.io/mqtt-cn/content/
```

## 快速启动
```sbtshell
mvn -clean
mvn packge
cd mqtt-broker/target
java -jar 编译好的文件.jar
```
## Web端口:
```
localhost:2580
```

## MQTT  端口
```sbtshell
8885
```

## Hello World

### 注意：
```
必须安装Python3，然后安装paho库:
pip3(linux)/pip(windows) install paho-mqtt
```

## 一个最简单的Python客户端测试
```python
import paho.mqtt.client as mqtt
import time
# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))

    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    client.subscribe("/S")
    time.sleep(5)
    client.publish("/1","1")
    client.publish("/2","2")
    client.publish("/3","3")
    client.publish("/4","4")
# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    print(msg.topic+" "+str(msg.payload))

client = mqtt.Client("clientId必须有")
client.on_connect = on_connect
client.on_message = on_message

client.connect("localhost", 8885, 60)

# Blocking call that processes network traffic, dispatches callbacks and
# handles reconnecting.
# Other loop*() functions are available that give a threaded interface and a
# manual interface.
client.loop_forever()
```

##注意：客户端必须有clientId，否则连接不了，而且订阅的topic必须以/开头，这两处为了设计架构，没有按照规范。
#### 软件架构说明
```sbtshell
基于netty+springboot+ignite技术栈实现
1. 使用netty实现通信及协议解析
2. 使用springboot提供依赖注入及属性配置
3. 使用ignite实现存储, 分布式锁, 集群和集群间通信

```

#### 项目结构
```
iot-mqtt-server
  ├── mqtt-auth -- MQTT服务连接时用户名和密码认证
  ├── mqtt-broker -- MQTT服务器功能的核心实现
  ├── mqtt-common -- 公共类及其他模块使用的服务接口及对象
  ├── mqtt-store -- MQTT服务器会话信息, 主题信息等内容的持久化存储,使用了:ignite
```
#### 功能说明
```
1. 参考MQTT3.1.1规范实现
2. 完整的QoS服务质量等级实现
3. 遗嘱消息, 保留消息及消息分发重试
4. 心跳机制
5. 连接认证(强制开启)
5. SSL方式连接(不支持非SSL连接)(非SSl链接形式正在研发)
6. 主题过滤(未完全实现标准: 以#或+符号开头的、以/符号结尾的及不存在/符号的订阅按非法订阅处理, 这里没有参考标准协议)
7. websocket支持
8. 集群功能
```


#### 快速开始(SSL连接)
```sbtshell
- 运行jar文件(如果需要修改配置项,可以在同级目录下新建config/application.yml进行修改)
- 打开mqtt-spy客户端, 填写相应配置[下载](https://github.com/eclipse/paho.mqtt-spy/wiki/Downloads)
- SSL MQTT 连接端口:8885, websocket端口: 9995 websocket path: /mqtt
- 连接使用的用户名:testOne
- 连接使用的密码: 6156ADE136B3BF385B595CF3A69BFAFF2D6AFE87FF3EE1B49224F51D0259B00B014BA7771064E9F46CBF67F27780AABCCC1C252142397FEE8316A91CB0C52176
- 连接使用的证书[mqtt-broker.cer](https://gitee.com/recallcode/iot-mqtt-server/releases)
```

#### 集群使用
```sbtshell
目前支持组播方式集群和静态IP方式集群(不能同时使用组播和静态IP)
- 多机环境集群: 配置application.yml中的`spring.mqtt.broker.id - 保证该值在集群环境中的唯一性`
  - 组播方式: 配置`spring.mqtt.enable-multicast-group=true`及`spring.mqtt.multicast-group=组播地址`
  - 静态IP方式: 配置`spring.mqtt.enable-multicast-group=false`及`spring.mqtt.static-ip-addresses=多个IP使用逗号分隔`
- 单机环境集群: 除上述配置外需要修改相应的端口,避免端口冲突
```

#### 自定义 - 连接认证
```sbtshell

- 默认只是简单使用对用户名进行RSA密钥对加密生成密码, 连接认证时对密码进行解密和相应用户名进行匹配认证
- 使用中如果需要实现连接数据库或其他方式进行连接认证, 只需要重写`mqtt-auth`模块下的相应方法即可

```

#### 自定义 - 服务端证书
```sbtshell
- 服务端证书存储在`mqtt-broker`的`resources/keystore/mqtt-broker.pfx`
- 用户可以制作自己的证书, 但存储位置和文件名必须使用上述描述的位置及文件名

```

#### 生成环境部署
```sbtshell
- 生成环境部署建议使用`keepalived+nginx+mqtt-broker`方式
- 使用nginx的tcp和websocket反向代理mqtt-broker集群实现负载均衡
- 使用keepalived实现nginx的高可用    
![图片](https://images.gitee.com/uploads/images/2018/0712/112559_e5f8401d_1081719.png "png")
- `mqtt-broker`模块中包含`Dockerfile`文件可以直接生成镜像
- 需要注意: 基于集群的实现机制, 在通过`docker run`部署容器时,需要添加--net=host参数
- `docker run --name=mqtt-broker-service --net=host --restart=always --env-file=/home/rancher/mqtt-broker/env.list -v /home/rancher/mqtt-broker/config/:/opt/mqtt-broker/config/ -v /home/rancher/mqtt-broker/persistence/:/opt/mqtt-broker/persistence/ -d mqtt-broker:1.5`
```
