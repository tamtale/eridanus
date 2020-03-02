package com.week1.game.Networking.Messages.Control;

import com.badlogic.gdx.Gdx;
import com.week1.game.Model.Entities.TowerType;
import com.week1.game.Networking.Host;
import com.week1.game.Networking.Messages.Game.CreateTowerMessage;
import com.week1.game.Networking.Messages.Game.InitMessage;
import com.week1.game.Networking.Messages.Game.TowerDetailsMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.Messages.Update;
import com.week1.game.Networking.Player;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

public class StartMessage extends HostControlMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.START;
    private final static String TAG = "StartMessage";
    
    public StartMessage(int playerID){
        super(playerID, MESSAGE_TYPE);
    }

    @Override 
    public void updateHost(Host h, DatagramPacket p) {
        h.gameStarted = true;
        Gdx.app.log(TAG, "Host received a 'start' message from: " + p.getAddress().getHostAddress());

        // tell each player what their id is
//                int playerId = 0;
        for (Player player : h.registry.values()) {
            String playerIdMessage = MessageFormatter.packageMessage(new PlayerIdMessage(player.playerId));
            DatagramPacket packet = new DatagramPacket(playerIdMessage.getBytes(), playerIdMessage.getBytes().length, player.address, player.port);
            try {
                h.udpSocket.send(packet);
            } catch (IOException e) {
                Gdx.app.error(TAG, "Failed to send message to: " + player.address);
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(2000);
        }  catch (InterruptedException e) {
            e.printStackTrace();
        }
        ;
        // TODO: this gets sent first so that the game engine does any initialization before the game starts (but udp doesn't guarantee order)
        
        System.out.println("About to broadcast init messages.");
        h.broadcastToRegisteredPlayers(MessageFormatter.packageMessage(
                new Update(Arrays.asList(
                        MessageFormatter.packageMessage(new TowerDetailsMessage(-1, h.towerDetails, 0)),
                        MessageFormatter.packageMessage(new InitMessage(h.registry.size(), -1, 0)),
//                        MessageFormatter.packageMessage(new CreateTowerMessage(4, 4, 2, 0, 0, 0)),
//                        MessageFormatter.packageMessage(new CreateTowerMessage(10, 4, 1, 1, 0, 0)),
//                        MessageFormatter.packageMessage(new CreateTowerMessage(20, 4, 1, 2, 0, 0)),
//                        MessageFormatter.packageMessage(new CreateTowerMessage(25, 15, 1, 3, 0, 0)),
//                        MessageFormatter.packageMessage(new CreateTowerMessage(5, 15, 1, 4, 0, 0)),
                        MessageFormatter.packageMessage(new CreateTowerMessage(15, 15, 1, 5, 0, 0))
                ))
        ));
        
        h.runUpdateLoop();

    }

    @Override
    public String toString() {
        return "StartMessage: " + playerID;
    }
}
