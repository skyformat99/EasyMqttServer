package com.easyiot.iot.mqtt.server.plugin


import com.easyiot.iot.mqtt.server.plugin.auth.IAuthService

/**
 * 认证插件
 */
interface AuthPlugin extends BasePlugin, IAuthService {
}
