package com.week1.game.Networking.Messages.Control;

import com.badlogic.gdx.Gdx;
import com.week1.game.Model.TowerDetails;
import com.week1.game.Networking.Host;
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
        h.broadcastToRegisteredPlayers(MessageFormatter.packageMessage(
                new Update(Arrays.asList(new String[] {MessageFormatter.packageMessage(new InitMessage(h.registry.size(), -1))}))));
        
        // need to convert the tower details map into 2d array
//        TowerDetails[h.][] towerDetailsArray = h.towerDetails.
        h.towerDetails.to
        h.broadcastToRegisteredPlayers(MessageFormatter.packageMessage(
                new TowerDetailsMessage(-1, h.towerDetails);
        ));

        h.runUpdateLoop();

    }

    @Override
    public String toString() {
        return "StartMessage: " + playerID;
    }
}
