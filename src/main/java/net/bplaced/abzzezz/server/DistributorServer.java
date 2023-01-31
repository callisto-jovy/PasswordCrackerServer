package net.bplaced.abzzezz.server;

import net.bplaced.abzzezz.util.ListUtil;
import net.bplaced.abzzezz.util.packet.Packet;
import net.bplaced.abzzezz.util.packet.PacketUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author MEL
 * @version 2012-05-02
 */
public class DistributorServer extends Server {

    public static final Distributor DISTRIBUTOR = new Distributor();

    public DistributorServer() {
        super(5000);
        System.out.println("Starting server");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("\n--------------------------");
            System.out.println("Current character bounds:");
            System.out.printf("Byte skip / bytes read %d, %d characters read%n", DISTRIBUTOR.getCurrentByteIndex(), DISTRIBUTOR.getCurrentByteIndex() / 8);
            System.out.printf("Total words read %d%n", DISTRIBUTOR.getWordsRead());
            System.out.printf("Invalid lengths %s", DISTRIBUTOR.getInvalidLengths());
            System.out.println("\n--------------------------");
        }, 8, 15, TimeUnit.SECONDS);
    }

    public void processNewConnection(String clientIp, int clientPort) {
        DISTRIBUTOR.addClient(clientIp, clientPort);

        final String base64 = ListUtil.convertZipToBase64();
        this.send(clientIp, clientPort, new Packet("START", base64).toString());
        System.out.println("A new client has been connected. The ZIP data was sent.");
    }


    public void processMessage(String clientIp, int clientPort, String message) {
        String ENDE = "*bye*";
        if (message.equals(ENDE)) {
            this.closeConnection(clientIp, clientPort);
        } else {
            final Packet packet = PacketUtil.packetFromString(message);

            switch (packet.getSignature()) {
                //The client is ready to receive instructions or needs a new range of characters to try.
                case "READY":
                    send(clientIp, clientPort, new Packet("WORDS", DISTRIBUTOR.getWords()).toString());
                    send(clientIp, clientPort, new Packet("RANGE", String.valueOf(DISTRIBUTOR.getRange())).toString());
                    break;
                case "UNSUCCESSFUL WORD":
                    send(clientIp, clientPort, new Packet("WORDS", DISTRIBUTOR.getWords()).toString());
                    break;
                case "UNSUCCESSFUL RANGE":
                    //Remove range from possible ranges.
                    final int length = Integer.parseInt(packet.getArguments().get(0));
                    DISTRIBUTOR.addToInvalid(length);
                    send(clientIp, clientPort, new Packet("RANGE", String.valueOf(DISTRIBUTOR.getRange())).toString());
                    break;
                case "SUCCESS":
                    DISTRIBUTOR.reset();
                    DISTRIBUTOR.getClientList().forEach(
                            client -> send(client.getIpAddress(), client.getPort(), new Packet("FOUND").toString())
                    );
                    final String password = packet.getArguments().get(0);
                    System.out.println("The password was found. Congrats! The password is: " + password);
                    break;
            }
        }
    }

    /**
     * Diese Methode wird aufgerufen, wenn die Verbindung beendet wird.
     */
    public void processClosingConnection(String pClientIP, int pClientPort) {


        System.out.println(pClientIP + " " + pClientPort + " has disconnected from the network.");
    }
}
