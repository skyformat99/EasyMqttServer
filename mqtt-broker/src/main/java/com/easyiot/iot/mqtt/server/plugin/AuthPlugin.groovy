package com.easyiot.iot.mqtt.server.plugin

import com.easyiot.iot.mqtt.server.common.auth.IAuthService
import org.springframework.stereotype.Service

/**
 * 认证插件
 */
interface AuthPlugin extends BasePlugin, IAuthService {
}
