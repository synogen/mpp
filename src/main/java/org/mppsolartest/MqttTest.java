package org.mppsolartest;

import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class MqttTest {
    public static void main(String[] args) throws MqttException, IOException {
        var config = new Properties();
        config.load(new FileReader("mqtt.properties"));

        var mqttClient = new MqttAsyncClient(config.getProperty("serverUrl"), config.getProperty("clientId"));
        var mqttOptions = new MqttConnectionOptions();
        mqttOptions.setUserName(config.getProperty("username"));
        mqttOptions.setPassword(config.getProperty("password").getBytes());
        mqttClient.connect(mqttOptions).waitForCompletion(5000);

        mqttClient.setCallback(new MqttHandler(mqttClient));

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                mqttClient.disconnect();
                mqttClient.close();
                throw new RuntimeException(e);
            }
        }

//        var message = new MqttMessage("""
//        {
//           "name":"Jag är här",
//           "state_topic":"homeassistant/sensor/testing2/state"
//        }
//        """.getBytes());
//        message.setQos(0);
//        mqttClient.publish("homeassistant/sensor/testing2/config", message);

    }
}
