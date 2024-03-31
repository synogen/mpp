package org.mppsolartest.mqtt;

public class HomeAssistantMqttNumber extends HomeAssistantMqttEntityBase {

    public HomeAssistantMqttNumber(String name, String topicPrefix, String deviceName) {
        super(name, topicPrefix, deviceName, "number");
        if (name.toLowerCase().contains("capacity")) getConfig().unit("%");
    }
}
