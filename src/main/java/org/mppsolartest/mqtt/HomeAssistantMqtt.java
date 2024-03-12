package org.mppsolartest.mqtt;

import org.mppsolartest.model.Field;

public class HomeAssistantMqtt {

    private String configJson = """
            {
                "name": "%s",
                "unit_of_measurement": "%s",
                "state_topic": "%s",
                "unique_id": "%s",
                "device": {
                    "name": "%s"
                    "identifiers": [
                        "%s"
                    ]
                },
                "origin": {
                    "name": "MQTT Java Test"
                    "sw_version": "testing"
                    "support_url": "https://github.com/synogen/mpp"
                }
            }
            """;

    private String stateTopic;

    private HomeAssistantMqtt(){};

    private HomeAssistantMqtt(String name, String unit, String stateTopic, String uniqueId, String deviceName) {
        configJson = configJson.formatted(name, unit, stateTopic, uniqueId, deviceName, deviceName.toLowerCase().replaceAll(" ", "_"));
        this.stateTopic = stateTopic;
    };
    public static HomeAssistantMqtt forField(Field field, String topicPrefix, String deviceName) {
        var name = field.description();

        var nameParts = name.split(" ");
        var unit = nameParts.length > 2 && nameParts[nameParts.length-2].equalsIgnoreCase("in")? nameParts[nameParts.length-1] : "";

        var uniqueId = (deviceName.length() > 0? deviceName.toLowerCase().replaceAll(" ", "_") + "_" : "") + name.toLowerCase().replaceAll(" ", "_");

        var state_topic = (topicPrefix.length() > 0? topicPrefix + "/" : "") + "sensor/" + uniqueId;

        return new HomeAssistantMqtt(name, unit, state_topic, uniqueId, deviceName);
    }

    public String getConfigJson() {
        return configJson;
    }

    public String getStateTopic() {
        return stateTopic;
    }

    public String getConfigTopic() {
        return stateTopic + "/config";
    }
}
