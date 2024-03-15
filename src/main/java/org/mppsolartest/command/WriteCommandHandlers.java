package org.mppsolartest.command;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.mppsolartest.mqtt.HomeAssistantMqttEntityBase;
import org.mppsolartest.serial.SerialHandler;

public class WriteCommandHandlers {
    public static void rawCommandHandler(String message, SerialHandler serialHandler, MqttClient mqttClient, HomeAssistantMqttEntityBase mqttEntity) throws Exception {
        var response = serialHandler.excuteSimpleCommand(message.toString());
        if (response.isEmpty()) response = "Empty response received, check serial configuration";
        mqttClient.publish(mqttEntity.getStateTopic(), new MqttMessage(response.getBytes()));
    }
}
