package com.week1.game.Networking.Messages.Control.HostControl;

import com.badlogic.gdx.Gdx;
import com.week1.game.Networking.Messages.Control.ClientControl.UndoReadyToStart;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Host;
import com.week1.game.Networking.NetworkObjects.Player;

import java.net.InetAddress;

/**
 * This is sent to the host by players who have regret for their tower choices and want to set themselves back to not ready
 * so that the game cannot start and they can choose different towers
 */
public class RetractLoadoutMessage extends HostControlMessage {

    private final static MessageType MESSAGE_TYPE = MessageType.RETRACTLOADOUT;

    public RetractLoadoutMessage(int playerID) {
        super(playerID, MESSAGE_TYPE);
    }

    public void updateHost(Host h, InetAddress addr, int port) {
        if (h.gameStarted) {
            Gdx.app.log("pjb3 - potential error", "Edge case: someone tried to retract their loadout after the game started by host");
            return;
        }
        Gdx.app.debug("pjb3", "retracting loadout!!");

        // remove their towers
        h.towerDetails.remove(playerID);

        // Tell all clients that all players are ready to go if they want to do any rendering showing people are ready
        for (Player p : h.registry.values()) {
            h.sendMessage(MessageFormatter.packageMessage(new UndoReadyToStart(-1)), p);
        }
    }
}
