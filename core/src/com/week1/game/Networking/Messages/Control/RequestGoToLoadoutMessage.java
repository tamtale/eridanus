package com.week1.game.Networking.Messages.Control;

import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.Messages.Update;
import com.week1.game.Networking.NetworkObjects.AHost;

import java.net.InetAddress;
import java.util.Arrays;

public class RequestGoToLoadoutMessage extends HostControlMessage {
    private final  static MessageType type = MessageType.REQUESTGOTOLOADOUT;

    public RequestGoToLoadoutMessage(int playerID) {
        super(playerID, type);
    }

    @Override
    public void updateHost(AHost h, InetAddress addr, int port) {

        h.broadcastToRegisteredPlayers(MessageFormatter.packageMessage(
                new Update(Arrays.asList(
                        MessageFormatter.packageMessage(new GoToLoadoutMessage(-1))
                ))));
    }
}
