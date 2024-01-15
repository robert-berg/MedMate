package com.example.medmate.medicines_repository;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class MedicineRemoteDataSource {

    private Mqtt3AsyncClient mqttClient;
    private final String mqttTopic = "medicines";

    public MedicineRemoteDataSource(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String ipAddress = sharedPreferences.getString("IPAddress", "192.168.0.105");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = "192.168.0.105"; // Default IP address
        }
        String serverUri = "tcp://" + ipAddress + ":1883";
        mqttClient = MqttClient.builder()
                .useMqttVersion3()
                .identifier("AndroidClient")
                .serverHost(serverUri.split(":")[1].substring(2))
                .serverPort(Integer.parseInt(serverUri.split(":")[2]))
                .buildAsync();
    }

    private void connectToBroker(Runnable onConnectedCallback) {
        // Check if the client is already connected
        if (mqttClient.getState().isConnected()) {
            onConnectedCallback.run();

        } else {
            mqttClient.connectWith()
                    .send()
                    .whenComplete((mqtt3ConnAck, throwable) -> {
                        if (throwable != null) {
                            Log.d("#tagConnectionError", "Error connecting to MQTT broker", throwable);
                        } else {
                            Log.d("#tagConnectionSuccess", "Connected to MQTT broker");
                            onConnectedCallback.run(); // Run the callback after successful connection
                        }
                    });
        }
    }

    public void publishMedicineUpdate(String message) {
        Runnable publishAction = () -> {
            mqttClient.publishWith()
                    .topic(mqttTopic)
                    .payload(message.getBytes())
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .send()
                    .whenComplete((mqtt3Publish, throwable) -> {
                        if (throwable != null) {
                            Log.d("#tagPublishError", "MQTT publish error", throwable);
                        } else {
                            Log.d("#tagPublishSuccess", "Message published");
                        }
                    });
        };

        connectToBroker(publishAction);
    }
}
