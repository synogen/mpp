package org.mppsolartest.mqtt;

import org.mppsolartest.model.Field;

import java.util.Map;

public class HomeAssistantMqttSelect extends HomeAssistantMqttEntityBase {

    public HomeAssistantMqttSelect(Field field, String topicPrefix, String deviceName) {
        super(field, topicPrefix, deviceName, "select");
        getConfig().options(getField().options().values());
    }

    @Override
    public Map<Integer, String> getOptions() {
        return getField().options();
    }
}
