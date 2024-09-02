package org.mppsolartest.command;

import org.mppsolartest.model.Field;
import org.mppsolartest.mqtt.HAType;

import java.math.BigDecimal;
import java.util.List;

public class Qpigs extends MapResponseCommand {
    @Override
    public String getCommand() {
        return "QPIGS";
    }

    @Override
    public List<Field> getFields() {
        return List.of(
                new Field<>("AC input voltage in V", BigDecimal::new),
                new Field<>("AC input frequency in Hz", BigDecimal::new),
                new Field<>("AC output voltage in V", BigDecimal::new),
                new Field<>("AC output frequency in Hz", BigDecimal::new),
                new Field<>("AC output apparent power in VA", Integer::valueOf),
                new Field<>("AC output active power in W", Integer::valueOf),
                new Field<>("Output load percentage", Integer::valueOf),
                new Field<>("BUS voltage", Integer::valueOf),
                new Field<>("Battery voltage in V", BigDecimal::new),
                new Field<>("Battery charging current in A", Integer::valueOf),
                new Field<>("Battery capacity percentage", Integer::valueOf, true, true),
                new Field<>("Inverter heat sink temperature in C°", Integer::valueOf),
                new Field<>("PV Input current for battery in A", BigDecimal::new),
                new Field<>("PV Input voltage", BigDecimal::new),
                new Field<>("Battery voltage from SCC in V", BigDecimal::new),
                new Field<>("Battery discharge current in A", Integer::valueOf),
                new Field<>("Status Flags 1", List.of(
                        new Field<>("PV or AC feed the load", this::toOnOff, HAType.BINARY),
                        new Field<>("Configuration change", this::toOnOff, HAType.BINARY),
                        new Field<>("SCC firmware version updated", this::toOnOff, HAType.BINARY),
                        new Field<>("Load on", this::toOnOff, HAType.BINARY),
                        new Field<>("Status Flags 1 Unknown flag 5", this::toOnOff, HAType.BINARY),
                        new Field<>("Charging on", this::toOnOff, HAType.BINARY),
                        new Field<>("Solar Charging on", this::toOnOff, HAType.BINARY),
                        new Field<>("AC Charging on", this::toOnOff, HAType.BINARY)
                )),
                new Field<>("Battery voltage offset for fans on", s -> s),
                new Field<>("EEPROM version", s -> s),
                new Field<>("PV Charging power in W", Integer::valueOf),
                new Field<>("Status Flags 2", s -> s)
        );
    }
}
