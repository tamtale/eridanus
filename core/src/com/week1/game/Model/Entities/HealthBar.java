package com.week1.game.Model.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

import static com.week1.game.Renderer.TextureUtils.makeTexture;

public class HealthBar {
    public static TextureRegion healthBarHigh = new TextureRegion(makeTexture(1, 1, Color.GREEN));
    public static TextureRegion healthBarMid = new TextureRegion(makeTexture(1, 1, Color.ORANGE));
    public static TextureRegion healthBarLow = new TextureRegion(makeTexture(1, 1, Color.FIREBRICK));
    public static TextureRegion healthBarBackground = new TextureRegion(makeTexture(1, 1, Color.BLACK));
    
    public static TextureRegion getHealthBarTexture(double hp, double maxHp) {
        double perc = hp / maxHp;
        if (perc > .5) return healthBarHigh;
        else if (perc > .2) return healthBarMid;
        else return healthBarLow;
    }
    
}
