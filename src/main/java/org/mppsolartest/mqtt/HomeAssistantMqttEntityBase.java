package org.mppsolartest.mqtt;

public abstract class HomeAssistantMqttEntityBase {
    public abstract String getConfigJson();

    public abstract String getStateTopic();

    public abstract String getConfigTopic();

    public abstract String getName();
}
