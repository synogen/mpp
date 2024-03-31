package org.mppsolartest.mqtt;

import org.mppsolartest.model.Field;

public class HomeAssistantMqttSensor extends HomeAssistantMqttEntityBase {

    private HomeAssistantMqttSensor(){}

    private HomeAssistantMqttSensor(String name, String unit, String stateTopic, String uniqueId, String deviceName) {
        getConfig().baseConfig(name, stateTopic, uniqueId, deviceName);
        if (!unit.isBlank()) getConfig().unit(unit);

        this.setStateTopic(stateTopic);
        this.setName(name);
    }
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
}
