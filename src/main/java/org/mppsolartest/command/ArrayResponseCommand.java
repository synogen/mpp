package org.mppsolartest.command;

import org.mppsolartest.model.CodeValue;
import org.mppsolartest.model.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class ArrayResponseCommand extends Command<HashMap<String, Object>> {

    public abstract List<Field> getFields();
    @Override
    public HashMap<String, Object> parseResponse(String response) {
        var result = new HashMap<String, Object>();
        if (response.length() < 5) return result; //in case of empty or (NAK response
        var rParts = response.substring(1).split(" ");
        for (int i = 0; i < rParts.length; i++) {
            if (getFields().size() > i) {
                var field = getFields().get(i);
                var value = rParts[i];
                if (field.subfields().isEmpty()) {
                    // no subfields, convert value directly
                    var convertedValue = field.converter().apply(value);
                    result.put(field.description(), convertedValue);
                } else {
                    // fields with multiple flags in one value generate subfields
                    if (field.subfields().size() == value.length()) {
                        for (int j = 0; j < value.length(); j++) {
                            var subfield = (Field)field.subfields().get(j);
                            result.put(subfield.description(), subfield.converter().apply(value.substring(j, j+1)));
                        }
                    }
                }
            } else {
                result.put("Unknown field " + rParts[i], 0);
            }
        }
        return result;
    }

    protected CodeValue mapFrom(String value, Map<Integer, String> map) {
        var code = Integer.valueOf(value);
        if (map.containsKey(code)) {
            return new CodeValue(code, map.get(code));
        } else {
            return new CodeValue(code, "Unknown (" + code + ")");
        }
    }

    protected String toOnOff(String s) {
        return Objects.equals(s, "1") ? "ON" : "OFF";
    }
}
