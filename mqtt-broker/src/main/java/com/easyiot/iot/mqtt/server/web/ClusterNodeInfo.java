package com.easyiot.iot.mqtt.server.web;

import lombok.Data;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.lang.IgniteProductVersion;

import java.util.Collection;
import java.util.UUID;

/**
 * 集群信息封装
 */
@Data
public class ClusterNodeInfo {

    private double averageCpuLoad;
    private double currentCpuLoad;
    private double currentGcCpuLoad;
    private int totalCpus;
    private long nodeStartTime;
    private int totalNodes;
    private UUID id;
    private boolean isLocal;
    private IgniteProductVersion version;
    private Collection<String> hostNames;
    private Collection<String> addresses;

    /**
     * @param clusterNode
     */
    public ClusterNodeInfo(ClusterNode clusterNode) {
        this.isLocal = clusterNode.isLocal();
        this.hostNames = clusterNode.hostNames();
        this.addresses = clusterNode.addresses();
        this.version = clusterNode.version();
        this.id = clusterNode.id();
        this.totalNodes = clusterNode.metrics().getTotalNodes();
        this.nodeStartTime = clusterNode.metrics().getNodeStartTime();
        this.averageCpuLoad = clusterNode.metrics().getAverageCpuLoad();
        this.currentCpuLoad = clusterNode.metrics().getCurrentCpuLoad();
        this.currentGcCpuLoad = clusterNode.metrics().getCurrentGcCpuLoad();
        this.totalCpus = clusterNode.metrics().getTotalCpus();
    }
}
