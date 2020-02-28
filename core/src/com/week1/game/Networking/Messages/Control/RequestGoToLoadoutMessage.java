package com.week1.game.Networking.Messages.Control;

import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.AHost;
import com.week1.game.Networking.NetworkObjects.Player;

import java.net.InetAddress;

public class RequestGoToLoadoutMessage extends HostControlMessage {
    private final  static MessageType type = MessageType.REQUESTGOTOLOADOUT;

    public RequestGoToLoadoutMessage(int playerID) {
        super(playerID, type);
    }

    @Override
    public void updateHost(AHost h, InetAddress addr, int port) {
        for (Player player : h.registry.values()) {
            h.sendMessage(MessageFormatter.packageMessage(new GoToLoadoutMessage(-1)), player);
        }
    }
}
