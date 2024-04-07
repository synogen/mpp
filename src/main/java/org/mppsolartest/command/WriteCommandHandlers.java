package org.mppsolartest.command;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.mppsolartest.Log;
import org.mppsolartest.mqtt.HomeAssistantMqttEntityBase;
import org.mppsolartest.serial.SerialHandler;

public class WriteCommandHandlers {
    public static void rawCommandHandler(String message, SerialHandler serialHandler, MqttClient mqttClient, HomeAssistantMqttEntityBase mqttEntity) throws Exception {
        var response = serialHandler.excuteSimpleCommand(message);
        if (response.isEmpty()) response = "Empty response received, check serial configuration";
        mqttClient.publish(mqttEntity.getStateTopic(), new MqttMessage(response.getBytes()));
    }

    public static void pbccCommandHandler(String message, SerialHandler serialHandler, MqttClient mqttClient, HomeAssistantMqttEntityBase mqttEntity) throws Exception {
        setNumberCommand("PBCC", 3, message, serialHandler, mqttClient, mqttEntity);
    }

    public static void pbdcCommandHandler(String message, SerialHandler serialHandler, MqttClient mqttClient, HomeAssistantMqttEntityBase mqttEntity) throws Exception {
        setNumberCommand("PBDC",3, message, serialHandler, mqttClient, mqttEntity);
    }

    public static void psdcCommandHandler(String message, SerialHandler serialHandler, MqttClient mqttClient, HomeAssistantMqttEntityBase mqttEntity) throws Exception {
        setNumberCommand("PSDC", 3, message, serialHandler, mqttClient, mqttEntity);
    }

    public static void pcpCommandHandler(String message, SerialHandler serialHandler, MqttClient mqttClient, HomeAssistantMqttEntityBase mqttEntity) throws Exception {
        setNumberCommandWithCodeMapping("PCP", message, serialHandler, mqttClient, mqttEntity);
    }

    public static void popCommandHandler(String message, SerialHandler serialHandler, MqttClient mqttClient, HomeAssistantMqttEntityBase mqttEntity) throws Exception {
        setNumberCommandWithCodeMapping("POP", message, serialHandler, mqttClient, mqttEntity);
    }

    public static void muchgcCommandHandler(String message, SerialHandler serialHandler, MqttClient mqttClient, HomeAssistantMqttEntityBase mqttEntity) throws Exception {
        setNumberCommand("MUCHGC", 3, message, serialHandler, mqttClient, mqttEntity);
    }

    public static void mnchgcCommandHandler(String message, SerialHandler serialHandler, MqttClient mqttClient, HomeAssistantMqttEntityBase mqttEntity) throws Exception {
        setNumberCommand("MNCHGC", 3, message, serialHandler, mqttClient, mqttEntity);
    }

    public static void setNumberCommandWithCodeMapping(String command, String message, SerialHandler serialHandler, MqttClient mqttClient, HomeAssistantMqttEntityBase mqttEntity) throws Exception {
        var options = mqttEntity.getOptions();
        for (var code: options.keySet()) {
            if (message.equalsIgnoreCase(options.get(code))) {
                if (runSetNumberCommand(command, 2, code, serialHandler, mqttClient, mqttEntity))
                    // tell HA that the value was set successfully by publishing an update to the MQTT entities state
                    mqttClient.publish(mqttEntity.getStateTopic(), new MqttMessage(message.getBytes()));
                break;
            }
        }
    }

    private static void setNumberCommand(String command, Integer numberPaddedLength, String value, SerialHandler serialHandler, MqttClient mqttClient, HomeAssistantMqttEntityBase mqttEntity) throws MqttException {
        var intValue = Integer.parseInt(value);
        if (runSetNumberCommand(command, numberPaddedLength, intValue, serialHandler, mqttClient, mqttEntity))
            // tell HA that the value was set successfully by publishing an update to the MQTT entities state
            mqttClient.publish(mqttEntity.getStateTopic(), new MqttMessage(value.getBytes()));
    }

    private static boolean runSetNumberCommand(String command, Integer numberPaddedLength, Integer value, SerialHandler serialHandler, MqttClient mqttClient, HomeAssistantMqttEntityBase mqttEntity) throws MqttException {
        var success = false;
        var valueString = String.format("%0" + numberPaddedLength + "d", value);
        var response = serialHandler.excuteSimpleCommand(command + valueString);
        success = response.equalsIgnoreCase("(ACK");
        if (response.isEmpty()) Log.log("[Serial] Empty response received, check serial configuration");
        if (response.equalsIgnoreCase("(NAK")) Log.log("[Serial] Inverter denied command " + command + valueString);
        return success;
    }
}
