package com.week1.game.Model;

import com.badlogic.gdx.graphics.Color;

/**
 * This class is passed over the network so the host gets the info about the
 * name and what color the person wants to be.
 */
public class PlayerInfo {
    private String playerName;
    private Color color;

    public PlayerInfo(String playerName, Color color) {
        this.playerName = playerName;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public String getPlayerName() {
        return playerName;
    }
}
