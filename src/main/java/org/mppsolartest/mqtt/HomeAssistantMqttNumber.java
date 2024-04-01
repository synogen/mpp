package org.mppsolartest.mqtt;

import org.mppsolartest.model.Field;

public class HomeAssistantMqttNumber extends HomeAssistantMqttEntityBase {

    public HomeAssistantMqttNumber(Field field, String topicPrefix, String deviceName) {
        super(field, topicPrefix, deviceName, "number");
        var commandable = field.commandHandler() != null;
        if (getName().contains("capacity")) {
            getConfig().unit("%");
            if (commandable) {
                getConfig().min(0);
                getConfig().max(100);
            }
        }
        if (commandable) getConfig().mode("box");
    }
}
