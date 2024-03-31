package org.mppsolartest.mqtt;

public class HomeAssistantMqttText extends HomeAssistantMqttEntityBase {

    private HomeAssistantMqttText(){};

    public HomeAssistantMqttText(String name, String topicPrefix, String deviceName) {
        var uniqueId = (deviceName.length() > 0? deviceName.toLowerCase().replaceAll(" ", "_") + "_" : "") + name.toLowerCase().replaceAll(" ", "_");

        this.setStateTopic((topicPrefix.length() > 0 ? topicPrefix + "/" : "") + "text/" + uniqueId);
        this.setName(name);

        getConfig().baseConfig(this.getName(), getStateTopic(), uniqueId, deviceName);
    };

}
