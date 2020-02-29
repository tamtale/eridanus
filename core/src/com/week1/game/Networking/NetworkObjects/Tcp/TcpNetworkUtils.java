package com.week1.game.Networking.NetworkObjects.Tcp;

import com.badlogic.gdx.Gdx;
import com.week1.game.GameControllerSetScreenAdapter;
import com.week1.game.MenuScreens.ScreenManager;

import java.net.*;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;

public class TcpNetworkUtils {
    private static final String TAG = "NetworkUtils - lji1";
    private static String addr;
    public static String getLocalHostAddr() {
//        https://stackoverflow.com/questions/40912417/java-getting-ipv4-address?fbclid=IwAR0JQ8qEf4V2bM42m-X0ATML0zf5zEyJ_gEWs9I7PskAHCmW_TNNj5cWp6I
//        https://stackoverflow.com/questions/8462498/how-to-determine-internet-network-interface-in-java


        if (TcpNetworkUtils.addr != null ) {
            return TcpNetworkUtils.addr;
        }
        
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
                    for (int i = 8000; i < 8050; i++) {
                        try (SocketChannel socket = SocketChannel.open()) {
                            socket.socket().setSoTimeout(3000);
                            socket.bind(new InetSocketAddress(addr, i));

                            // Try using the socket to connect to some reliable site
                            // If it works, then this ip is usable
                            socket.connect(new InetSocketAddress("google.com", 80));
                            Gdx.app.log(TAG, "Obtained local host address: " + addr.getHostAddress() + " with port: " + i);
                            TcpNetworkUtils.addr = addr.getHostAddress();
                            return TcpNetworkUtils.addr;
                        } catch (Exception e) {
                            Gdx.app.log(TAG, "Port failed on: " + addr + ": " + i);
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
     * @return The client object
     */
    public static TcpClient initNetworkObjects(boolean isHost, String hostIP, Integer port, GameControllerSetScreenAdapter gameAdapter) {
        final String TAG = "initNetworkObjects - lji1";
        Gdx.app.log(TAG, "Local host address: " + getLocalHostAddr());

        TcpClient c =  null;
        try {
            if (isHost) {
                Gdx.app.log(TAG, "Host option chosen.");

                String localIpAddr = InetAddress.getLocalHost().getHostAddress();

                // create the host instance
                TcpHost h = new TcpHost(port);
                // start listening for messages from clients
                h.listenForClientMessages();


                // Now make the client stuff
                ScreenManager sm = new ScreenManager(gameAdapter, true);
                c = new TcpClient(localIpAddr, h.getPort(), sm);
                c.sendJoinMessage();

            } else {
                Gdx.app.log(TAG, "Client option chosen.");
                // host ip is the number listed under ipconfig > Wireless LAN adapter Wi-Fi > IPv4 Address

                try {
                    ScreenManager sm = new ScreenManager(gameAdapter, false);
                    c = new TcpClient(hostIP, port, sm);
                    c.sendJoinMessage();
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

