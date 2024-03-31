package org.mppsolartest.mqtt;

import java.util.Map;

public class HomeAssistantMqttSelect extends HomeAssistantMqttEntityBase {

    public HomeAssistantMqttSelect(String name, String topicPrefix, String deviceName, Map<Integer, String> options) {
        super(name, topicPrefix, deviceName, "select");
        getConfig().options(options.values());
    }

}
