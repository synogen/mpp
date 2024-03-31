package org.mppsolartest.model;

import org.mppsolartest.mqtt.HAType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Field<T> {

    private final String description;
    private Function<String, T> converter;
    private List<Field> subfields = new ArrayList<>();
    private HAType haType = HAType.SENSOR;

    private Map options;

    public Field(String description, Function<String, T> converter) {
        this.description = description;
        this.converter = converter;
    }

    public Field(String description, Function<String, T> converter, HAType haType) {
        this.description = description;
        this.converter = converter;
        this.haType = haType;
    }

    public Field(String description, Function<String, T> converter, Map<Integer, String> options) {
        this.description = description;
        this.converter = converter;
        this.options = options;
        this.haType = HAType.SELECT;
    }

    public Field(String description, List<Field> subfields) {
        this.description = description;
        this.subfields = subfields;
        this.haType = HAType.MULTIFLAG;
    }

    public String description() {
        return description;
    }

    public Function<String, T> converter() {
        return converter;
    }

    public List<Field> subfields() {
        return subfields;
    }

    public HAType haType() {
        return haType;
    }

    public Map options() {
        return options;
    }
}
