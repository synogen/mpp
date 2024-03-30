package org.mppsolartest.mqtt;

public class HomeAssistantMqttText extends HomeAssistantMqttEntityBase {

    private ConfigJson configJson = new ConfigJson();

    private String name;

    private HomeAssistantMqttText(){};

    public HomeAssistantMqttText(String name, String topicPrefix, String deviceName) {
        var uniqueId = (deviceName.length() > 0? deviceName.toLowerCase().replaceAll(" ", "_") + "_" : "") + name.toLowerCase().replaceAll(" ", "_");

        this.setStateTopic((topicPrefix.length() > 0 ? topicPrefix + "/" : "") + "text/" + uniqueId);
        this.name = name;

        configJson.baseConfig(this.name, getStateTopic(), uniqueId, deviceName);
    };

    public String getConfigJson() {
        return configJson.getJson();
    }

    @Override
    public ConfigJson getConfig() {
        return configJson;
    }

    @Override
    public String getName() {
        return name;
    }
}
