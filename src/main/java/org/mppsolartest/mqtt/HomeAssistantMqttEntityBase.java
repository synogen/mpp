package org.mppsolartest.mqtt;

public abstract class HomeAssistantMqttEntityBase {
    abstract public String getConfigJson();

    abstract public String getStateTopic();

    abstract public String getConfigTopic();

    abstract public String getName();
}
