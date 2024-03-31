package org.mppsolartest.mqtt;

public class HomeAssistantMqttText extends HomeAssistantMqttEntityBase {

    public HomeAssistantMqttText(String name, String topicPrefix, String deviceName) {
        super(name, topicPrefix, deviceName, "text");
    }

}
