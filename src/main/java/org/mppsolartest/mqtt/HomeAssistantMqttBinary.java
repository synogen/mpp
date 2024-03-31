package org.mppsolartest.mqtt;

public class HomeAssistantMqttBinary extends HomeAssistantMqttEntityBase {

    private HomeAssistantMqttBinary(){}

    public HomeAssistantMqttBinary(String name, String topicPrefix, String deviceName) {
        var uniqueId = (deviceName.length() > 0? deviceName.toLowerCase().replaceAll(" ", "_") + "_" : "") + name.toLowerCase().replaceAll(" ", "_");

        this.setStateTopic((topicPrefix.length() > 0 ? topicPrefix + "/" : "") + "binary_sensor/" + uniqueId);
        this.setName(name);

        getConfig().baseConfig(name, getStateTopic(), uniqueId, deviceName);
    }

}
