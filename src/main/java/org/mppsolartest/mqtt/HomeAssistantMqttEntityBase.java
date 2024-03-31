package org.mppsolartest.mqtt;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.mppsolartest.serial.SerialHandler;

public abstract class HomeAssistantMqttEntityBase {

    private ConfigJson configJson = new ConfigJson();
    private String name;

    private String stateTopic;
    private String commandTopic = "";

    public String getConfigJson() {
        return configJson.getJson();
    }

    public ConfigJson getConfig() {
        return configJson;
    }

    public String getStateTopic() {
        return stateTopic;
    }

    public void setStateTopic(String stateTopic) {
        this.stateTopic = stateTopic;
    }

    public String getConfigTopic() {
        return stateTopic + "/config";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public String getCommandTopic() {
        return commandTopic;
    }


    protected CommandFunction<String, SerialHandler, MqttClient, HomeAssistantMqttEntityBase> commandHandler = (message, serialHandler, mqttClient, mqttEntityBase) -> {
        throw new RuntimeException(this.getClass() + " " + this.getName() + " has no command function");
    };

    public CommandFunction<String, SerialHandler, MqttClient, HomeAssistantMqttEntityBase> getCommandHandler() {
        return commandHandler;
    }

    public void setCommandHandler(CommandFunction<String, SerialHandler, MqttClient, HomeAssistantMqttEntityBase> commandHandler) {
        this.commandHandler = commandHandler;
        // automatically set default command topic if a command handler is set
        getConfig().commandTopic(getStateTopic() + "/set");
        commandTopic = getStateTopic() + "/set";
    }

}
