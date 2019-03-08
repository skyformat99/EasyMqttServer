package com.easyiot.iot.mqtt.server.web;

import com.alibaba.fastjson.JSONObject;
import com.easyiot.iot.mqtt.server.store.client.ChannelStoreService;
import com.easyiot.iot.mqtt.server.store.client.TopicStoreService;
import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/")
    public Object index() {
        return "Run Success!";

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
        return ReturnResult.returnDataMessage(1, "Success", channelStoreService.clientCount());
    }

    /**
     * 本地节点信息
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