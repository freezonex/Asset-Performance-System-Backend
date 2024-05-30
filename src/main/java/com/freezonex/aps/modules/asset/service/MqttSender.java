package com.freezonex.aps.modules.asset.service;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MqttSender {

    @Autowired
    private IMqttClient mqttClient;
    private static final int MAX_RETRY_COUNT = 10; // 最大重试次数
    private static final Logger LOGGER = LoggerFactory.getLogger(MqttSender.class); // 使用SLF4J日志记录器
    @Async
    public void sendMessage(String topic, String payload) {
        int attempt = 0;
        while (attempt < MAX_RETRY_COUNT) {
            try {
                if (!mqttClient.isConnected()) {
                    LOGGER.info("Client is not connected. Attempting to reconnect...");
                    mqttClient.reconnect();
                }
                LOGGER.info("Sending message to topic: {}", topic);
                MqttMessage message = new MqttMessage(payload.getBytes());
                message.setQos(2);
                message.setRetained(true);
                mqttClient.publish(topic, message);
                LOGGER.info("Message sent successfully");
                return; // 成功发送后返回
            } catch (MqttException e) {
                LOGGER.error("Attempt {} failed to send message to topic: {}. Error: {}", attempt + 1, topic, e.getMessage());
                attempt++;
                handleRetry(attempt);
            }
        }
        LOGGER.error("Failed to send message after {} attempts. Aborting.", MAX_RETRY_COUNT);
    }

    private void handleRetry(int attempt) {
        if (attempt >= MAX_RETRY_COUNT) {
            LOGGER.error("Maximum retry attempts reached. Aborting operation.");
            return;
        }
        try {
            // 指数退避策略，逐步增加等待时间
            long delay = 1000 * attempt;
            LOGGER.info("Waiting {} milliseconds before retry.", delay);
            Thread.sleep(delay);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt(); // 恢复中断状态
            LOGGER.error("Thread interrupted during retry delay", ie);
        }
    }
}
