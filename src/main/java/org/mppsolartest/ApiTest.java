package org.mppsolartest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazecast.jSerialComm.SerialPort;
import org.mppsolartest.command.Qpiri;
import org.mppsolartest.serial.SerialHandler;

import java.io.FileReader;
import java.util.Properties;

import static spark.Spark.get;
import static spark.Spark.port;

/**
 * REST API example
 */
public class ApiTest {
    public static void main(String[] args) throws Exception {
        var config = new Properties();
        config.load(new FileReader("serial.properties"));
        var port = SerialPort.getCommPort(config.getProperty("port"));
        port.setBaudRate(2400);
        if (port.openPort()) {
            Log.log(port.getSystemPortName() + " open");
            var serialHandler = new SerialHandler(port);

            port(80);
            get("/api/rawcommand/:cmd", (request, response) -> {
                var command = request.params("cmd").toUpperCase();
                return serialHandler.excuteSimpleCommand(command);
            });
            get("/api/command/:cmd", (request, response) -> {
                var command = request.params("cmd").toUpperCase();
                if (command.equalsIgnoreCase("QPIRI")) return new ObjectMapper().writeValueAsString(new Qpiri().run(serialHandler));
                return "Unknown or unimplemented command " + command;
            });
        }
    }
}
