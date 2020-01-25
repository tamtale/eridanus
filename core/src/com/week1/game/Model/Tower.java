package com.week1.game.Model;

import com.badlogic.gdx.graphics.Texture;

public class Tower {
    public float x, y;
    private Texture skin;
    private int playerID;

    public Tower(Texture t, float x, float y, int playerID) {
        skin = t;
        this.x = x;
        this.y = y;
        this.playerID = playerID;
    }

    public Texture getSkin() {
        return skin;
    }
}
