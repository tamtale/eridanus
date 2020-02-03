package com.week1.game.Networking;

import com.badlogic.gdx.Gdx;

import java.net.*;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils - lji1";
    public static String getLocalHostAddr() {
//        https://stackoverflow.com/questions/40912417/java-getting-ipv4-address?fbclid=IwAR0JQ8qEf4V2bM42m-X0ATML0zf5zEyJ_gEWs9I7PskAHCmW_TNNj5cWp6I
//        https://stackoverflow.com/questions/8462498/how-to-determine-internet-network-interface-in-java
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

                    // try all the ports from 8000 to 9000, in case some of them are being used
                    for (int i = 8000; i < 9000; i++) {
                        try (SocketChannel socket = SocketChannel.open()) {
                            socket.socket().setSoTimeout(3000);
                            socket.bind(new InetSocketAddress(addr, i));

                            // Try using the socket to connect to some reliable site
                            // If it works, then this ip is usable
                            socket.connect(new InetSocketAddress("google.com", 80));
                            Gdx.app.log(TAG, "Obtained local host address: " + addr.getHostAddress() + " with port: " + i);
                            return addr.getHostAddress();
                        } catch (Exception e) {
                            Gdx.app.log(TAG, "Port failed: " + i);
//                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } 
        
        Gdx.app.error(TAG, "Unable to obtain valid ip address.");
        return "failure to obtain ip address - see NetworkUtils";
    }

    /**
     * Accepts arguments that determine whether this instance will host the game or just
     * act as a client. (Even when hosting, the instance also behaves as a client.)
     *
     * @param args - Either "host" or "client <ip address> <port number> <optional - start>"
     * @return The client object
     */
    public static Client initNetworkObjects(String[] args, INetworkClientToEngineAdapter adapter) {
        final String TAG = "initNetworkObjects - lji1";
        Gdx.app.log(TAG, "Local host address: " + getLocalHostAddr());
        
        Gdx.app.log(TAG, "Arguments: ");
        for (int i = 0; i < args.length; i++) {
            Gdx.app.log(TAG, "\t" + args[i]);
        }

        Client c =  null;
        try {
            if (args[0].equals("host")) {
                Gdx.app.log(TAG, "Host option chosen.");

                String localIpAddr = InetAddress.getLocalHost().getHostAddress();

                // create the host instance
                int port;
                try {
                    port = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    throw new IndexOutOfBoundsException("Expected arguments in format: host <portnumber> <start (optional)>");
                }
                Host h = new Host(port);
                // start listening for messages from clients
                h.listenForClientMessages();


                // Now make the client stuff
                c = new Client(localIpAddr, h.getPort(), adapter);

                if (args.length == 3 && args[2].equals("start")) {
                    // Time to start the game
                    c.sendStringMessage("start");
                }
                

            } else if  (args[0].equals("client")) {
                Gdx.app.log(TAG, "Client option chosen.");
                // host ip is the number listed under ipconfig > Wireless LAN adapter Wi-Fi > IPv4 Address

                try {
                    String hostIpAddr = args[1];
                    int hostPort = Integer.parseInt(args[2]);
                    c = new Client(hostIpAddr, hostPort, adapter);

                    if (args.length == 4 && args[3].equals("start")) {
                        // Time to start the game
                        c.sendStringMessage("start");
                    } 
                }
                catch (Exception e) {
                    throw new IndexOutOfBoundsException("Expected arguments in format: client <ip address> <portnumber> <start (optional)>");
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
            Gdx.app.error(TAG, "Failed to initialize");
        }

        return c;
    }
}

