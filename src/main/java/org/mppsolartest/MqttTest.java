package org.mppsolartest;

import com.fazecast.jSerialComm.SerialPort;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.mppsolartest.command.Qpigs;
import org.mppsolartest.communication.SerialHandler;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Properties;

public class MqttTest {
    public static void main(String[] args) throws Exception {
        var mqttConfig = new Properties();
        mqttConfig.load(new FileReader("mqtt.properties"));
        var serialConfig = new Properties();
        serialConfig.load(new FileReader("serial.properties"));

        var mqttClient = new MqttAsyncClient(mqttConfig.getProperty("serverUrl"), mqttConfig.getProperty("clientId"));
        var mqttOptions = new MqttConnectionOptions();
        mqttOptions.setUserName(mqttConfig.getProperty("username"));
        mqttOptions.setPassword(mqttConfig.getProperty("password").getBytes());
        mqttClient.connect(mqttOptions).waitForCompletion(5000);

        mqttClient.setCallback(new MqttHandler(mqttClient));

        // TODO send configuration messages
        // should be sent
        // - when the program starts -> OK
        // - when mqtt homeassistant/status reads online
        var qpigs = new Qpigs();
        var fields = qpigs.getFields();
        var mqttEntityList = new HashMap<String, HomeAssistantMqtt>();
        for (var field: fields) {
            var haMqtt = HomeAssistantMqtt.forField(field, mqttConfig.getProperty("topicPrefix"), mqttConfig.getProperty("sensorNamePrefix"));
            mqttClient.publish(haMqtt.getConfigTopic(), new MqttMessage(haMqtt.getConfigJson().getBytes()));
            mqttEntityList.put(field.description(), haMqtt);
        }


        // TODO subscribe to command topic so HA can send commands to inverter?

        while (true) {
            try {
                // TODO send status messages
                var port = SerialPort.getCommPort(serialConfig.getProperty("port"));
                port.setBaudRate(2400);
                if (port.openPort()) {
                    var serialHandler = new SerialHandler(port);
                    var values = qpigs.run(serialHandler);
                    for (var valueKey: values.keySet()) {
                        var value = values.get(valueKey);
                        var haMqtt = mqttEntityList.get(valueKey);
                        mqttClient.publish(haMqtt.getStateTopic(), new MqttMessage(value.toString().getBytes()));
                    }
                } else {
                    // TODO send unavailable status to availability topic?
                }

                Thread.sleep(10000);
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
