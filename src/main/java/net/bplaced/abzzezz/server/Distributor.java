package net.bplaced.abzzezz.server;

import net.bplaced.abzzezz.util.Client;

import java.util.ArrayList;
import java.util.List;

public class Distributor {
    public static final int MAX_UNICODE_VALUE = 255; //65_536;
    public static final int AMOUNT_INCREASE = 10;

    private final List<Client> clientList = new ArrayList<>();

    //List to keep track of previous client's ranges.
    private final List<int[]> openRanges = new ArrayList<>();

    private int length = 1;

    private int currentMinBound = 33;


    public void addClient(final String ip, final int port) {
        this.clientList.add(new Client(ip, port));
    }

    //Hand of 200 characters to the client to try
    public synchronized int[] getBounds() {
        int minBound;
        int maxBound;
        //Every unicode symbol has been tried therefore.
        if (this.currentMinBound + AMOUNT_INCREASE >= MAX_UNICODE_VALUE) {
            length++; //Increase the length
            this.currentMinBound = 0;
            minBound = MAX_UNICODE_VALUE - AMOUNT_INCREASE;
        } else {
            minBound = currentMinBound;
            this.currentMinBound += AMOUNT_INCREASE;
        }

        maxBound = minBound + AMOUNT_INCREASE;
        return new int[]{
                minBound,
                maxBound,
        };
    }


    public List<Client> getClientList() {
        return clientList;
    }

    public int getCurrentMinBound() {
        return currentMinBound;
    }

    public int getLength() {
        return length;
    }

    public void reset() {
        this.length = 1;
        this.currentMinBound = 0;

    }
}
