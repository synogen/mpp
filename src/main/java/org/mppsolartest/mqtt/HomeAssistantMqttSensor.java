package org.mppsolartest.mqtt;

import org.mppsolartest.model.Field;

public class HomeAssistantMqttSensor extends HomeAssistantMqttEntityBase {

    public HomeAssistantMqttSensor(String name, String topicPrefix, String deviceName) {
        super(name, topicPrefix, deviceName, "sensor");
    }

    public HomeAssistantMqttSensor(Field field, String topicPrefix, String deviceName) {
        super(field, topicPrefix, deviceName, "sensor");

        // look for unit definition suffix "in <unit>", for example "A" for "Current in A"
        var nameParts = getName().split(" ");
        var unit = nameParts.length > 2 && nameParts[nameParts.length-2].equalsIgnoreCase("in")? nameParts[nameParts.length-1] : "";
        // unit fallbacks
        if (unit.isEmpty() && getName().toLowerCase().contains("voltage") && !getName().equalsIgnoreCase("Input voltage range")) unit = "V";
        if (unit.isEmpty() && getName().toLowerCase().contains("percent")) unit = "%";
        if (!unit.isBlank()) getConfig().unit(unit);

        if (field.measurement()) getConfig().measurement();
    }
}
