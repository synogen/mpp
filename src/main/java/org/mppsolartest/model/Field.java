package org.mppsolartest.model;

import org.mppsolartest.mqtt.HAType;

import java.util.function.Function;

public class Field<T> {

    private String description;
    private Function<String, T> converter;
    private HAType haType = HAType.SENSOR;


    public Field(String description, Function<String, T> converter) {
        this.description = description;
        this.converter = converter;
    }

    public Field(String description, Function<String, T> converter, HAType haType) {
        this.description = description;
        this.converter = converter;
        this.haType = haType;
    }

    public String description() {
        return description;
    }

    public Function<String, T> converter() {
        return converter;
    }

    public HAType haType() {
        return haType;
    }
}
