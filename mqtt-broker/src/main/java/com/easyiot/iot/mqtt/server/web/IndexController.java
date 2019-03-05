package com.easyiot.iot.mqtt.server.web;

import com.alibaba.fastjson.JSONObject;
import com.easyiot.iot.mqtt.server.store.client.ChannelStoreService;
import com.easyiot.iot.mqtt.server.store.client.TopicStoreService;
import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
        return "Run Success!<br> /topics 查看Topic <br> /clients 查看客户端";

    }

    @GetMapping("/topics/{page}/{size}")
    public Object topics(@PathVariable int page, @PathVariable int size) {
        return JSONObject.toJSONString(topicStoreService.listAll(page, size));

    }

    @GetMapping("/channels/{page}/{size}")

    public Object channels(@PathVariable int page, @PathVariable int size) {

        return JSONObject.toJSONString(channelStoreService.listAll(page, size));

    }

    @GetMapping("/total")
    public Object total() {
        return JSONObject.toJSONString(channelStoreService.clientCount());

    }

    @GetMapping("/clusterInfo")
    public Object clusterInfo() {
        try {
            ClusterNode localNode = ignite.cluster().localNode();
            ClusterNodeInfo clusterNodeInfo = new ClusterNodeInfo(localNode);
            return JSONObject.toJSONString(clusterNodeInfo);
        } catch (Exception e) {
            return "Error:" + e.getMessage();

        }


    }


    /**
     * 根据Token获取用户数据
     *
     * @param request
     * @return
     */
    @Autowired
    JdbcTemplate jdbcTemplate;

    private Map<String, Object> getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token == null) {
            return null;
        }
        try {
            return jdbcTemplate.queryForMap("SELECT * FROM admin WHERE token=? ", token);
        } catch (Exception e) {
            return null;
        }
    }
}