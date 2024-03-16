package org.mppsolartest;

import com.fazecast.jSerialComm.SerialPort;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.mppsolartest.command.Qdop;
import org.mppsolartest.command.Qpigs;
import org.mppsolartest.command.Qpiri;
import org.mppsolartest.command.WriteCommandHandlers;
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

        // get MQTT entities for inverter commands
        var qpigs = new Qpigs();
        var qpiri = new Qpiri();
        var qdop = new Qdop();

        var fields = new ArrayList<Field>();
        fields.addAll(qpigs.getFields());
        fields.addAll(qpiri.getFields());
        fields.addAll(qdop.getFields());
        var mqttEntityList = MqttUtil.getHaMqttEntities(fields, topicPrefix, deviceName);

        // add command MQTT entity for receiving raw commands
        var commandEntity = new HomeAssistantMqttText("Raw Command Receiver", topicPrefix, deviceName);
        commandEntity.setCommandHandler(WriteCommandHandlers::rawCommandHandler);
        mqttEntityList.put(commandEntity.getName(), commandEntity);

        // add command handlers for setting battery back to grid/discharge/cut-off
        mqttEntityList.get(qdop.getFields().get(8).description()).setCommandHandler(WriteCommandHandlers::pbccCommandHandler);
        mqttEntityList.get(qdop.getFields().get(9).description()).setCommandHandler(WriteCommandHandlers::pbdcCommandHandler);
        mqttEntityList.get(qdop.getFields().get(10).description()).setCommandHandler(WriteCommandHandlers::psdcCommandHandler);

        // MQTT subscriptions and handling
        mqttSubscriber.subscribe(haStatusTopic, 0);
        for (var mqttEntity: mqttEntityList.values()) {
            if (!mqttEntity.getCommandTopic().isEmpty()) mqttSubscriber.subscribe(mqttEntity.getCommandTopic(), 0);
        }

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
                } else {
                    // every MQTT entity with a command topic has to be checked and acted upon here
                    for (var mqttEntity: mqttEntityList.values()) {
                        if (!mqttEntity.getCommandTopic().isEmpty() && topic.equalsIgnoreCase(mqttEntity.getCommandTopic())) {
                            mqttEntity.getCommandHandler().apply(message.toString(), serialHandler, mqttPublisher, mqttEntity);
                        }
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
                    values.putAll(qdop.run(serialHandler));
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
