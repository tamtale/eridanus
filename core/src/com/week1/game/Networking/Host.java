package demo;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Host {
    
    private int port;
    private DatagramSocket udpSocket;
    
    private Map<InetAddress, Player> registry = new HashMap<>();
    
    private boolean gameStarted = false;
    private StringBuilder aggregateMessage = new StringBuilder();
    
//    private Scanner scanner;
    
    public Host() throws SocketException, IOException {
//        this.port = port;
        this.udpSocket = new DatagramSocket();
        this.port = udpSocket.getLocalPort();
        
        System.out.println("Creating socket for host instance with address: " +
                NetworkUtils.getLocalHostAddr() + " on port: " + this.port);
        
//        System.out.println("**********");
//        System.out.println(InetAddress.getLocalHost());
//        System.out.println(InetAddress.getLocalHost().getHostAddress());
//        System.out.println(udpSocket);
//        System.out.println(udpSocket.getLocalAddress());
//        System.out.println(udpSocket.getLocalAddress().getHostAddress());
//        System.out.println(udpSocket.getInetAddress());
////        System.out.println(udpSocket.getInetAddress().getHostAddress()); // Apparently, is null
        
        
//        System.out.println(NetworkInterface.getNetworkInterfaces());
//        
//        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
//        while(interfaces.hasMoreElements()) {
//            System.out.println("Interface:");
//            NetworkInterface nextInterface = interfaces.nextElement();
//            System.out.println("\t" + nextInterface.getDisplayName());
//            System.out.println("name: " + nextInterface.getName());
//            System.out.println(nextInterface.isVirtual());
//            System.out.println(nextInterface.getInetAddresses());
//            Enumeration<InetAddress> addrs = nextInterface.getInetAddresses();
//            System.out.println("Has more elts: " + addrs.hasMoreElements());
//            while(addrs.hasMoreElements()) {
//                System.out.println("addr:");
//                InetAddress addr = addrs.nextElement();
//                System.out.println(addr.getHostAddress());
//            }
//            
//        }
//        System.out.println("**********");
        
    }
    
    public int getPort() {
        return port;
    }

    public void listenForClientMessages() throws Exception {
        
        // Spawn a new thread to listen for messages incoming to the host
        new Thread(() -> {
            while (true) {
                System.out.println("Host is listening for next client message.");
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                try {
                    // blocks until a packet is received
                    udpSocket.receive(packet);
                    processMessage(packet);
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    private void processMessage(DatagramPacket packet) {
        String msg = new String(packet.getData()).trim();
        
        if (!gameStarted) {
//             Game has started
            if (msg.equals("join")) {
                System.out.println("Host received a 'join' message from: " + packet.getAddress().getHostAddress());
                registry.put(packet.getAddress(), new Player(packet.getAddress(), packet.getPort()));

                System.out.println("List of Players: ");
                registry.values().forEach((p) -> System.out.println("\t" + p.address + " : " + p.port));
            } else if (msg.equals("start")) {
                gameStarted = true;
                System.out.println("Host received a 'start' message from: " + packet.getAddress().getHostAddress());
                System.out.println("Host will forward the start message to all registered players.");
                broadcastToRegisteredPlayers("start");
            } else {
                System.out.println("Unrecognized message before start of game.");
            }
        } else {
            // Game has started
            System.out.println("Host received an update message from: " + packet.getAddress().getHostAddress());

            aggregateMessage.append(msg);
            registry.get(packet.getAddress()).checkedIn = true;

            // check if all players have checked in yet
            boolean ready = true;
            for (Player player : registry.values()) {
                ready = ready && player.checkedIn;
            }

            if (ready) {
                // all players have checked in; send out the next update
                broadcastToRegisteredPlayers(aggregateMessage.toString());

                // clean up for the next round
                aggregateMessage = new StringBuilder();
                registry.values().forEach((player) -> player.checkedIn = false);
            } 
            // else, wait for the other players to check in
        }
        
        
    }
    
    private void broadcastToRegisteredPlayers(String msg) {
        registry.values().forEach((player) -> {
            DatagramPacket p = new DatagramPacket(msg.getBytes(), msg.getBytes().length, player.address, player.port);
            try {
                this.udpSocket.send(p);
            } catch (IOException e) {
                System.out.println("Failed to send message to: " + player.address);
                e.printStackTrace();
            }
        });
    }
}
