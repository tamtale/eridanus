package com.week1.game.Model;

import com.badlogic.gdx.graphics.Color;
import com.week1.game.Pair;

/**
 * This class is passed over the network so the host gets the info about the
 * name and what color the person wants to be.
 */
public class PlayerInfo {
    public static Color defaultColor = Color.GRAY;
    private String playerName;
    private String factionName = "Factionless";
    private Color color = defaultColor;

    public PlayerInfo(String playerName) {
        this.playerName = playerName;
    }

    public Color getColor() {
        return color;
    }

    public String getFactionName() {
        return factionName;
    }
    public String getPlayerName() {
        return playerName;
    }

    public void setFaction(Pair<String, Color> faction) {
        this.factionName = faction.key;
        this.color = faction.value;
    }
}
