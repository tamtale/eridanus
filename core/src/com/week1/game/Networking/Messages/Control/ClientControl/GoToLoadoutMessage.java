package com.week1.game.Networking.Messages.Control.ClientControl;

import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Client;

/**
 * This is the message that is sent to command clients to go to the loadoutScreen to choose their towers
 */
public class GoToLoadoutMessage extends ClientControlMessage {

    private final static MessageType type = MessageType.GOTOLOADOUT;

    public GoToLoadoutMessage(int playerID) {
        super(playerID, type);
    }

    @Override
    public void updateClient(Client c) {
        c.getScreenManager().createNewLoadoutScreen((Client)c);
    }
}
