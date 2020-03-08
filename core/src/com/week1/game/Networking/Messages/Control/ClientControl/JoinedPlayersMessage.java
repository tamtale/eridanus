package com.week1.game.Networking.Messages.Control.ClientControl;

import com.week1.game.MenuScreens.ConnectionScreen;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Client;

import java.util.List;

/**
 * This message is sent to a player right after they connect to the Host
 */
public class JoinedPlayersMessage extends ClientControlMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.JOINEDPLAYERS;
    
    private List<String> players; 

    public JoinedPlayersMessage(int playerID, List<String> players){
        super(playerID, MESSAGE_TYPE);
        this.players = players;
    }

    @Override 
    public void updateClient(Client c) {
        System.out.println("Joined players: " + players);
        c.getScreenManager().getConnectionScreen().updateJoinedPlayers(this.players);
    }
    
    @Override
    public String toString() {
        return "Joined Players Message: " + playerID;
    }
}
