package com.week1.game.Networking.Messages.Control;

import com.badlogic.gdx.Gdx;
import com.week1.game.Networking.NetworkObjects.AHost;
import com.week1.game.Networking.NetworkObjects.Udp.UdpHost;
import com.week1.game.Networking.Messages.Game.InitMessage;
import com.week1.game.Networking.Messages.Game.TowerDetailsMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.Messages.Update;
import com.week1.game.Networking.NetworkObjects.Player;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

public class StartMessage extends HostControlMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.START;
    private final static String TAG = "StartMessage";
    
    public StartMessage(int playerID){
        super(playerID, MESSAGE_TYPE);
    }

    @Override 
    public void updateHost(AHost h, InetAddress addr, int port) {
        h.gameStarted = true;
        Gdx.app.log(TAG, "Host received a 'start' message from: " + addr.getHostAddress());

        // tell each player what their id is
//                int playerId = 0;
        for (Player player : h.registry.values()) {
            String playerIdMessage = MessageFormatter.packageMessage(new PlayerIdMessage(player.playerId));
            h.sendMessage(playerIdMessage, player);
        }
        try {
            Thread.sleep(2000);
        }  catch (InterruptedException e) {
            e.printStackTrace();
        }
        ;
        // TODO: this gets sent first so that the game engine does any initialization before the game starts (but udp doesn't guarantee order)
        h.broadcastToRegisteredPlayers(MessageFormatter.packageMessage(
                new Update(Arrays.asList(
                        MessageFormatter.packageMessage(new InitMessage(h.registry.size(), -1, 0)),
                        MessageFormatter.packageMessage(new TowerDetailsMessage(-1, h.towerDetails, 0))
                ))
        ));
        
        h.runUpdateLoop();

    }

    @Override
    public String toString() {
        return "StartMessage: " + playerID;
    }
}
