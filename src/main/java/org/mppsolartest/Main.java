package org.mppsolartest;

import com.fazecast.jSerialComm.SerialPort;
import org.mppsolartest.command.Qpigs;
import org.mppsolartest.command.Qpiri;
import org.mppsolartest.communication.SerialHandler;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // configure com port
        var ports = SerialPort.getCommPorts();
        for (int i = 0; i < ports.length; i++) {
            var number = i + 1;
            System.out.println(number + ". " + ports[i].getSystemPortName());
        }
        var comChoice = new Scanner(System.in).nextInt() - 1;

        // query
        var port = ports[comChoice];
        if (port.openPort()) {
            var serialHandler = new SerialHandler(port);
            var commandChoice = "";
            System.out.println(new Qpigs().run(serialHandler));
            while (true) {
                // get command
                System.out.println("Command (\"exit\" to quit):");
                commandChoice = new Scanner(System.in).next();
                if (!"exit".equalsIgnoreCase(commandChoice)) {
                    System.out.println(serialHandler.excuteSimpleCommand(commandChoice));
                } else {
                    break;
                }
            }
            serialHandler.close();
        }

        // TODO implement commands from manual
        // TODO MQTT publishing/receiving?
        // TODO or just set it directly with some sort of configuration here instead of using home assistant?
    }
}