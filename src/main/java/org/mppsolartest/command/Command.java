package org.mppsolartest.command;

import org.mppsolartest.serial.SerialHandler;

public abstract class Command<T> {

    public abstract String getCommand();

    public abstract T parseResponse(String response);

    public T run(SerialHandler serialHandler) {
        var response = serialHandler.excuteSimpleCommand(getCommand());
        return parseResponse(response);
    }
}
