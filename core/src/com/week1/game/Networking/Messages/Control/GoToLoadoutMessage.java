package com.week1.game.Networking.Messages.Control;

import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.AClient;

public class GoToLoadoutMessage extends ClientControlMessage {

    private final static MessageType type = MessageType.GOTOLOADOUT;

    public GoToLoadoutMessage(int playerID) {
        super(playerID, type);
    }

    @Override
    public void updateClient(AClient c) {
        c.createNewLoadoutScreen();
    }
}
