package org.mppsolartest.model;

import java.util.function.Function;

public record Field<T>(String description, Function<String, T> converter) {
}
