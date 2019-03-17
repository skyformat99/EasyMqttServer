package com.easyiot.iot.mqtt.server.web;

import com.alibaba.fastjson.JSONObject;
import com.easyiot.iot.mqtt.server.config.BrokerProperties;
import com.easyiot.iot.mqtt.server.common.client.ChannelStoreService;
import com.easyiot.iot.mqtt.server.common.client.TopicStoreService;
import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 设计哲学：
 * 参考了 Netty的设计思路，这里每一个连接进来的客户端，都叫做Channel（理解为一个‘管道’）
 */
@RestController
public class IndexController {

}