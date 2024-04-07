package org.mppsolartest.command;

import org.mppsolartest.model.Field;
import org.mppsolartest.mqtt.HAType;

import java.math.BigDecimal;
import java.util.List;

public class Qdop extends MapResponseCommand {
    @Override
    public String getCommand() {
        return "QDOP";
    }

    @Override
    public List<Field> getFields() {
        return List.of(
                new Field<>("Unknown 1", Integer::valueOf),
                new Field<>("Unknown 2", Integer::valueOf),
                new Field<>("Unknown 3", Integer::valueOf),
                new Field<>("Unknown 4", Integer::valueOf),
                new Field<>("Unknown 5", BigDecimal::new),
                new Field<>("Unknown 6", BigDecimal::new),
                new Field<>("Unknown 7", Integer::valueOf),
                new Field<>("Unknown 8", Integer::valueOf),
                new Field<>("Battery back to grid capacity", Integer::valueOf, HAType.NUMBER, WriteCommandHandlers::pbccCommandHandler),
                new Field<>("Battery back to discharge capacity", Integer::valueOf, HAType.NUMBER, WriteCommandHandlers::pbdcCommandHandler),
                new Field<>("Battery cut-off capacity", Integer::valueOf, HAType.NUMBER, WriteCommandHandlers::psdcCommandHandler),
                new Field<>("Unknown 12", Integer::valueOf),
                new Field<>("Unknown 13", Integer::valueOf),
                new Field<>("Unknown 14", Integer::valueOf)
        );
    }
}
