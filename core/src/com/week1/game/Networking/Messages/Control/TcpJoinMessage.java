package com.week1.game.Networking.Messages.Control;

import com.badlogic.gdx.Gdx;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.AHost;

import java.net.InetAddress;

// TODO I think this class can be removed entirely?????
public class TcpJoinMessage extends HostControlMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.TCPJOIN;
    private final static String TAG = "JoinMessage";

    public TcpJoinMessage(int playerID){
        super(playerID, MESSAGE_TYPE);
    }

    @Override 
    public void updateHost(AHost h, InetAddress addr, int port) {
        Gdx.app.log(TAG, "Host received a 'tcp join' message from: " + addr.getHostAddress());

        Gdx.app.log(TAG, "List of Players: ");
        h.registry.values().forEach((player) -> Gdx.app.log(TAG, "\t" + player.address + " : " + player.port));
    }

    @Override
    public String toString() {
        return "JoinMessage: " + playerID;
    }
}
