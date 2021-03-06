package com.week1.game.Networking.Messages.Control.HostControl;

import com.badlogic.gdx.Gdx;
import com.week1.game.Networking.Messages.Control.ClientControl.GoToGameMessage;
import com.week1.game.Networking.Messages.Game.InitMessage;
import com.week1.game.Networking.Messages.Game.TowerDetailsMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.Messages.Update;
import com.week1.game.Networking.NetworkObjects.Host;
import com.week1.game.Networking.NetworkObjects.Player;

import java.net.InetAddress;
import java.util.Arrays;

/**
 * This message is sent to the Host when the hosting client send it. It will only
 * have an effect if all players have sent in their towerloadouts.
 */
public class RequestGoToGameMessage extends HostControlMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.REQUESTGOTOGAME;
    private final static String TAG = "StartMessage";
    private long mapSeed;
    private boolean enableFog;
    
    public RequestGoToGameMessage(long mapSeed, boolean enableFog, int playerID) {
        super(playerID, MESSAGE_TYPE);
        this.mapSeed = mapSeed;
        this.enableFog = enableFog;
    }

    @Override 
    public void updateHost(Host h, InetAddress addr, int port) {
        // Before you can start the game, all players must have sent in a loadout!
        for (Player player: h.registry.values()) {
            if (! h.towerDetails.containsKey(player.playerId)) {
                // If there is no tower details for a player, you cannot allow the game to start yet.
                Gdx.app.debug("pjb3 - StartMessage", "CANNOT start the game without all TowerLoadouts. Missing player " + player.playerId);
                return;
            }
        }

        h.gameStarted = true;

        // Tell all the players to
        for (Player player: h.registry.values()) {
            h.sendMessage(MessageFormatter.packageMessage(new GoToGameMessage(-1)), player);
        }
        Gdx.app.log(TAG, "Host received a 'start' message from: " + addr.getHostAddress());

        // TODO: this gets sent first so that the game engine does any initialization before the game starts (but udp doesn't guarantee order)
        h.broadcastToRegisteredPlayers(MessageFormatter.packageMessage(
                new Update(Arrays.asList(
                    MessageFormatter.packageMessage(new TowerDetailsMessage(-1, h.towerDetails, 0)),
                    MessageFormatter.packageMessage(new InitMessage(mapSeed, enableFog, h.registry.size(), -1, 0))
                ))
        ));
        
        h.runUpdateLoop();

    }

    @Override
    public String toString() {
        return "StartMessage: " + playerID;
    }
}
