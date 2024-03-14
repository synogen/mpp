package org.mppsolartest;

import com.fazecast.jSerialComm.SerialPort;
import org.mppsolartest.command.Qpigs;
import org.mppsolartest.serial.SerialHandler;

import java.util.Scanner;

public class CliCommandTest {
    public static void main(String[] args) throws Exception {
        // configure com port
        var ports = SerialPort.getCommPorts();
        for (int i = 0; i < ports.length; i++) {
            var number = i + 1;
            Log.log(number + ". " + ports[i].getSystemPortName());
        }
        var comChoice = new Scanner(System.in).nextInt() - 1;

        // query
        var port = ports[comChoice];
        if (port.openPort()) {
            var serialHandler = new SerialHandler(port);
            var commandChoice = "";
            Log.log(new Qpigs().run(serialHandler).toString());
            while (true) {
                // get command
                Log.log("Command (\"exit\" to quit):");
                commandChoice = new Scanner(System.in).next();
                if (!"exit".equalsIgnoreCase(commandChoice)) {
                    Log.log(serialHandler.excuteSimpleCommand(commandChoice));
                } else {
                    break;
                }
            }
            serialHandler.close();
        }
    }
}