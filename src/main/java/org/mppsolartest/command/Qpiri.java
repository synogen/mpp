package org.mppsolartest.command;

import org.mppsolartest.model.Field;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Qpiri extends ArrayResponseCommand {
    @Override
    public String getCommand() {
        return "QPIRI";
    }

    @Override
    List<Field> getFields() {
        return List.of(
                new Field<>("Grid rating voltage in V", BigDecimal::new),
                new Field<>("Grid rating current in A", BigDecimal::new),
                new Field<>("AC output rating voltage in V", BigDecimal::new),
                new Field<>("AC output rating frequency in Hz", BigDecimal::new),
                new Field<>("AC output rating current in A", BigDecimal::new),
                new Field<>("AC output rating apparent power in VA", Integer::valueOf),
                new Field<>("AC output rating active power in W", Integer::valueOf),
                new Field<>("Battery rating voltage in V", BigDecimal::new),
                new Field<>("Battery re-charge voltage in V", BigDecimal::new),
                new Field<>("Battery under voltage in V", BigDecimal::new),
                new Field<>("Battery bulk voltage in V", BigDecimal::new),
                new Field<>("Battery float voltage in V", BigDecimal::new),
                new Field<>("Battery type", s -> mapFrom(s,
                        Map.ofEntries(
                                entry(0, "AGM"),
                                entry(1, "Flooded"),
                                entry(2, "User"),
                                entry(9, "LIC-protocol compatible battery")
                        ))
                ),
                new Field<>("Current max AC charging current in A", Integer::valueOf),
                new Field<>("Current max charging current in A", Integer::valueOf),
                new Field<>("Input voltage range", s -> mapFrom(s,
                        Map.ofEntries(
                                entry(0, "Appliance"),
                                entry(1, "UPS")
                        ))
                ),
                new Field<>("Output source priority", s -> mapFrom(s,
                        Map.ofEntries(
                                entry(0, "Utility first"),
                                entry(1, "Solar first"),
                                entry(2, "SBU")
                        ))
                ),
                new Field<>("Charger source priority", s -> mapFrom(s,
                        Map.ofEntries(
                                entry(0, "Utility first"),
                                entry(1, "Solar first"),
                                entry(2, "Solar + Utility"),
                                entry(3, "Only solar charging permitted if battery voltage not too low")
                        ))
                ),
                new Field<>("Parallel max number", Integer::valueOf),
                new Field<>("Machine type", s -> mapFrom(s,
                        Map.ofEntries(
                                entry(0, "Grid tie"),
                                entry(1, "Offgrid"),
                                entry(10, "Hybrid")
                        ))
                ),
                new Field<>("Topology", s -> mapFrom(s,
                        Map.ofEntries(
                                entry(0, "Transformerless"),
                                entry(1, "Transformer")
                        ))
                ),
                new Field<>("Output mode", s -> mapFrom(s,
                        Map.ofEntries(
                                entry(0, "single"),
                                entry(1, "parallel output(0°)"),
                                entry(2, "Phase 1 of 3 Phase output"),
                                entry(3, "Phase 2 of 3 Phase output"),
                                entry(4, "Phase 3 of 3 Phase output"),
                                entry(5, "parallel output(120°)"),
                                entry(6, "parallel output(180°)")
                        ))
                ),
                new Field<>("Battery re-discharge voltage", BigDecimal::new),
                new Field<>("PV OK condition for parallel", s -> mapFrom(s,
                        Map.ofEntries(
                                entry(0, "As long as one unit of inverters has connect PV, parallel system will consider PV OK"),
                                entry(1, "Only All of inverters have connect PV, parallel system will consider PV OK")
                        ))
                ),
                new Field<>("PV power balance", s -> mapFrom(s,
                        Map.ofEntries(
                                entry(0, "PV input max current will be the max charged current"),
                                entry(1, " PV input max power will be the sum of the max charged power and loads power")
                        ))
                ),
                new Field<>("Max. charging time at C.V stage", Integer::valueOf)
        );
    }
}
