#include <ESP8266WiFi.h>
#include <PubSubClient.h>
int pinLED = D2;

const char* ssid = "********";//连接的路由器的名字
const char* password = "********";//连接的路由器的密码
const char* mqtt_server = "********";//服务器的地址
const int port=****;//服务器端口号

WiFiClient espClient;
PubSubClient client(espClient);

int light=255;

void setup_wifi() {//自动连WIFI接入网络
  delay(10);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print("...");
  }
}

void callback(char* topic, byte* payload, unsigned int length) {//用于接收数据
  int l=0;
  int p=1;
  for (int i = length-1; i >=0; i--) {
    l+=(int)((char)payload[i]-'0')*p;
    p*=10;
  }
  light=l;
  Serial.println(l);//换行

}

void reconnect() {//等待，直到连接上服务器
  while (!client.connected()) {//如果没有连接上
    if (client.connect("biloba")+random(999999999)) {//接入时的用户名，尽量取一个很不常用的用户名
      client.subscribe("LED");//接收外来的数据时的intopic
    } else {
      Serial.print("failed, rc=");//连接失败
      Serial.print(client.state());//重新连接
      Serial.println(" try again in 5 seconds");//延时5秒后重新连接
      delay(5000);
    }
  }
}


void setup() {//初始化程序，只运行一遍
  Serial.begin(9600);//设置串口波特率（与烧写用波特率不是一个概念）
  setup_wifi();//自动连WIFI接入网络
  client.setServer(mqtt_server, port);//端口号
  client.setCallback(callback); //用于接收服务器接收的数据
}



void loop() {//主循环
   reconnect();//确保连上服务器，否则一直等待。
   client.loop();//MUC接收数据的主循环函数。
   analogWrite(pinLED,light);
}
