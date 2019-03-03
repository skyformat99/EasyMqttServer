package com.easyiot.iot.mqtt.server.web;

import com.easyiot.iot.mqtt.server.common.client.ClientStore;
import org.apache.ignite.springdata.repository.IgniteRepository;

/**
 * 客户端DAO
 */
public interface ClientDAO extends IgniteRepository<ClientStore, Long> {

}
