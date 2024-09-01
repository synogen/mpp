package org.mppsolartest.command;

import org.mppsolartest.model.Field;

import java.math.BigDecimal;
import java.util.List;

public class Qpigs2 extends MapResponseCommand {
    @Override
    public String getCommand() {
        return "QPIGS2";
    }

    @Override
    public List<Field> getFields() {
        return List.of(
                new Field<>("PV2 Input current for battery in A", BigDecimal::new),
                new Field<>("PV2 Input voltage", BigDecimal::new),
                new Field<>("PV2 Charging power in W", Integer::valueOf)
        );
    }
}
