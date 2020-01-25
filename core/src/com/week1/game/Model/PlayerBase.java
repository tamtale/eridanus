package com.week1.game.Model;

import com.badlogic.gdx.graphics.Texture;
import com.week1.game.Networking.Player;

public class PlayerBase {
    public float x, y;
    private float hp;
    private int playerID;
    private Texture skin;

    public PlayerBase(float initialHp, float x, float y, int playerID, Texture skin) {
        this.skin = skin;
        this.hp = initialHp;
        this.playerID = playerID;
        this.x = x;
        this.y = y;
    }

    public Texture getSkin() {
        return skin;
    }
}
