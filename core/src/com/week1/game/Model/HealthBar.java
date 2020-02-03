package com.week1.game.Model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class HealthBar {
    private static Texture healthBarHigh = makeTexture(Color.GREEN);
    private static Texture healthBarMid = makeTexture(Color.ORANGE);
    private static Texture healthBarLow = makeTexture(Color.FIREBRICK);
    private static Texture healthBarBackground = makeTexture(Color.BLACK);
    
    public static Texture getHealthBar(double  hp, double maxHp) {
        double perc = hp / maxHp;
        if (perc > .5) return healthBarHigh;
        else if (perc > .2) return healthBarMid;
        else return healthBarLow;
    }


}
