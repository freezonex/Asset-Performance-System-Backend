package com.freezonex.aps.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    /**
     * 创建并初始化一个IMqttClient实例，用于与MQTT服务器建立连接。
     *
     * @return IMqttClient 返回初始化好的MQTT客户端实例。
     * @throws MqttException 如果建立连接时发生错误，则抛出MqttException。
     */
    @Bean
    public IMqttClient mqttClient() throws MqttException {
        // 设置发布者ID
        String publisherId = "aps-mqtt";
        // 创建MqttClient实例，连接地址和客户端ID
        IMqttClient client = new MqttClient("tcp://47.236.10.165:30884", publisherId);

        // 配置连接选项
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true); // 开启自动重连
        options.setCleanSession(true); // 设置会话清除标志
        options.setConnectionTimeout(10); // 设置连接超时时间

        // 使用配置选项连接到MQTT服务器
        client.connect(options);

        return client;
    }

}
