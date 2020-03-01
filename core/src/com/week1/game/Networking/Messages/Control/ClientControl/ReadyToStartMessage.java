package com.week1.game.Networking.Messages.Control.ClientControl;

import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Client;

/**
 * This is send to the 'hosting' client indicating all players have sent in
 * their tower loadout and the game can now be started
 */
public class ReadyToStartMessage extends ClientControlMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.READYTOSTART;

    public ReadyToStartMessage(int playerID) {
        super(playerID, MESSAGE_TYPE);
    }

    @Override
    public void updateClient(Client c) {
        c.getScreenManager().setReadyToStart();
    }
}
