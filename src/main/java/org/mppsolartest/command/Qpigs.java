package org.mppsolartest.command;

import org.mppsolartest.model.Field;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Qpigs extends ArrayResponseCommand {
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
                new Field<>("Battery capacity percentage", Integer::valueOf),
                new Field<>("Inverter heat sink temperature in C°", Integer::valueOf),
                new Field<>("PV Input current for battery in A", BigDecimal::new),
                new Field<>("PV Input voltage", BigDecimal::new),
                new Field<>("Battery voltage from SCC in V", BigDecimal::new),
                new Field<>("Battery discharge current in A", Integer::valueOf),
                new Field<>("Status Flags 1", s -> s),
                new Field<>("Reserved", s -> s),
                new Field<>("Reserved 2", s -> s),
                new Field<>("PV Charging power in W", Integer::valueOf),
                new Field<>("Status Flags 2", s -> s)
        );
    }
}
