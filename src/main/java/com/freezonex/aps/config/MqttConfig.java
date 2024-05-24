package com.freezonex.aps.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    @Value("${mqtt.broker-url}")
    private String brokerUrl;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.connection-timeout}")
    private int connectionTimeout;

    @Value("${mqtt.clean-session}")
    private boolean cleanSession;

    @Value("${mqtt.automatic-reconnect}")
    private boolean automaticReconnect;

    @Bean
    public IMqttClient mqttClient() {
        IMqttClient client = null;
        try {
            client = new MqttClient(brokerUrl, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(automaticReconnect);
            options.setCleanSession(cleanSession);
            options.setConnectionTimeout(connectionTimeout);
            client.connect(options);
        } catch (MqttException e) {
            System.err.println("Failed to connect to MQTT broker: " + e.getMessage());
            // Handle or log the exception as needed
        }
        return client;
    }
}
