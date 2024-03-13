package org.mppsolartest.serial;

import com.fazecast.jSerialComm.SerialPort;
import org.mppsolartest.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialHandler {
    private final InputStream input;
    private final OutputStream output;
    private final SerialPort serialPort;
    private int errorcount = 0;

    private void clearbuffer() {
        try {
            for(int buflen = this.input.available(); buflen > 0; --buflen) {
                this.input.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public SerialHandler(SerialPort serialPort) {
        this.serialPort = serialPort;
        this.serialPort.setComPortParameters(2400, 8, 1, 0);
        this.serialPort.setComPortTimeouts(1, 1200, 0);
        this.input = serialPort.getInputStream();
        this.output = serialPort.getOutputStream();
    }

    public synchronized String excuteSimpleCommand(String command) {
        boolean result = true;
        String returnValue = "";

        try {
            for(int time = 0; (returnValue.length() == 0 || returnValue.startsWith("(NAK")) && time < 3; ++time) {
                this.clearbuffer();
                byte[] crc = CRCUtil.getCRCByte(command);
                byte[] bytes = command.getBytes();
                this.output.write(bytes);
                this.output.write(crc);
                this.output.write(13);
                this.output.flush();
                long end = System.currentTimeMillis() + 3000L;
                StringBuilder sb = new StringBuilder();
                boolean linebreak = false;

                while(System.currentTimeMillis() < end) {
                    int ch;
                    if ((ch = this.input.read()) >= 0) {
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
        } catch (Exception e) {
            result = false;
        } finally {
            this.countErrorandNotifyProcesser(result);
        }

        return returnValue;
    }

    public synchronized String excuteCommand(String command, boolean isResponse) {
        boolean result = true;
        String returnValue = "";

        try {
            for(int time = 0; (returnValue == null || returnValue.length() == 0 || returnValue.startsWith("(NAK")) && time < 3; ++time) {
                this.clearbuffer();
                byte[] crc = CRCUtil.getCRCByte(command);
                byte[] bytes = command.getBytes();
                this.output.write(bytes);
                this.output.write(crc);
                this.output.write(13);
                this.output.flush();
                if (!isResponse) {
                    returnValue = null;
                    break;
                }

                long end = System.currentTimeMillis() + 10000L;
                StringBuilder sb = new StringBuilder();
                boolean linebreak = false;

                while(System.currentTimeMillis() < end) {
                    int ch;
                    if ((ch = this.input.read()) >= 0) {
                        if (ch == 13) {
                            linebreak = true;
                            break;
                        }

                        sb.append((char)ch);
                    }
                }

                if (!linebreak) {
                    if (command.indexOf("P") == 0) {
                        Log.log("4567890::" + returnValue);
                    }

                    result = false;
                }

                returnValue = sb.toString();
                if (returnValue != null && returnValue.length() > 0) {
                    if (CRCUtil.checkCRC(returnValue)) {
                        returnValue = returnValue.substring(0, returnValue.length() - 2);
                    } else {
                        returnValue = "";
                    }
                }
            }
        } catch (Exception e) {
            result = false;
        } finally {
            this.countErrorandNotifyProcesser(result);
        }

        return returnValue;
    }

    private void countErrorandNotifyProcesser(boolean success) {
        if (success) {
            this.errorcount = 0;
        } else {
            ++this.errorcount;
        }

        if (this.errorcount >= 12) {
            Log.log("---------communication exception---------" + this.errorcount);
        }

    }

    public void close() {
        if (this.input != null) {
            try {
                this.input.close();
            } catch (IOException ignored) {}
        }

        if (this.output != null) {
            try {
                this.output.close();
            } catch (IOException ignored) {}
        }

        if (this.serialPort != null) {
            try {
                this.serialPort.closePort();
            } catch (Exception ignored) {}
        }
    }
}
