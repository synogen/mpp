package org.mppsolartest.serial;

import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialHandler {
    private final InputStream input;
    private final OutputStream output;
    private final SerialPort serialPort;
    private int _errorcount = 0;

    private void clearbuffer() {
        try {
            for(int buflen = this.input.available(); buflen > 0; --buflen) {
                this.input.read();
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public SerialHandler(SerialPort serialPort) throws Exception {
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
                boolean flag = false;

                while(System.currentTimeMillis() < end) {
                    int ch;
                    if ((ch = this.input.read()) >= 0) {
                        if (ch == 13) {
                            flag = true;
                            break;
                        }

                        sb.append((char)ch);
                    }
                }

                if (!flag) {
                    result = false;
                }

                returnValue = sb.toString();
                if (CRCUtil.checkCRC(returnValue)) {
                    returnValue = returnValue.substring(0, returnValue.length() - 2);
                } else {
                    returnValue = "";
                }
            }
        } catch (Exception var15) {
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
                boolean flag = false;

                while(System.currentTimeMillis() < end) {
                    int ch;
                    if ((ch = this.input.read()) >= 0) {
                        if (ch == 13) {
                            flag = true;
                            break;
                        }

                        sb.append((char)ch);
                    }
                }

                if (!flag) {
                    if (command.indexOf("P") == 0) {
                        System.out.println("4567890::" + returnValue);
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
        } catch (Exception var17) {
            result = false;
        } finally {
            this.countErrorandNotifyProcesser(result);
        }

        return returnValue;
    }

    private void countErrorandNotifyProcesser(boolean success) {
        if (success) {
            this._errorcount = 0;
        } else {
            ++this._errorcount;
        }

        if (this._errorcount >= 12) {
            System.out.println("---------communication exception---------" + this._errorcount);
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

    public String getDeviceName() {
        String portName = this.serialPort.getSystemPortName();
        int index = portName.lastIndexOf("/");
        if (index > 0) {
            portName = portName.substring(index + 1);
        }

        return portName;
    }

    public int getMpptTrackNumber() {
        int mpptTrackNumber = 2;

        try {
            String result = this.excuteCommand("QPIRI", true);
            System.out.println("result===" + result);
            if (result != null && !"".equals(result) && !result.equals("QPIRI")) {
                String[] arr = result.split(" ");
                mpptTrackNumber = Integer.parseInt(arr[7]);
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return mpptTrackNumber;
    }

    public String getSerialNo() {
        String serialno = "";
        String serialnoStr;

        try {
            for(var i = 0; i < 2; ++i) {
                serialnoStr = this.excuteCommand("QSID", true);
                if (serialnoStr != null && !"".equals(serialnoStr) && !serialnoStr.equalsIgnoreCase("(NAK") && !serialnoStr.equalsIgnoreCase("(ACK") && !serialnoStr.equals("QSID")) {
                    String validLen = serialnoStr.substring(1, 3);
                    serialno = serialnoStr.substring(3, 3 + Integer.parseInt(validLen));
                    break;
                }
            }

            if (serialno.length() > 0) {
                return serialno;
            }

            for(var i = 0; i < 3; ++i) {
                serialnoStr = this.excuteCommand("QID", true);
                if (serialnoStr != null && !"".equals(serialnoStr) && !serialnoStr.equalsIgnoreCase("(NAK") && !serialnoStr.equalsIgnoreCase("(ACK") && !serialnoStr.equals("QID")) {
                    serialno = serialnoStr.substring(1);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return serialno;
    }

    public String getModeType() {
        String machineTypeStr = "";

        try {
            String qpiriStr = this.excuteCommand("QPIRI", true);
            System.out.println(" qpiriStr===" + qpiriStr);
            if (!"".equals(qpiriStr) && !qpiriStr.equals("(NAK") && !qpiriStr.equals("QPIRI")) {
                String[] ratingInfo = qpiriStr.split(" ");
                machineTypeStr = ratingInfo[8];
                return machineTypeStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return machineTypeStr;
    }

    public String getDeviceModel() {
        String deviceModel = "";

        try {
            String qdmStr = this.excuteCommand("QDM", true);
            if (!"".equals(qdmStr) && !qdmStr.equals("(NAK") && !qdmStr.equals("QDM")) {
                qdmStr = qdmStr.substring(1);
                deviceModel = qdmStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return deviceModel;
    }

    public boolean isSupportQCTH() {
        String qchtStr = this.excuteCommand("QCHT", true);
        return qchtStr != null && !"".equals(qchtStr) && !qchtStr.equals("(NAK");
    }

    public boolean isSupportQPPS() {
        String ppsStr = this.excuteCommand("QPPS", true);
        return ppsStr != null && !"".equals(ppsStr) && !ppsStr.equals("(NAK");
    }
}
