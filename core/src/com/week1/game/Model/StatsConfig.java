package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;

public class StatsConfig {
    static final int PLAYERNOTASSIGNED = -1;

    static final double startingMana = 100;
    static final double manaRegenRate = 1;
    static final double towerDestructionBonus = 0.5; // This will be multiplies by the cost of the tower to determine bonus mana.
    static final double playerBaseBonus = 200;

    static final double playerBaseInitialHp = 500;

    static public final double placementRange = 15;
  
    // Basic
    static public final double tempTower1Cost = 75;
    static public final double tempTower1Health = 75;
    static public final double tempTower1Damage = 2.5;
    static public final double tempTower1Range = 15;
    static public final Pixmap basicTexture = new Pixmap(Gdx.files.internal("towertransparent.png"));


    // Sniper
    static public final double tempTower2Cost = 150;
    static public final double tempTower2Health = 10;
    static public final double tempTower2Damage = 5;
    static public final double tempTower2Range = 20;
    static public final Pixmap sniperTexture = new Pixmap(Gdx.files.internal("tower3.png"));

    // Tank
    static public final double tempTower3Cost = 100;
    static public final double tempTower3Health = 100;
    static public final double tempTower3Damage = 2;
    static public final double tempTower3Range = 10;
    static public final Pixmap tankTexture = new Pixmap(Gdx.files.internal("tower3.png"));

    static public final double tempMinion1Cost = 20;
    static public final double tempMinion1Health = 50;
    
    static public final double tempMinionRange = 8;

    static public final double tempDamage = 1;

    public enum attackType {
        BASIC_DMG,
        SPLASH_DMG,
    }
}
