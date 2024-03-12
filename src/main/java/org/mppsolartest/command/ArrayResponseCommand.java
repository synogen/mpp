package org.mppsolartest.command;

import org.mppsolartest.model.CodeValue;
import org.mppsolartest.model.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                result.put(field.description(), field.converter().apply(rParts[i]));
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
}
