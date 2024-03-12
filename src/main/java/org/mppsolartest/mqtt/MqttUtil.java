package org.mppsolartest.mqtt;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.mppsolartest.model.Field;

import java.util.HashMap;
import java.util.List;

public class MqttUtil {

    public static HashMap<String, HomeAssistantMqtt> getHaMqttEntities(List<Field> fields, String topicPrefix, String deviceName) {
        var mqttEntityList = new HashMap<String, HomeAssistantMqtt>();
        for (var field: fields) {
            var haMqtt = HomeAssistantMqtt.forField(field, topicPrefix, deviceName);
            mqttEntityList.put(field.description(), haMqtt);
        }
        return mqttEntityList;
    }

    public static void publishConfigForHaMqttEntities(HashMap<String, HomeAssistantMqtt> entities, MqttClient mqttClient) throws MqttException {
        for (var entity: entities.values()) {
            mqttClient.publish(entity.getConfigTopic(), new MqttMessage(entity.getConfigJson().getBytes()));
        }
    }
}
