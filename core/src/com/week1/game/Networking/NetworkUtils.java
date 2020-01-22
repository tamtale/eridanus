package com.week1.game.Networking;

import com.badlogic.gdx.Gdx;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkUtils {
    public static String getLocalHostAddr() {
//        https://stackoverflow.com/questions/40912417/java-getting-ipv4-address?fbclid=IwAR0JQ8qEf4V2bM42m-X0ATML0zf5zEyJ_gEWs9I7PskAHCmW_TNNj5cWp6I
        String ip;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    // *EDIT*
                    if (addr instanceof Inet6Address) continue;

                    ip = addr.getHostAddress();
                    System.out.println("*******************" + iface.getDisplayName() + " " + ip);
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } 
        
        
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
                Gdx.app.log(INITNETWORKTAG, "Host option chosen.");

                String localIpAddr = InetAddress.getLocalHost().getHostAddress();

                // create the host instance
                Host h = new Host();
                // start listening for messages from clients
                h.listenForClientMessages();


                // Now make the client stuff
                c = new Client(localIpAddr, h.getPort(), adapter);

            } else if  (args[0].equals("client")) {
                Gdx.app.log(INITNETWORKTAG, "Client option chosen.");
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

