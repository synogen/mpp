package org.mppsolartest.mqtt;

import org.mppsolartest.model.Field;

public class HomeAssistantMqttSensor extends HomeAssistantMqttEntityBase {

    private String configJson = """
            {
                "name": "%s",
                "unit_of_measurement": "%s",
                "state_topic": "%s",
                "unique_id": "%s",
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
    private HomeAssistantMqttSensor(){};

    private HomeAssistantMqttSensor(String name, String unit, String stateTopic, String uniqueId, String deviceName) {
        configJson = configJson.formatted(name, unit, stateTopic, uniqueId, deviceName, deviceName.toLowerCase().replaceAll(" ", "_"));
        this.stateTopic = stateTopic;
        this.name = name;
    };
    public static HomeAssistantMqttSensor forField(Field field, String topicPrefix, String deviceName) {
        var name = field.description();

        // look for unit definition suffix "in <unit>", for example "A" for "Current in A"
        var nameParts = name.split(" ");
        var unit = nameParts.length > 2 && nameParts[nameParts.length-2].equalsIgnoreCase("in")? nameParts[nameParts.length-1] : "";
        // unit fallbacks
        if (unit.isEmpty() && name.toLowerCase().contains("voltage")) unit = "V";
        if (unit.isEmpty() && name.toLowerCase().contains("percent")) unit = "%";

        var uniqueId = (deviceName.length() > 0? deviceName.toLowerCase().replaceAll(" ", "_") + "_" : "") + name.toLowerCase().replaceAll(" ", "_");

        var state_topic = (topicPrefix.length() > 0? topicPrefix + "/" : "") + "sensor/" + uniqueId;

        return new HomeAssistantMqttSensor(name, unit, state_topic, uniqueId, deviceName);
    }

    @Override
    public String getConfigJson() {
        return configJson;
    }

    @Override
    public String getStateTopic() {
        return stateTopic;
    }

    @Override
    public String getConfigTopic() {
        return stateTopic + "/config";
    }

    @Override
    public String getName() {
        return name;
    }
}
