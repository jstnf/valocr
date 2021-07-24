package dev.justinf.valocr;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class SerialTerminal {

    private String port;
    private boolean connecting;
    private boolean connected;
    private long lastHandshakeTimestamp;
    private CallbackResult lastHandshake;

    public SerialTerminal() {
        this.port = "COM1"; // Default value, needs to be changed in GUI
        this.connecting = false;
        this.connected = false;
        this.lastHandshakeTimestamp = -1;
    }

    public boolean establishConnection() {
        connecting = true;
        SerialPort serialPort = SerialPort.getCommPort(port);
        serialPort.setComPortParameters(9600, 8, 1, 0);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
        if (!serialPort.openPort()) {
            System.out.println("Could not open port!");
            connecting = false;
            connected = false;
            return false;
        }

        System.out.println("Opened port!");

        // Now, initialize and attempt handshake to determine if this is correct port
        System.out.println("Attempting handshake (will check again for handshake in 5 seconds)!");
        lastHandshake = new CallbackResult();
        initiateHandshakeListeners(serialPort, lastHandshake);
        serialPort.writeBytes(new byte[]{ 0x69 }, 1);

        // Wait 5s for data
        try {
            Thread.sleep(5000);
            if (!lastHandshake.getReceived().get() || !lastHandshake.getWritten().get()) {
                System.out.println("Handshake failed! (" + lastHandshake.getWritten().get() + ", " + lastHandshake.getReceived().get() + ") Attempting to close port. Please try testing connection again.");
                if (!serialPort.closePort()) {
                    System.out.println("Could not close port!");
                }
                connecting = false;
                connected = false;
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            connecting = false;
            connected = false;
            return false;
        }

        // We have a working connection to the HC-05 / microchip!
        System.out.println("Connection established!");
        serialPort.removeDataListener();

        // Only listens if we receive data back
        serialPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {
                lastHandshakeTimestamp = System.currentTimeMillis();
            }
        });

        connecting = false;
        connected = true;
        return true;
    }

    private void initiateHandshakeListeners(SerialPort serialPort, CallbackResult result) {
        // Written
        serialPort.addDataListener(new SerialPortDataListener() {

            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_WRITTEN;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {
                if (serialPortEvent.getEventType() == SerialPort.LISTENING_EVENT_DATA_WRITTEN) {
                    System.out.println("Successfully wrote data of size: " + serialPortEvent.getReceivedData().length);
                    result.getWritten().set(true);
                }
            }
        });

        // Received
        serialPort.addDataListener(new SerialPortDataListener() {

            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {
                if (serialPortEvent.getEventType() == SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
                    result.getReceived().set(true);
                    byte[] newData = serialPortEvent.getReceivedData();
                    System.out.println("Received data of size: " + newData.length);
                    for (int i = 0; i < newData.length && result.getReceived().get(); i++) {
                        System.out.println(newData[i]);
                        result.getReceived().set(newData[i] == 0x69); // We need to have received ONLY 0x69's
                    }

                    if (!result.getReceived().get()) {
                        System.out.println("Handshake RECEIVE requirement failed! (wrong bytes received)");
                    } else {
                        lastHandshakeTimestamp = System.currentTimeMillis();
                    }
                }
            }
        });
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

    public CallbackResult getLastHandshake() {
        return lastHandshake;
    }

    public void setPort(String port) {
        this.port = port;
    }
}