package org.mppsolartest.mqtt;

public class HomeAssistantMqttBinary extends HomeAssistantMqttEntityBase {

    public HomeAssistantMqttBinary(String name, String topicPrefix, String deviceName) {
        super(name, topicPrefix, deviceName, "binary_sensor");
    }

}
