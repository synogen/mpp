package org.mppsolartest.mqtt;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigJson {

    private List<String> entries = new ArrayList<>();

    public void addEntry(String entry) {
        entries.add(entry);
    }

    public String getJson() {
        return """
                {
                    %s,
                    "origin": {
                        "name": "MQTT Java Test",
                        "sw_version": "testing",
                        "support_url": "https://github.com/synogen/mpp"
                    }
                }"""
                .formatted(entries.stream().collect(Collectors.joining(",\n\t"))
        );
    }

    private void simpleString(String key, String value) {
        entries.add("\"%s\": \"%s\"".formatted(key, value));
    }

    private void simpleNumber(String key, String value) {
        entries.add("\"%s\": %s".formatted(key, value));
    }

    public void device(String name) {
        entries.add("""
                "device": {
                    "name": "%s",
                    "identifiers": [
                        "%s"
                    ]
                }""".formatted(name, name.toLowerCase().replaceAll(" ", "_")).replaceAll("\n", "\n\t"));
    }

    public void uniqueId(String uniqueId) {
        simpleString("unique_id", uniqueId);
    }

    public void commandTopic(String commandTopic) {
        simpleString("command_topic", commandTopic);
    }

    public void stateTopic(String stateTopic) {
        simpleString("state_topic", stateTopic);
    }

    public void name(String name) {
        simpleString("name", name);
    }

    public void unit(String unit) {
        simpleString("unit_of_measurement", unit);
    }

    public void min(Integer number) {
        simpleNumber("min", String.valueOf(number));
    }

    public void max(Integer number) {
        simpleNumber("max", String.valueOf(number));
    }

    public void mode(String mode) {
        simpleString("mode", mode);
    }

    public void baseConfig(String name, String stateTopic, String uniqueId, String device) {
        name(name);
        stateTopic(stateTopic);
        uniqueId(uniqueId);
        device(device);
    }
}
