package net.bplaced.abzzezz.util;

public class Client {

    private final String ipAddress;
    private final int port;

    private int range;


    public Client(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }


    public int getPort() {
        return port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }
}
