package org.mppsolartest.mqtt;

public class HomeAssistantMqttNumber extends HomeAssistantMqttEntityBase {

    private HomeAssistantMqttNumber(){}

    public HomeAssistantMqttNumber(String name, String topicPrefix, String deviceName) {
        var uniqueId = (deviceName.length() > 0? deviceName.toLowerCase().replaceAll(" ", "_") + "_" : "") + name.toLowerCase().replaceAll(" ", "_");
        this.setStateTopic((topicPrefix.length() > 0 ? topicPrefix + "/" : "") + "number/" + uniqueId);
        this.setName(name);

        getConfig().baseConfig(name, getStateTopic(), uniqueId, deviceName);
        if (name.toLowerCase().contains("capacity")) getConfig().unit("%");
    }
}
