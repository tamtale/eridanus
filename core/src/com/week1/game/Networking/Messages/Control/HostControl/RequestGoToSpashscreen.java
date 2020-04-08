package com.week1.game.Networking.Messages.Control.HostControl;

import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Host;

import java.net.InetAddress;

/**
 * This is the message that is sent when a client is in either the connection screen or the loadout screen and
 * they would like to go back to the home screen.
 */
public class RequestGoToSpashscreen extends HostControlMessage {

    private final  static MessageType type = MessageType.REQUESTGOTOSPLASHSCREEN;

    public RequestGoToSpashscreen(int playerID) {
        super(playerID, type);
    }

    @Override
    public void updateHost(Host h, InetAddress addr, int port) {

    }
}
