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

    /**
     * Creates and initializes an IMqttClient instance for connecting to the MQTT broker.
     *
     * @return IMqttClient The initialized MQTT client instance, or null if the connection fails.
     */
    @Bean
    public IMqttClient mqttClient() {
        try {
            IMqttClient client = new MqttClient(brokerUrl, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(automaticReconnect);
            options.setCleanSession(cleanSession);
            options.setConnectionTimeout(connectionTimeout);
            client.connect(options);
            return client;
        } catch (MqttException e) {
            // Log the exception and return null to allow the application to continue starting
            System.err.println("Failed to connect to MQTT broker: " + e.getMessage());
            return null;
        }
    }

}
