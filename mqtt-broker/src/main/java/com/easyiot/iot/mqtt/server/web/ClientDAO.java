package com.easyiot.iot.mqtt.server.web;

import com.easyiot.iot.mqtt.server.common.client.ClientStore;
import org.apache.ignite.springdata.repository.IgniteRepository;
import org.apache.ignite.springdata.repository.config.RepositoryConfig;

/**
 * 客户端DAO
 */
@RepositoryConfig(cacheName = "ClientStore")
public interface ClientDAO extends IgniteRepository<ClientStore, Long> {

}
