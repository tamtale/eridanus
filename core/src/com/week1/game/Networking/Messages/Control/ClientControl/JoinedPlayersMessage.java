package com.week1.game.Networking.Messages.Control.ClientControl;

import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Client;

import java.util.List;

/**
 * This message is sent to a Player when someone else connects.
 */
public class JoinedPlayersMessage extends ClientControlMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.JOINEDPLAYERS;

    private List<String> players;
    private boolean isReady;

    public JoinedPlayersMessage(int playerID, List<String> players, boolean isReady){
        super(playerID, MESSAGE_TYPE);
        this.players = players;
        this.isReady = isReady;
    }

    @Override 
    public void updateClient(Client c) {
        System.out.println("Joined players: " + players);
        c.getScreenManager().getConnectionScreen().updateJoinedPlayers(this.players);
        c.getScreenManager().getConnectionScreen().setReadyToStart(this.isReady);
    }
    
    @Override
    public String toString() {
        return "Joined Players Message: " + playerID;
    }
}
