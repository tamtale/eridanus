package com.week1.game.Networking.Messages.Control.HostControl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.week1.game.Networking.Messages.Control.ClientControl.JoinedPlayersMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Host;
import com.week1.game.Networking.NetworkObjects.Player;
import com.week1.game.Pair;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import static com.week1.game.Model.PlayerInfo.defaultColor;

public class SubmitFactionMessage extends HostControlMessage  {
    private final static MessageType MESSAGE_TYPE = MessageType.SUBMITCOLOR;
    private Pair<String, Color> faction;

    public SubmitFactionMessage(int playerID, Pair<String, Color> faction) {
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


        Set<Color> selectedColors = new HashSet<>();
        for (Player p : h.registry.values()) {
           if( p.getColor().equals(defaultColor)) {
               sendNewColors(h, false);
               return;
           }
           Gdx.app.debug("SubmitFactionMessage", "Color is " + p.getColor() + " vs " + defaultColor);
           selectedColors.add(p.getColor());
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
