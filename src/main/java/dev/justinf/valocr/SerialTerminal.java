package dev.justinf.valocr;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;

public class SerialTerminal {

    private String port;
    private SerialPort currentPort;
    private boolean connecting;
    private boolean connected;
    private long lastHandshakeTimestamp;

    public SerialTerminal() {
        this.port = "COM1"; // Default value, changed in GUI
        this.connecting = false;
        this.connected = false;
        this.lastHandshakeTimestamp = -1;
    }

    public boolean establishConnection() {
        SerialPort[] ports = SerialPort.getCommPorts();
        for (int i = 0; i < ports.length; i++) {
            System.out.println(ports[i].getSystemPortName() + ": " + ports[i].getDescriptivePortName());
        }

        connecting = true;
        currentPort = SerialPort.getCommPort(port);
        currentPort.setComPortParameters(9600, 8, 1, 0);
        currentPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
        if (!currentPort.openPort()) {
            System.out.println("Could not open port!");
            connecting = false;
            connected = false;
            return false;
        }

        lastHandshakeTimestamp = System.currentTimeMillis();
        System.out.println("Opened port!");

        connecting = false;
        connected = true;
        return true;
    }

    public boolean write(byte data) {
        try {
            currentPort.getOutputStream().write(data);
            currentPort.getOutputStream().flush();
            lastHandshakeTimestamp = System.currentTimeMillis();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getPort() {
        return port;
    }

    public boolean isConnecting() {
        return connecting;
    }

    public boolean isConnected() {
        return connected;
    }

    public long getLastHandshakeTimestamp() {
        return lastHandshakeTimestamp;
    }

    public void setPort(String port) {
        this.port = port;
    }
}