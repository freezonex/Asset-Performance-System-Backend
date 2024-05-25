package com.freezonex.aps.modules.asset.service;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MqttSender {

    @Autowired
    private IMqttClient mqttClient;
    @Async
    public void sendMessage(String topic, String payload) throws MqttException {
        if (mqttClient.isConnected()) {
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(2);
            message.setRetained(true);
            mqttClient.publish(topic, message);
        }
    }
}
