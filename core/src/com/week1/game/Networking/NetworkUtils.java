package com.week1.game.Networking;

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
}
