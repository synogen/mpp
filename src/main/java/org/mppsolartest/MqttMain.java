package org.mppsolartest;

import com.fazecast.jSerialComm.SerialPort;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.mppsolartest.command.Qpigs;
import org.mppsolartest.command.Qpiri;
import org.mppsolartest.model.Field;
import org.mppsolartest.mqtt.HomeAssistantMqttText;
import org.mppsolartest.mqtt.MqttUtil;
import org.mppsolartest.serial.SerialHandler;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Properties;

import static org.mppsolartest.Log.*;

public class MqttMain {

    private static String haStatusTopic = "homeassistant/status";
    public static void main(String[] args) throws Exception {
        // only for remote debug wait
//        Log.log("Press any key to start");
//        System.in.read();

        var mqttConfig = new Properties();
        mqttConfig.load(new FileReader("mqtt.properties"));
        var topicPrefix = mqttConfig.getProperty("topicPrefix");
        var deviceName = mqttConfig.getProperty("deviceName");
        var serialConfig = new Properties();
        if (Files.exists(Path.of("serial.properties"))) {
            serialConfig.load(new FileReader("serial.properties"));
        } else {
            serialConfig.put("port", "/dev/ttyUSB0");
        }

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

        // open serial port
        var port = SerialPort.getCommPort(serialConfig.getProperty("port"));
        port.setBaudRate(2400);
        var portOpen = port.openPort(500);
        var serialHandler = new SerialHandler(port);

        // get MQTT entities for QPIGS inverter command
        var qpigs = new Qpigs();
        var qpiri = new Qpiri();

        var fields = new ArrayList<Field>();
        fields.addAll(qpigs.getFields());
        fields.addAll(qpiri.getFields());
        var mqttEntityList = MqttUtil.getHaMqttEntities(fields, topicPrefix, deviceName);

        // add command MQTT entity for receiving raw commands
        var commandEntity = new HomeAssistantMqttText("Raw Command Receiver", topicPrefix, deviceName);
        mqttEntityList.put(commandEntity.getName(), commandEntity);

        // MQTT subscriptions and handling
        mqttSubscriber.subscribe(haStatusTopic, 0);
        mqttSubscriber.subscribe(commandEntity.getCommandTopic(), 0);
        mqttSubscriber.setCallback(new MqttCallback() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                log("[MQTT] " + topic + ": " + message.toString());
                if (topic.equalsIgnoreCase(haStatusTopic)) {
                    if (message.toString().equalsIgnoreCase("online")) {
                        // HA MQTT discovery configurations on HA coming online
                        MqttUtil.publishConfigForHaMqttEntities(mqttEntityList, mqttPublisher);
                        log("[MQTT] Re-published MQTT discovery configurations for Home Assistant");
                    }
                } else if (topic.equalsIgnoreCase(commandEntity.getCommandTopic())) {
                    // TODO should this action be defined on the command entity itself so the callback simply checks all command entities and runs their action if the topic matches theirs?
                    var response = serialHandler.excuteSimpleCommand(message.toString());
                    if (response.isEmpty()) response = "Empty response received, check serial configuration";
                    mqttSubscriber.publish(commandEntity.getStateTopic(), new MqttMessage(response.getBytes()));
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

        // HA MQTT discovery configurations on program start
        MqttUtil.publishConfigForHaMqttEntities(mqttEntityList, mqttPublisher);
        log("[MQTT] Published MQTT discovery configurations for Home Assistant");


        // serial query loop
        try {
            while (true) {
                // get updates from inverter and publish to MQTT
                if (portOpen) {
                    var values = qpigs.run(serialHandler);
                    values.putAll(qpiri.run(serialHandler));
                    if (values.keySet().isEmpty()) log("[Serial] No values received from serial port " + port.getSystemPortName() + ", check config!");
                    for (var valueKey: values.keySet()) {
                        var value = values.get(valueKey);
                        if (mqttEntityList.containsKey(valueKey)) {
                            var haMqtt = mqttEntityList.get(valueKey);
                            mqttPublisher.publish(haMqtt.getStateTopic(), new MqttMessage(value.toString().getBytes()));
                        }
                    }
                    log("[MQTT] Published updates from inverter to MQTT");
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
