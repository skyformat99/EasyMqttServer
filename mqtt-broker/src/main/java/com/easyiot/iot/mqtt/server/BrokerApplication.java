/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.easyiot.iot.mqtt.server;

import org.apache.ignite.springdata.repository.config.EnableIgniteRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 通过SpringBoot启动服务
 */
@EnableScheduling
@EnableIgniteRepositories
@SpringBootApplication(scanBasePackages = {"com.easyiot.iot.mqtt.server"})
public class BrokerApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(BrokerApplication.class);
        //application.setWebApplicationType(WebApplicationType.REACTIVE);
        application.run(args);
    }


}
