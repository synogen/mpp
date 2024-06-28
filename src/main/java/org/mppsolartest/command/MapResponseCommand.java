package org.mppsolartest.command;

import org.mppsolartest.model.CodeValue;
import org.mppsolartest.model.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class MapResponseCommand extends Command<HashMap<String, Object>> {

    public abstract List<Field> getFields();
    @Override
    public HashMap<String, Object> parseResponse(String response) {
        var result = new HashMap<String, Object>();
        if (response.isEmpty() || response.equalsIgnoreCase("(NAK")) return result; //in case of empty or (NAK response
        var rParts = response.substring(1).split(" ");
        for (int i = 0; i < rParts.length; i++) {
            if (getFields().size() > i) {
                var field = getFields().get(i);
                var value = rParts[i];
                if (field.subfields().isEmpty()) {
                    // no subfields, convert value directly
                    var convertedValue = field.convert(value);
                    result.put(field.description(), convertedValue);
                } else {
                    // fields with multiple flags in one value generate subfields
                    if (field.subfields().size() == value.length()) {
                        for (int j = 0; j < value.length(); j++) {
                            var subfield = (Field)field.subfields().get(j);
                            result.put(subfield.description(), subfield.convert(value.substring(j, j+1)));
                        }
                    }
                }
            } else {
                result.put("Unknown field " + rParts[i], 0);
            }
        }
        return result;
    }

    protected CodeValue mapStringCodes(String value, Map<String, String> map) {
        if (map.containsKey(value)) {
            return new CodeValue(value, map.get(value));
        } else {
            return new CodeValue(value, "Unknown (" + value + ")");
        }
    }

    protected CodeValue mapIntCodes(String value, Map<Integer, String> map) {
        var key = Integer.parseInt(value);
        if (map.containsKey(key)) {
            return new CodeValue(value, map.get(key));
        } else {
            return new CodeValue(value, "Unknown (" + value + ")");
        }
    }

    protected String mapIntCodesToString(String value, Map<Integer, String> map) {
        var key = Integer.parseInt(value);
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            return "Unknown (" + value + ")";
        }
    }

    protected String toOnOff(String s) {
        return Objects.equals(s, "1") ? "ON" : "OFF";
    }
}
