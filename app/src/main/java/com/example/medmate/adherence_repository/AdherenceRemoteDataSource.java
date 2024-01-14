package com.example.medmate.adherence_repository;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AdherenceRemoteDataSource {


    private static AdherenceRemoteDataSource instance = null;
    private Mqtt3AsyncClient mqttClient;
    private final String adherenceTopic = "adherence";
    private int messageCount = 0;
    private List<AdherenceMessageObserver> observers = new ArrayList<>();

    // Observer interface
    public interface AdherenceMessageObserver {
        void onAdherenceMessageReceived(int messageCount);
    }

    // Private constructor
    private AdherenceRemoteDataSource(String serverIp) {
        if (serverIp == null || serverIp.isEmpty()) {
            serverIp = "192.168.0.105"; // Default IP address
        }
        String serverUri = "tcp://" + serverIp + ":1883";
        mqttClient = MqttClient.builder()
                .useMqttVersion3()
                .identifier("AndroidClient-Adherence")
                .serverHost(serverUri.split(":")[1].substring(2))
                .serverPort(Integer.parseInt(serverUri.split(":")[2]))
                .buildAsync();
        connectToBroker();
    }

    // Public method to get the instance
    public static synchronized AdherenceRemoteDataSource getInstance(String serverIp) {
        if (instance == null) {
            instance = new AdherenceRemoteDataSource(serverIp);
        }
        return instance;
    }

    // Subscribe observer
    public void addObserver(AdherenceMessageObserver observer) {
        observers.add(observer);
    }

    // Unsubscribe observer
    public void removeObserver(AdherenceMessageObserver observer) {
        observers.remove(observer);
    }

    // Notify observers
    private void notifyObservers() {
        for (AdherenceMessageObserver observer : observers) {
            observer.onAdherenceMessageReceived(messageCount);
        }
    }
    private void connectToBroker() {
        mqttClient.connectWith()
                .send()
                .whenComplete((mqtt3ConnAck, throwable) -> {
                    if (throwable != null) {
                        Log.d("#tagAdherenceConnectionError", "Error connecting to MQTT broker", throwable);
                    } else {
                        Log.d("#tagAdherenceConnectionSuccess", "Connected to MQTT broker, subscribing to topic");
                        subscribeToAdherenceTopic();
                    }
                });
    }

    private void subscribeToAdherenceTopic() {
        mqttClient.subscribeWith()
                .topicFilter(adherenceTopic)
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(publish -> {
                    String message = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
                    handleAdherenceMessage(message);
                })
                .send()
                .whenComplete((mqtt3SubAck, throwable) -> {
                    if (
                            throwable != null) {
                        Log.d("#tagAdherenceSubscribeError", "Error subscribing to topic", throwable);
                    } else {
                        Log.d("#tagAdherenceSubscribeSuccess", "Subscribed to adherence topic");
                    }
                });
    }

    private void handleAdherenceMessage(String message) {
        // Process the message received about medicine adherence
        Log.d("#tagAdherenceMessageReceived", "Received adherence message: " + message);
        messageCount++; // Increment the message count
        notifyObservers(); // Notify all observers
    }

}