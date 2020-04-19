package com.week1.game.Networking.Messages.Control.HostControl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.week1.game.Model.Entities.UnitModel;
import com.week1.game.Networking.Messages.Control.ClientControl.JoinedPlayersMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Host;
import com.week1.game.Networking.NetworkObjects.Player;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;


/*
 * This is when a player selects their Faction/Color combo on the second part of the connection screen
 */
public class SubmitFactionMessage extends HostControlMessage  {
    private final static MessageType MESSAGE_TYPE = MessageType.SUBMITCOLOR;
    private String faction;

    public SubmitFactionMessage(int playerID, String faction) {
        super(playerID, MESSAGE_TYPE);
        this.faction = faction;
    }

    @Override
    public void updateHost(Host h, InetAddress addr, int port) {
        Gdx.app.debug("pjb3", "THE PLAYER ID IS " + playerID + " " + h.registry.size() + " " + h.registry.keySet());
        for (Player p : h.registry.values()) {
            if (p.playerId == playerID) {
                p.setFaction(faction);
            }
        }


        Set<String> selectedColors = new HashSet<>();
        for (Player p : h.registry.values()) {
           if( p.getFaction().equals("Factionless")) {
               sendNewColors(h, false);
               return;
           }
           Gdx.app.debug("SubmitFactionMessage", "Color is " + p.getFaction() + " vs null");
           selectedColors.add(p.getFaction());
        }
        if (selectedColors.size() != h.registry.size()) {
            // Not enough different factions. Send not ready because of duplicate colors.
            sendNewColors(h, false);
        } else {
            // We have enough factions selected!
            sendNewColors(h, true);
        }
    }

    public void sendNewColors(Host h, boolean isReady) {
        for (Player p : h.registry.values()) {
            h.sendMessage(MessageFormatter.packageMessage(new JoinedPlayersMessage(-1, h.getJoinedPlayers(), isReady)), p);
        }
    }


}
