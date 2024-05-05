package org.mppsolartest.command;

import org.mppsolartest.model.Field;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Qmod extends MapResponseCommand {
    @Override
    public String getCommand() {
        return "QMOD";
    }

    @Override
    public List<Field> getFields() {
        return List.of(
                new Field<>("Inverter Mode", s -> mapStringCodes(s,
                        Map.ofEntries(
                                entry("P", "Power On Mode"),
                                entry("S", "Standby Mode"),
                                entry("L", "Line Mode"),
                                entry("B", "Battery Mode "),
                                entry("F", "Fault Mode"),
                                entry("H", "Power Saving Mode")
                        )),
                        false
                )
        );
    }
}
