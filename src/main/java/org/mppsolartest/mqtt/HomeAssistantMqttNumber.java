package org.mppsolartest.mqtt;

public class HomeAssistantMqttNumber extends HomeAssistantMqttEntityBase {

    private ConfigJson configJson = new ConfigJson();

    private String stateTopic;
    private String name;

    private HomeAssistantMqttNumber(){};

    public HomeAssistantMqttNumber(String name, String topicPrefix, String deviceName) {
        var uniqueId = (deviceName.length() > 0? deviceName.toLowerCase().replaceAll(" ", "_") + "_" : "") + name.toLowerCase().replaceAll(" ", "_");
        this.stateTopic = (topicPrefix.length() > 0? topicPrefix + "/" : "") + "number/" + uniqueId;
        this.name = name;

        configJson.baseConfig(name, stateTopic, uniqueId, deviceName);
        if (name.toLowerCase().contains("capacity")) configJson.unit("%");
    };
    @Override
    public String getConfigJson() {
        return configJson.getJson();
    }

    @Override
    public ConfigJson getConfig() {
        return configJson;
    }

    public String getStateTopic() {
        return stateTopic;
    }

    public String getConfigTopic() {
        return stateTopic + "/config";
    }

    @Override
    public String getName() {
        return name;
    }
}
