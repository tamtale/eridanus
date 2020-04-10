package com.week1.game.Networking.Messages.Control.ClientControl;

import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Client;

/**
 * This message is processed by clients to disable starting the game as a player is no longer ready
 */
public class UndoReadyToStart extends ClientControlMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.UNDOREADYTOSTART;

    public UndoReadyToStart(int playerID) {
        super(playerID, MESSAGE_TYPE);
    }

    @Override
    public void updateClient(Client c) {
        c.getScreenManager().unsetReadyToStart();
    }
}
