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
    @Autowired
    Ignite ignite;
    @Autowired
    ChannelStoreService channelStoreService;
    @Autowired
    TopicStoreService topicStoreService;
    @Autowired
    BrokerProperties brokerProperties;

    @GetMapping("/")
    public Object index() {
        return "Run Success!";

    }

    @PostMapping("/login")
    public Object login(@RequestBody JSONObject body) {
        if (body.getString("web_console_token").length() > 0 &&
                body.getString("web_console_token").equals(brokerProperties.getWebConsoleToken())) {
            JSONObject result = new JSONObject();
            result.put("code", 1);
            result.put("token", brokerProperties.getWebConsoleToken());
            result.put("message", "Login success!");
            return result;
        } else {
            JSONObject result = new JSONObject();
            result.put("code", 0);
            result.put("message", "Login failure!");
            return result;

        }

    }

    @GetMapping("/topics/{page}/{size}")
    public Object topics(@PathVariable int page, @PathVariable int size) {
        return ReturnResult.returnDataMessage(1, "Success", topicStoreService.listAll(page, size));

    }

    @GetMapping("/channels/{page}/{size}")

    public Object channels(@PathVariable int page, @PathVariable int size) {

        return ReturnResult.returnDataMessage(1, "Success", channelStoreService.listAll(page, size));

    }

    @GetMapping("/total")
    public Object total() {
        return ReturnResult.returnDataMessage(1, "Success", channelStoreService.count());
    }

    /**
     * 本地节点信息
     *
     * @return
     */
    @GetMapping("/localNodeInfo")
    public Object localNodeInfo() {
        try {
            ClusterNode localNode = ignite.cluster().localNode();
            ClusterNodeInfo clusterNodeInfo = new ClusterNodeInfo(localNode);
            return ReturnResult.returnDataMessage(1, "Success", JSONObject.toJSON(clusterNodeInfo));
        } catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("code", 0);
            result.put("message", "Info access failure,Because of internal error!");
            return result;

        }


    }
}