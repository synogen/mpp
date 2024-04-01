package org.mppsolartest.mqtt;

import org.mppsolartest.model.Field;

public class HomeAssistantMqttText extends HomeAssistantMqttEntityBase {

    public HomeAssistantMqttText(Field field, String topicPrefix, String deviceName) {
        super(field, topicPrefix, deviceName, "text");
    }

}
