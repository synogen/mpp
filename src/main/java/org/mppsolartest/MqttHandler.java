package org.mppsolartest;

import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

public class MqttHandler implements MqttCallback {

    private final IMqttAsyncClient client;

    public MqttHandler(IMqttAsyncClient client) throws MqttException {
        this.client = client;
        var statusSub = client.subscribe("testing/status",0);
        statusSub.waitForCompletion(5000);
    }

    @Override
    public void disconnected(MqttDisconnectResponse disconnectResponse) {

    }

    @Override
    public void mqttErrorOccurred(MqttException exception) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        System.out.println(topic + " " + message.toString());
    }

    @Override
    public void deliveryComplete(IMqttToken token) {

    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {

    }

    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties) {

    }
}
