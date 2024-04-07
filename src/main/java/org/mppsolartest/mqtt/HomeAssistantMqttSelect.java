package org.mppsolartest.mqtt;

import org.mppsolartest.model.Field;
import org.mppsolartest.serial.SerialHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class HomeAssistantMqttSelect extends HomeAssistantMqttEntityBase {

    public HomeAssistantMqttSelect(Field field, String topicPrefix, String deviceName, SerialHandler serialHandler) {
        super(field, topicPrefix, deviceName, "select");

        Collection options = new ArrayList<>();
        if (getField().optionsMap() != null) options = getField().optionsMap().values();
        if (getField().inverterOptionListQuery() != null) options = (Collection) getField().inverterOptionListQuery().apply(serialHandler);
        options = options.stream().map(String::valueOf).toList(); // in case we get integer options
        getConfig().options(options);
    }

    @Override
    public Map<Integer, String> getOptions() {
        return getField().optionsMap();
    }
}
