package com.week1.game.Networking;

import java.net.InetAddress;
import java.util.Scanner;

public class Demo {
    
    
    /*
    main takes args on whether to be client or host
    one player instantiates the host
    all players instantiate clients that use args from main to connect to the host
    
    when a client is started it sends a join message to the host to let it know that it has joined the host
    
    any player can send a start message to begin game play
        this start message is typed on the console, captured by a running instance of a 'game' that was also instantiated by main
        then the game calls a method on the instance of the client to tell it to pass that start message to the host
        
        
        when the host receives the start message clients can no longer join
        
     */
    
    public static void main(String[] args) throws Exception {
        runDemo(args);
        
//        System.out.println(InetAddress.getLocalHost().getHostAddress());
    }
    
    private static void runDemo(String[] args) throws Exception {
        
        System.out.println("InetAddress.getLocalHost(): " + InetAddress.getLocalHost());
        
        if (args[0].equals("host")) {
            // usage: Demo.jar host
            
//            int port = Integer.parseInt(args[1]);
            String localIpAddr = InetAddress.getLocalHost().getHostAddress();

            // create the host instance
            Host h = new Host();
            // start listening for messages from clients
            h.listenForClientMessages();

            
            // Now to client stuff
            Client c = new Client(localIpAddr, h.getPort());
            playGame(c);

        } else if  (args[0].equals("client")) {
            // usage: Demo.jar client <host ip addr> <host port>
            //          host ip is the number listed under ipconfig > Wireless LAN adapter Wi-Fi > IPv4 Address
            
            int hostPort = Integer.parseInt(args[2]);
            String hostIpAddr = args[1];
            Client c = new Client(hostIpAddr, hostPort);
            
            if (args.length == 4 && args[3].equals("start")) {
                // Time to start the game
                c.sendMessage("start");
            }

            playGame(c);
            
            
        } else {
            System.out.println("Unrecognized command.");
        }
    }
    
    private static void playGame(Client c) {
        
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Waiting for start message from the host.");
        c.waitForUpdate();
        System.out.println("Start message received.");
        
        while (true) {
            System.out.println("Please enter a character:");
            String userInput = scanner.nextLine();
            c.sendMessage(userInput);
            
            
            System.out.println("Waiting for update message from the host.");
            String msg = c.waitForUpdate();
            System.out.println("Received update message: " + msg);
        }
        
        
    }
    
}
