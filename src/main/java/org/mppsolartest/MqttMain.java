package org.mppsolartest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazecast.jSerialComm.SerialPort;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.mppsolartest.command.*;
import org.mppsolartest.model.Field;
import org.mppsolartest.mqtt.HomeAssistantMqttEntityBase;
import org.mppsolartest.mqtt.HomeAssistantMqttSensor;
import org.mppsolartest.mqtt.HomeAssistantMqttText;
import org.mppsolartest.mqtt.MqttUtil;
import org.mppsolartest.serial.SerialHandler;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import static org.mppsolartest.Log.log;

public class MqttMain {

    private static final boolean remoteDebugWait = false;
    private static final String haStatusTopic = "homeassistant/status";

    public static void main(String[] args) throws Exception {
        // remote debug wait
        if (remoteDebugWait) {
            System.out.println("Waiting for remote debug connection, press any key when connected");
            System.in.read();
        }

        // load configuration files
        var mqttConfig = loadMqttConfig("mqtt.properties");
        var topicPrefix = mqttConfig.getProperty("topicPrefix");
        var deviceName = mqttConfig.getProperty("deviceName");
        var username = mqttConfig.getProperty("username");
        var password = mqttConfig.getProperty("password").getBytes();
        var serverUrl = mqttConfig.getProperty("serverUrl");
        var clientId = mqttConfig.getProperty("clientId");
        var serialConfig = loadSerialConfig("serial.properties");
        var serialPort = serialConfig.getProperty("port");

        // create MQTT clients
        // I couldn't get publishing to work properly when one MQTT client handled both subscriptions (callback) and
        // publishing inside a callback, maybe a race-condition of some sort inside paho mqtt or it's just not threaded?
        // So we'll simply use two clients
        var mqttOptions = new MqttConnectionOptionsBuilder().username(username).password(password).build();
        var mqttSubscriber = createMqttClient(mqttOptions, serverUrl, clientId + "_subscriber");
        var mqttPublisher = createMqttClient(mqttOptions, serverUrl, clientId + "_publisher");

        // open serial port
        var serialHandler = new SerialHandler(SerialPort.getCommPort(serialPort));

        // get MQTT entities for all inverter commands that will be run repeatedly with a certain interval
        var qpigs = new Qpigs();
        var qpiri = new Qpiri();
        var qdop = new Qdop();
        var qmod = new Qmod();

        var fields = queryFields(qpigs, qpiri, qdop, qmod);
        var mqttEntityList = MqttUtil.getHaMqttEntities(fields, topicPrefix, deviceName, serialHandler);

        // add command MQTT entity for receiving raw commands
        var rawCommandField = new Field<>("Raw Command Receiver", s -> s, WriteCommandHandlers::rawCommandHandler);
        var commandEntity = new HomeAssistantMqttText(rawCommandField, topicPrefix, deviceName);
        mqttEntityList.put(commandEntity.getName(), commandEntity);

        // MQTT entities for communicating program state to HA
        var errorCountEntity = new HomeAssistantMqttSensor("Serial Error Count", topicPrefix, deviceName);
        mqttEntityList.put(errorCountEntity.getName(), errorCountEntity);

        // MQTT subscriptions and handling
        mqttSubscriptions(mqttSubscriber, mqttPublisher, mqttEntityList, serialHandler);

        // HA MQTT discovery configurations on program start
        MqttUtil.publishConfigForHaMqttEntities(mqttEntityList, mqttPublisher);
        log("[MQTT] Published MQTT discovery configurations for Home Assistant");


        // serial query loop
        try {
            while (true) {
                // get updates from inverter and publish to MQTT
                if (serialHandler.isOpen()) {
                    // run query commands and get values
                    var values = qpigs.run(serialHandler);
                    values.putAll(qpiri.run(serialHandler));
                    values.putAll(qdop.run(serialHandler));
                    values.putAll(qmod.run(serialHandler));
                    if (values.keySet().isEmpty()) {
                        log("[Serial] No values received from serial port " + serialHandler.getSystemPortName() + ", check config!");
                    } else {
                        // match values against corresponding MQTT entities and publish to MQTT
                        var jsonMapper = new ObjectMapper();
                        for (var valueKey: values.keySet()) {
                            var value = values.get(valueKey);
                            if (mqttEntityList.containsKey(valueKey)) {
                                var haMqtt = mqttEntityList.get(valueKey);
                                var valueString = value.getClass().isRecord()? jsonMapper.writeValueAsString(value) : value.toString();
                                mqttPublisher.publish(haMqtt.getStateTopic(), new MqttMessage(valueString.getBytes()));
                            }
                        }
                        log("[MQTT] Published updates from inverter to MQTT");
                    }
                    // send error count to HA
                    mqttPublisher.publish(errorCountEntity.getStateTopic(), new MqttMessage(String.valueOf(serialHandler.errorCount()).getBytes()));

                    // retry serial connection on too many failures
                    if (serialHandler.errorCount() > 20) {
                        log("[Serial] Failed communicating for " + serialHandler.errorCount() + " times, attempting to re-initialize serial device");
                        while (!serialHandler.reinit()) {
                            log("[Serial] Re-initialization failed, delaying 5 seconds");
                            Thread.sleep(5000);
                        }
                        log("[Serial] Re-initialization successful");
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
            serialHandler.close();
        }
    }

    private static ArrayList<Field> queryFields(MapResponseCommand ...commands) {
        var fields = new ArrayList<Field>();
        Arrays.stream(commands).forEach(command -> fields.addAll(command.getFields()));
        return fields;
    }

    private static MqttClient createMqttClient(MqttConnectionOptions options, String serverUrl, String clientId) throws MqttException {
        var client = new MqttClient(serverUrl, clientId);
        client.connect(options);
        return client;
    }

    private static Properties loadMqttConfig(String filename) throws IOException {
        var properties = new Properties();
        properties.load(new FileReader(filename));
        return properties;
    }

    private static Properties loadSerialConfig(String filename) throws IOException {
        var properties = new Properties();
        if (Files.exists(Path.of(filename))) {
            properties.load(new FileReader(filename));
        } else {
            properties.put("port", "/dev/ttyUSB0");
        }
        return properties;
    }

    private static void mqttSubscriptions(MqttClient mqttSubscriber, MqttClient mqttPublisher, HashMap<String, HomeAssistantMqttEntityBase> mqttEntityList, SerialHandler serialHandler) throws MqttException {
        // subscriptions
        mqttSubscriber.subscribe(haStatusTopic, 0);
        for (var mqttEntity: mqttEntityList.values()) {
            if (!mqttEntity.getCommandTopic().isEmpty()) mqttSubscriber.subscribe(mqttEntity.getCommandTopic(), 0);
        }

        // handling
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
    }


}
