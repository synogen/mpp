package org.mppsolartest.serial;

import com.fazecast.jSerialComm.SerialPort;
import org.mppsolartest.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.mppsolartest.Log.log;

public class SerialHandler {
    private InputStream input;
    private OutputStream output;
    private final SerialPort serialPort;
    private int errorCount = 0;

    private void clearBuffer() {
        try {
            for(var buflen = input.available(); buflen > 0; --buflen) {
                input.read();
            }
        } catch (Exception e) {
            Log.logException(e);
        }

    }

    public SerialHandler(SerialPort serialPort) {
        this.serialPort = serialPort;
        this.serialPort.setComPortParameters(2400, 8, 1, 0);
        this.serialPort.setComPortTimeouts(1, 1200, 0);
        this.serialPort.openPort(500);
        this.input = serialPort.getInputStream();
        this.output = serialPort.getOutputStream();
    }

    public boolean isOpen() {
        return serialPort.isOpen();
    }
    public synchronized String excuteSimpleCommand(String command) {
        log("[Serial] Query: " + command);
        var result = true;
        var returnValue = "";

        try {
            for(var time = 0; (returnValue.length() == 0 || returnValue.startsWith("(NAK")) && time < 3; ++time) {
                clearBuffer();
                var crc = CRCUtil.getCRCByte(command);
                var bytes = command.getBytes();
                output.write(bytes);
                output.write(crc);
                output.write(13);
                output.flush();
                var timeout = System.currentTimeMillis() + 3000L;
                var sb = new StringBuilder();
                var linebreak = false;

                while(System.currentTimeMillis() < timeout) {
                    int ch;
                    if ((ch = input.read()) >= 0) {
                        if (ch == 13) {
                            linebreak = true;
                            break;
                        }

                        sb.append((char)ch);
                    }
                }

                if (!linebreak) {
                    result = false;
                }

                returnValue = sb.toString();
                if (CRCUtil.checkCRC(returnValue)) {
                    returnValue = returnValue.substring(0, returnValue.length() - 2);
                } else {
                    returnValue = "";
                }
            }
        } catch (IOException e) {
            result = false;
        } finally {
            countErrorAndLog(result);
        }
        log("[Serial] Response: " + returnValue);
        return returnValue;
    }

    private void countErrorAndLog(boolean success) {
        errorCount = success? 0 : errorCount + 1;

        if (errorCount >= 12) {
            Log.log("[Serial] Communication failed " + errorCount + " times");
        }
    }

    public void close() {
        if (input != null) {
            try {
                input.close();
            } catch (IOException ignored) {}
        }

        if (output != null) {
            try {
                output.close();
            } catch (IOException ignored) {}
        }

        if (serialPort != null) {
            serialPort.closePort();
        }
    }

    public String getSystemPortName() {
        return serialPort.getSystemPortName();
    }

    public int errorCount() {
        return errorCount;
    }

    public void reinit() {
        this.close();
        this.serialPort.openPort(500);
        this.input = serialPort.getInputStream();
        this.output = serialPort.getOutputStream();
    }
}
