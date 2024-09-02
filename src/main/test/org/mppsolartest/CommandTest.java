package org.mppsolartest;

import org.mppsolartest.command.Qpiri;
import org.mppsolartest.model.Field;
import org.mppsolartest.mqtt.HomeAssistantMqttText;

import java.math.BigDecimal;

/**
 * Only for debug/testing purposes
 */
public class CommandTest {

    private static String qpiriExample = "(230.0 21.7 230.0 50.0 21.7 5000 5000 48.0 47.0 46.5 57.0 57.0 9 002 070 1 2 1 1 01 0 0 54.0 0 1";
    public static void main(String[] args) {
        var test = new Qpiri().parseResponse(qpiriExample);

        var f = new Field<>("AC input voltage in V", BigDecimal::new);

        System.out.println(new HomeAssistantMqttText(f, "hatest", "gew5048").getConfigJson());

        Log.logException(new UnsupportedOperationException("går inte"));

        System.out.println("Test");


    }


}
