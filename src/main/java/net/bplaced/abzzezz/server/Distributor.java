package net.bplaced.abzzezz.server;

import net.bplaced.abzzezz.util.Client;
import net.bplaced.abzzezz.util.ListUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Distributor {
    public static final int AMOUNT_INCREASE = 100;
    public static final int MAX_RANGE = 7;

    private final List<Client> clientList = new ArrayList<>();

    //List to keep track of all the invalid password lengths as determined by the clients.
    private final List<Integer> invalidLengths = new ArrayList<>();

    private int currentByteIndex = 0;

    private int wordsRead = 0;


    public void addClient(final String ip, final int port) {
        this.clientList.add(new Client(ip, port));
    }

    public synchronized String[] getWords() {
        final List<String> words = new ArrayList<>();

        try (final InputStream inputStream = ListUtil.createInputStream()) {
            inputStream.skip(currentByteIndex);

            BufferedInputStream bis = new BufferedInputStream(inputStream);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();


            for (int result = bis.read(), i = 0; result != -1 && i < AMOUNT_INCREASE; result = bis.read()) {
                if (result == '\n') {
                    words.add(buf.toString());
                    currentByteIndex += buf.size();
                    buf.reset();
                    i++;
                } else {
                    buf.write((byte) result);
                    currentByteIndex += result; //More bytes have been read
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        wordsRead += AMOUNT_INCREASE;
        //Add new strings to the buffer
        return words.toArray(String[]::new);
    }

    public synchronized int getRange() {
        int rand;
        do {
            rand = ThreadLocalRandom.current().nextInt(MAX_RANGE);
        } while (invalidLengths.contains(rand));

        //TODO: Distribute evenly
        return rand;
    }

    public synchronized void addToInvalid(final int length) {
        this.invalidLengths.add(length);
    }

    public int getWordsRead() {
        return wordsRead;
    }

    public void reset() {
        this.currentByteIndex = 0;
        this.wordsRead = 0;
    }

    public List<Client> getClientList() {
        return clientList;
    }

    public int getCurrentByteIndex() {
        return currentByteIndex;
    }

    public List<Integer> getInvalidLengths() {
        return invalidLengths;
    }
}
