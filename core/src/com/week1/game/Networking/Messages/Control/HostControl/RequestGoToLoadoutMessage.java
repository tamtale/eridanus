package com.week1.game.Networking.Messages.Control.HostControl;

import com.week1.game.Networking.Messages.Control.ClientControl.GoToLoadoutMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Host;
import com.week1.game.Networking.NetworkObjects.Player;

import java.net.InetAddress;

/**
 * This is sent by the client on the host when it is asking the host to move ALL clients
 * to the Loadout screen when everyone has connected.
 */
public class RequestGoToLoadoutMessage extends HostControlMessage {
    private final  static MessageType type = MessageType.REQUESTGOTOLOADOUT;

    public RequestGoToLoadoutMessage(int playerID) {
        super(playerID, type);
    }

    @Override
    public void updateHost(Host h, InetAddress addr, int port) {
        for (Player player : h.registry.values()) {
            h.sendMessage(MessageFormatter.packageMessage(new GoToLoadoutMessage(-1, h.getPlayerInfoList())), player);
        }
    }
}
