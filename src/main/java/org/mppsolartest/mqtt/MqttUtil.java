package org.mppsolartest.mqtt;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.mppsolartest.model.Field;

import java.util.HashMap;
import java.util.List;

public class MqttUtil {

    public static HashMap<String, HomeAssistantMqttEntityBase> getHaMqttEntities(List<Field> fields, String topicPrefix, String deviceName) {
        var mqttEntityList = new HashMap<String, HomeAssistantMqttEntityBase>();
        for (var field: fields) {
            var haMqtt = switch (field.haType()) {
                case TEXT: yield new HomeAssistantMqttText(field.description(), topicPrefix, deviceName);
                case NUMBER: yield new HomeAssistantMqttNumber(field.description(), topicPrefix, deviceName);
                case BINARY: yield new HomeAssistantMqttBinary(field.description(), topicPrefix, deviceName);
                case MULTIFLAG: mqttEntityList.putAll(getHaMqttEntities(field.subfields(), topicPrefix, deviceName)); yield null;
                case SELECT: yield new HomeAssistantMqttSelect(field.description(), topicPrefix, deviceName, field.options());
                default: yield new HomeAssistantMqttSensor(field.description(), topicPrefix, deviceName);
            };
            if (haMqtt != null) mqttEntityList.put(field.description(), haMqtt);
        }
        return mqttEntityList;
    }

    public static void publishConfigForHaMqttEntities(HashMap<String, HomeAssistantMqttEntityBase> entities, MqttClient mqttClient) throws MqttException {
        for (var entity: entities.values()) {
            mqttClient.publish(entity.getConfigTopic(), new MqttMessage(entity.getConfigJson().getBytes()));
        }
    }
}
