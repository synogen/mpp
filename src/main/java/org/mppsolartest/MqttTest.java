package org.mppsolartest;

import com.fazecast.jSerialComm.SerialPort;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.mppsolartest.command.Qpigs;
import org.mppsolartest.mqtt.MqttUtil;
import org.mppsolartest.serial.SerialHandler;

import java.io.FileReader;
import java.util.Properties;

public class MqttTest {

    private static String haStatusTopic = "homeassistant/status";
    public static void main(String[] args) throws Exception {
        // only for remote debug wait
//        System.out.println("Press any key to start");
//        System.in.read();

        var mqttConfig = new Properties();
        mqttConfig.load(new FileReader("mqtt.properties"));
        var serialConfig = new Properties();
        serialConfig.load(new FileReader("serial.properties"));

        var mqttOptions = new MqttConnectionOptions();
        mqttOptions.setUserName(mqttConfig.getProperty("username"));
        mqttOptions.setPassword(mqttConfig.getProperty("password").getBytes());

        // I couldn't get publishing to work properly when one MQTT client handled both subscriptions (callback) and
        // publishing inside a callback, maybe a race-condition of some sort inside paho mqtt or it's just not threaded?
        // So we'll simply use two clients
        var mqttSubscriber = new MqttClient(mqttConfig.getProperty("serverUrl"), mqttConfig.getProperty("clientId") + "_subscriber");
        var mqttPublisher = new MqttClient(mqttConfig.getProperty("serverUrl"), mqttConfig.getProperty("clientId") + "_publisher");
        mqttSubscriber.connect(mqttOptions);
        mqttPublisher.connect(mqttOptions);

        // get MQTT entities for QPIGS inverter command
        var qpigs = new Qpigs();
        var fields = qpigs.getFields();
        var mqttEntityList = MqttUtil.getHaMqttEntities(fields, mqttConfig.getProperty("topicPrefix"), mqttConfig.getProperty("deviceName"));

        mqttSubscriber.setCallback(new MqttCallback() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("[MQTT] " + topic + ": " + message.toString());
                if (topic.equalsIgnoreCase(haStatusTopic)) {
                    if (message.toString().equalsIgnoreCase("online")) {
                        // HA MQTT discovery configurations on HA coming online
                        MqttUtil.publishConfigForHaMqttEntities(mqttEntityList, mqttPublisher);
                        System.out.println("Re-published MQTT discovery configurations for Home Assistant");
                    }
                }
            }

            @Override
            public void disconnected(MqttDisconnectResponse disconnectResponse) {}
            @Override
            public void mqttErrorOccurred(MqttException exception) {}
            @Override
            public void deliveryComplete(IMqttToken token) {}
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {}
            @Override
            public void authPacketArrived(int reasonCode, MqttProperties properties) {}
        });
        mqttSubscriber.subscribe(haStatusTopic, 0);

        // HA MQTT discovery configurations on program start
        MqttUtil.publishConfigForHaMqttEntities(mqttEntityList, mqttPublisher);
        System.out.println("Published MQTT discovery configurations for Home Assistant");

        // TODO subscribe to command topic so HA can send commands to inverter?

        var port = SerialPort.getCommPort(serialConfig.getProperty("port"));
        port.setBaudRate(2400);
        var portOpen = port.openPort(500);

        try {
            while (true) {
                // TODO send status messages
                if (portOpen) {
                    var serialHandler = new SerialHandler(port);
                    var values = qpigs.run(serialHandler);
                    if (values.keySet().isEmpty()) System.out.println("No values received from serial port " + port.getSystemPortName() + ", check config!");
                    for (var valueKey: values.keySet()) {
                        var value = values.get(valueKey);
                        if (mqttEntityList.containsKey(valueKey)) {
                            var haMqtt = mqttEntityList.get(valueKey);
                            mqttPublisher.publish(haMqtt.getStateTopic(), new MqttMessage(value.toString().getBytes()));
                        }
                    }
                } else {
                    // TODO send unavailable status to availability topic?
                }

                Thread.sleep(10000);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            mqttPublisher.disconnect();
            mqttPublisher.close();
            mqttSubscriber.disconnect();
            mqttSubscriber.close();
            port.closePort();
        }
    }


}
