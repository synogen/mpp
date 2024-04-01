package org.mppsolartest.mqtt;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.mppsolartest.model.Field;
import org.mppsolartest.serial.SerialHandler;

import java.util.Map;

public abstract class HomeAssistantMqttEntityBase {

    private final ConfigJson configJson = new ConfigJson();

    private Field field;
    private String stateTopic;
    private String commandTopic = "";

    public HomeAssistantMqttEntityBase(Field field, String topicPrefix, String deviceName, String type) {
        this.field = field;

        var uniqueId = createUniqueId(deviceName, getName());
        this.setStateTopic(createStateTopic(topicPrefix, type, uniqueId));

        getConfig().baseConfig(getName(), getStateTopic(), uniqueId, deviceName);

        if (field.commandHandler() != null) {
            // automatically set default command topic if a command handler is set
            commandTopic = getStateTopic() + "/set";
            getConfig().commandTopic(commandTopic);
        }
    }

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

    public Map<Integer, String> getOptions() {
        return null;
    }

    public String getName() {
        return field.description();
    }
    public String getCommandTopic() {
        return commandTopic;
    }

    public Field getField() {
        return field;
    }

    private CommandFunction<String, SerialHandler, MqttClient, HomeAssistantMqttEntityBase> defaultCommandHandler = (message, serialHandler, mqttClient, mqttEntityBase) -> {
        throw new RuntimeException(this.getClass() + " " + this.getName() + " has no command function");
    };

    public CommandFunction<String, SerialHandler, MqttClient, HomeAssistantMqttEntityBase> getCommandHandler() {
        return field.commandHandler() != null? field.commandHandler() : defaultCommandHandler;
    }

    public String createUniqueId(String deviceName, String name) {
        return (deviceName.length() > 0? deviceName.toLowerCase().replaceAll(" ", "_") + "_" : "") + name.toLowerCase().replaceAll(" ", "_");
    }

    public String createStateTopic(String topicPrefix, String type, String uniqueId) {
        return (topicPrefix.length() > 0 ? topicPrefix + "/" : "") + type + "/" + uniqueId;
    }

}
