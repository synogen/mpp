package org.mppsolartest.mqtt;

public class HomeAssistantMqttNumber extends HomeAssistantMqttEntityBase {

    private String configJson = """
            {
                "name": "%s",
                "state_topic": "%s",
                "command_topic": "%s",
                "unique_id": "%s",
                "min": 0,
                "max": 100,
                "device": {
                    "name": "%s",
                    "identifiers": [
                        "%s"
                    ]
                },
                "origin": {
                    "name": "MQTT Java Test",
                    "sw_version": "testing",
                    "support_url": "https://github.com/synogen/mpp"
                }
            }
            """;

    private String stateTopic;
    private String name;

    private HomeAssistantMqttNumber(){};

    public HomeAssistantMqttNumber(String name, String topicPrefix, String deviceName) {
        var uniqueId = (deviceName.length() > 0? deviceName.toLowerCase().replaceAll(" ", "_") + "_" : "") + name.toLowerCase().replaceAll(" ", "_");

        this.stateTopic = (topicPrefix.length() > 0? topicPrefix + "/" : "") + "number/" + uniqueId;
        this.name = name;

        configJson = configJson.formatted(name, stateTopic, getCommandTopic(), uniqueId, deviceName, deviceName.toLowerCase().replaceAll(" ", "_"));
    };

    public String getConfigJson() {
        return configJson;
    }

    public String getStateTopic() {
        return stateTopic;
    }

    public String getConfigTopic() {
        return stateTopic + "/config";
    }

    @Override
    public String getName() {
        return name;
    }

    public String getCommandTopic() {
        return stateTopic + "/set";
    }
}
