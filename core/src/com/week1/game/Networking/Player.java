package demo;

import java.net.InetAddress;

public class Player {
    public InetAddress address;
    public int port;
    public boolean checkedIn;
    
    public Player(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        checkedIn = false;
    }
}
