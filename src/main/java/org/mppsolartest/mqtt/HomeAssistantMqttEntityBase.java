package org.mppsolartest.mqtt;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.mppsolartest.serial.SerialHandler;

public abstract class HomeAssistantMqttEntityBase {
    abstract public String getConfigJson();

    abstract public String getStateTopic();

    abstract public String getConfigTopic();

    abstract public String getName();
    abstract public String getCommandTopic();

    protected CommandFunction<String, SerialHandler, MqttClient, HomeAssistantMqttEntityBase> commandHandler = (message, serialHandler, mqttClient, mqttEntityBase) -> {
        throw new RuntimeException(this.getClass() + " " + this.getName() + " has no command function");
    };

    public CommandFunction<String, SerialHandler, MqttClient, HomeAssistantMqttEntityBase> getCommandHandler() {
        return commandHandler;
    }

    public void setCommandHandler(CommandFunction<String, SerialHandler, MqttClient, HomeAssistantMqttEntityBase> commandHandler) {
        this.commandHandler = commandHandler;
    }

}
