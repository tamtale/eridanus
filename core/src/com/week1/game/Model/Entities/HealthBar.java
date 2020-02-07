package com.week1.game.Model.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import static com.week1.game.Renderer.TextureUtils.makeTexture;

public class HealthBar {
    public static Texture healthBarHigh = makeTexture(1, 1, Color.GREEN);
    public static Texture healthBarMid = makeTexture(1, 1, Color.ORANGE);
    public static Texture healthBarLow = makeTexture(1, 1, Color.FIREBRICK);
    public static Texture healthBarBackground = makeTexture(1, 1, Color.BLACK);
    
    public static Texture getHealthBar(double hp, double maxHp) {
        double perc = hp / maxHp;
        if (perc > .5) return healthBarHigh;
        else if (perc > .2) return healthBarMid;
        else return healthBarLow;
    }


}
