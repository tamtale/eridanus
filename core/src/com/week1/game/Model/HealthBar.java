package com.week1.game.Model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import static com.week1.game.Model.Unit.makeTexture;

public class HealthBar {
    public static Texture healthBarHigh = makeTexture(Color.GREEN);
    public static Texture healthBarMid = makeTexture(Color.ORANGE);
    public static Texture healthBarLow = makeTexture(Color.FIREBRICK);
    public static Texture healthBarBackground = makeTexture(Color.BLACK);
    
    public static Texture getHealthBar(double hp, double maxHp) {
        double perc = hp / maxHp;
        if (perc > .5) return healthBarHigh;
        else if (perc > .2) return healthBarMid;
        else return healthBarLow;
    }


}
