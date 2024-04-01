package org.mppsolartest.mqtt;

import org.mppsolartest.model.Field;

public class HomeAssistantMqttBinary extends HomeAssistantMqttEntityBase {

    public HomeAssistantMqttBinary(Field field, String topicPrefix, String deviceName) {
        super(field, topicPrefix, deviceName, "binary_sensor");
    }

}
