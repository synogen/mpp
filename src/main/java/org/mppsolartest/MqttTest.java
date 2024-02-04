package org.mppsolartest;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;

import java.nio.charset.StandardCharsets;

public class MqttTest {
    public static void main(String[] args) throws MqttException {
        var mqttClient = new MqttClient("tcp://server:1883", "java_voltronic_test");
        var mqttOptions = new MqttConnectionOptions();
        mqttOptions.setUserName("mygga");
        mqttOptions.setPassword("FanTaDem!".getBytes());
        mqttClient.connect(mqttOptions);
        var message = new MqttMessage("Test MQTT from Java 2".getBytes(StandardCharsets.UTF_8));
        message.setQos(0);
        mqttClient.publish("homeassistant", message);
        mqttClient.disconnect();
        mqttClient.close();
    }
}
