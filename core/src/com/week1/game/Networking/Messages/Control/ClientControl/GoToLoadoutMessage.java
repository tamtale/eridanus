package com.week1.game.Networking.Messages.Control.ClientControl;

import com.week1.game.Model.PlayerInfo;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Client;

import java.util.List;

/**
 * This is the message that is sent to command clients to go to the loadoutScreen to choose their towers.
 *
 * At this point, all names and colors have been chosen, and they are also sent to the clients.
 */
public class GoToLoadoutMessage extends ClientControlMessage {

    private final static MessageType type = MessageType.GOTOLOADOUT;

    private List<PlayerInfo> allInfo;

    public GoToLoadoutMessage(int playerID, List<PlayerInfo> infoList) {
        super(playerID, type);
        allInfo = infoList;
    }

    @Override
    public void updateClient(Client c) {
        c.setInfoList(allInfo);
        c.getScreenManager().createNewLoadoutScreen(c);
    }
}
