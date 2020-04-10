package com.week1.game.Model;

public class StatsConfig {
    static final int PLAYERNOTASSIGNED = -1;

    static final double startingMana = 1000;
    public static final double manaRegenRate = 1;
    static public final double towerDestructionBonus = 0.5; // This will be multiplies by the cost of the tower to determine bonus mana.

    static public final double placementRange = 15;

    static public final double tempMinion1Cost = 10;
    static public final double tempMinion1Health = 50;
    
    static public final double tempMinionRange = 8;

    static public final double tempDamage = 1;
    
    public static final int CRYSTAL_RESPAWN_INTERVAL = 200; 
    public static final int SECONDARY_CRYSTAL_RESPAWN_INTERVAL = 10;
    public static final int CRYSTAL_HEALTH = 700;
    
    

    public enum attackType {
        BASIC_DMG,
        SPLASH_DMG,
    }
}
