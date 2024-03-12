package org.mppsolartest;

import org.mppsolartest.model.Field;

public class HomeAssistantMqtt {

    private String configJson = """
            {
                "name": "%s",
                "unit_of_measurement": "%s",
                "state_topic": "%s"
            }
            """;

    private String stateTopic;

    private HomeAssistantMqtt(){};

    private HomeAssistantMqtt(String name, String unit, String stateTopic) {
        configJson = configJson.formatted(name, unit, stateTopic);
        this.stateTopic = stateTopic;
    };
    public static HomeAssistantMqtt forField(Field field, String topicPrefix, String sensorNamePrefix) {
        var name = field.description();

        var nameParts = name.split(" ");
        var unit = nameParts.length > 2 && nameParts[nameParts.length-2].equalsIgnoreCase("in")? nameParts[nameParts.length-1] : "";

        var state_topic = (topicPrefix.length() > 0? topicPrefix + "/" : "") + "sensor/" + (sensorNamePrefix.length() > 0? sensorNamePrefix + "_" : "") + name.toLowerCase().replaceAll(" ", "_");

        return new HomeAssistantMqtt(name, unit, state_topic);
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
