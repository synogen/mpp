package org.mppsolartest.mqtt;
@FunctionalInterface
public interface CommandFunction<String, SerialHandler, MqttClient, HomeAssistantMqttEntityBase> {
    void apply(String message, SerialHandler serialHandler, MqttClient mqttClient, HomeAssistantMqttEntityBase mqttEntity) throws Exception;
}
