package com.week1.game.Networking;

import com.badlogic.gdx.Gdx;

import java.net.InetAddress;
import java.net.NetworkInterface;

public class NetworkUtils {
    public static String getLocalHostAddr() {
        try {
            // TODO: broken for Tam
            return NetworkInterface.getByName("wlan0").getInterfaceAddresses().get(0).getAddress().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to obtain local host address, due to anomalous network configuration. Use ipconfig > Wireless LAN adapter Wi-Fi > IPv4 Address instead.");
            return "Failure";
        }
    }

    /**
     * Accepts arguments that determine whether this instance will host the game or just
     * act as a client. (Even when hosting, the instance also behaves as a client.)
     *
     * @param args - Either "host" or "client <ip address> <port number> <optional - start>"
     * @return The client object
     */
    public static Client initNetworkObjects(String[] args, INetworkClientToEngineAdapter adapter) {
        final String INITNETWORKTAG = "initNetworkObjects - lji1";
        Gdx.app.log(INITNETWORKTAG, "Local host address: " + getLocalHostAddr());

        Client c =  null;
        try {
            if (args[0].equals("host")) {

                String localIpAddr = InetAddress.getLocalHost().getHostAddress();

                // create the host instance
                Host h = new Host();
                // start listening for messages from clients
                h.listenForClientMessages();


                // Now make the client stuff
                c = new Client(localIpAddr, h.getPort(), adapter);

            } else if  (args[0].equals("client")) {
                // host ip is the number listed under ipconfig > Wireless LAN adapter Wi-Fi > IPv4 Address

                String hostIpAddr = args[1];
                int hostPort = Integer.parseInt(args[2]);
                c = new Client(hostIpAddr, hostPort, adapter);

                if (args.length == 4 && args[3].equals("start")) {
                    // Time to start the game
                    c.sendMessage("start");
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
            Gdx.app.error(INITNETWORKTAG, "Failed to initialize");
        }

        return c;
    }
}

