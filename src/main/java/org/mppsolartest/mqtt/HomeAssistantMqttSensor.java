package org.mppsolartest.mqtt;

public class HomeAssistantMqttSensor extends HomeAssistantMqttEntityBase {

    public HomeAssistantMqttSensor(String name, String topicPrefix, String deviceName) {
        super(name, topicPrefix, deviceName, "sensor");

        // look for unit definition suffix "in <unit>", for example "A" for "Current in A"
        var nameParts = name.split(" ");
        var unit = nameParts.length > 2 && nameParts[nameParts.length-2].equalsIgnoreCase("in")? nameParts[nameParts.length-1] : "";
        // unit fallbacks
        if (unit.isEmpty() && name.toLowerCase().contains("voltage")) unit = "V";
        if (unit.isEmpty() && name.toLowerCase().contains("percent")) unit = "%";
        if (!unit.isBlank()) getConfig().unit(unit);

    }
}
