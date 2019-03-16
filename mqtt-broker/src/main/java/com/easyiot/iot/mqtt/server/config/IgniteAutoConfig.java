/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.easyiot.iot.mqtt.server.config;

import cn.hutool.core.util.StrUtil;
import com.easyiot.iot.mqtt.server.common.client.ChannelStore;
import com.easyiot.iot.mqtt.server.common.client.TopicStore;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteMessaging;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Arrays;

/**
 * 自动配置apache ignite
 */
@Configuration
@SuppressWarnings("unchecked")
public class IgniteAutoConfig {
    private static final String usrDir = System.getProperty("user.dir");
    private static final String separator = File.separator;
    private static final String DB = "db";
    private static final String WAL = "wal";
    private static final String ARCHIVE = "archive";

    @Value("${spring.mqtt.broker.id}")
    private String instanceName;

    @Value("${spring.mqtt.broker.enable-multicast-group: true}")
    private boolean enableMulticastGroup;

    @Value("${spring.mqtt.broker.multicast-group: 239.255.255.255}")
    private String multicastGroup;

    @Value("${spring.mqtt.broker.static-ip-addresses: []}")
    private String[] staticIpAddresses;

    @Bean
    public IgniteProperties igniteProperties() {
        return new IgniteProperties();
    }

    @Bean
    public Ignite ignite() throws Exception {
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        /**
         * 这TMD就是个坑，要设置一个ID
         * 具体参考这里：https://www.cnblogs.com/cord/p/9431865.html
         */
        igniteConfiguration.setConsistentId(instanceName);

        /**
         * 控制台日志住处输出频率 0就是关闭输出
         */

        igniteConfiguration.setMetricsLogFrequency(0);
        /**
         * 集群用到的名称
         */
        igniteConfiguration.setIgniteInstanceName(instanceName);

        /**
         * 日志输出
         */
        Logger logger = LoggerFactory.getLogger("org.apache.ignite");
        igniteConfiguration.setGridLogger(new Slf4jLogger(logger));

        /**
         *  非持久化数据区域
         */
        DataRegionConfiguration notPersistence = new DataRegionConfiguration().setPersistenceEnabled(false)
                .setInitialSize(igniteProperties().getNotPersistenceInitialSize() * 1024 * 1024)
                .setMaxSize(igniteProperties().getNotPersistenceMaxSize() * 1024 * 1024).setName("not-persistence-data-region");
        /**
         *  持久化数据区域
         */
        DataRegionConfiguration persistence = new DataRegionConfiguration().setPersistenceEnabled(true)
                .setInitialSize(igniteProperties().getPersistenceInitialSize() * 1024 * 1024)
                .setMaxSize(igniteProperties().getPersistenceMaxSize() * 1024 * 1024).setName("persistence-data-region");
        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration().setDefaultDataRegionConfiguration(notPersistence)
                .setDataRegionConfigurations(persistence)
                .setWalArchivePath(StrUtil.isNotBlank(igniteProperties().getPersistenceStorePath()) ? igniteProperties().getPersistenceStorePath() : null)
                .setWalPath(StrUtil.isNotBlank(igniteProperties().getPersistenceStorePath()) ? igniteProperties().getPersistenceStorePath() : null)
                .setStoragePath(StrUtil.isNotBlank(igniteProperties().getPersistenceStorePath()) ? igniteProperties().getPersistenceStorePath() : null);
        igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration);

        /**
         * 集群, 基于组播或静态IP配置
         */
        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        if (this.enableMulticastGroup) {
            TcpDiscoveryMulticastIpFinder tcpDiscoveryMulticastIpFinder = new TcpDiscoveryMulticastIpFinder();
            tcpDiscoveryMulticastIpFinder.setMulticastGroup(multicastGroup);
            tcpDiscoverySpi.setIpFinder(tcpDiscoveryMulticastIpFinder);
        } else {
            TcpDiscoveryVmIpFinder tcpDiscoveryVmIpFinder = new TcpDiscoveryVmIpFinder();
            tcpDiscoveryVmIpFinder.setAddresses(Arrays.asList(staticIpAddresses));
            tcpDiscoverySpi.setIpFinder(tcpDiscoveryVmIpFinder);
        }
        igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);
        Ignite ignite = Ignition.start(igniteConfiguration);
        if (!ignite.cluster().active()) {
            ignite.cluster().active(true);  //如果集群未启动则启动集群
        }
        return ignite;
    }

    /**
     * 配置Ignite 的实现类
     *
     * @return
     * @throws Exception
     */

    //topicCache
    @Bean
    public IgniteCache topicStoreCache() throws Exception {
        CacheConfiguration cacheConfiguration = new CacheConfiguration()
                .setDataRegionName("not-persistence-data-region")
                .setCacheMode(CacheMode.PARTITIONED)
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setIndexedTypes(String.class, TopicStore.class)
                .setName("topicStoreCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteCache channelStoreCache() throws Exception {
        CacheConfiguration cacheConfiguration = new CacheConfiguration()
                .setDataRegionName("not-persistence-data-region")
                .setCacheMode(CacheMode.PARTITIONED)
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setIndexedTypes(String.class, ChannelStore.class)
                .setName("channelStoreCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }


    @Bean
    public IgniteCache sessionStoreCache() throws Exception {
        CacheConfiguration cacheConfiguration = new CacheConfiguration()
                .setDataRegionName("not-persistence-data-region")
                .setCacheMode(CacheMode.PARTITIONED)
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setName("sessionStoreCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteCache messageIdCache() throws Exception {
        CacheConfiguration cacheConfiguration = new CacheConfiguration()
                .setDataRegionName("not-persistence-data-region")
                .setCacheMode(CacheMode.PARTITIONED)
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setName("messageIdCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteCache retainMessageCache() throws Exception {
        CacheConfiguration cacheConfiguration = new CacheConfiguration()
                .setDataRegionName("persistence-data-region")
                .setCacheMode(CacheMode.PARTITIONED)
                .setName("retainMessageCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteCache subscribeNotWildcardCache() throws Exception {
        CacheConfiguration cacheConfiguration = new CacheConfiguration().setDataRegionName("persistence-data-region")
                .setCacheMode(CacheMode.PARTITIONED).setName("subscribeNotWildcardCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteCache subscribeWildcardCache() throws Exception {
        CacheConfiguration cacheConfiguration = new CacheConfiguration().setDataRegionName("persistence-data-region")
                .setCacheMode(CacheMode.PARTITIONED).setName("subscribeWildcardCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteCache dupPublishMessageCache() throws Exception {
        CacheConfiguration cacheConfiguration = new CacheConfiguration().setDataRegionName("persistence-data-region")
                .setCacheMode(CacheMode.PARTITIONED).setName("dupPublishMessageCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteCache dupPubRelMessageCache() throws Exception {
        CacheConfiguration cacheConfiguration = new CacheConfiguration().setDataRegionName("persistence-data-region")
                .setCacheMode(CacheMode.PARTITIONED).setName("dupPubRelMessageCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteMessaging igniteMessaging() throws Exception {
        return ignite().message(ignite().cluster().forRemotes());
    }

}
