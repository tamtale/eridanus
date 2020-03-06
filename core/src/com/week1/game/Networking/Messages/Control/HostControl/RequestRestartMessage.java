package com.week1.game.Networking.Messages.Control.HostControl;

import com.badlogic.gdx.Gdx;
import com.week1.game.Networking.Messages.Control.ClientControl.RestartMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Host;
import com.week1.game.Networking.NetworkObjects.Player;

import java.net.InetAddress;

public class RequestRestartMessage extends HostControlMessage {

    private final static MessageType type = MessageType.REQUESTRESTART;

    public RequestRestartMessage(int playerID) {
        super(playerID, type);
    }

    @Override
    public void updateHost(Host h, InetAddress addr, int port) {
        Gdx.app.debug("pjb3 - RequestRestart handler", "handling");
        h.towerDetails.clear();
        for (Player player : h.registry.values()) {
            h.sendMessage(MessageFormatter.packageMessage(new RestartMessage(-1)), player);
        }
    }
}
