package com.week1.game.Model;

public interface Damageable {
    static final int NORMAL_DMG = 1;
    static final int SPLASH_DMG = 2;

    /* This is a short-hand version for taking normal damage as default */
    default boolean takeDamage(float dmg) {
        return takeDamage(dmg, NORMAL_DMG);
    }

    /*
     * This function must be implemented. It returns false if the entity was not destroyed
     * and returns true if the entity was destroyed
     */
    boolean takeDamage(float dmg, int damageType);
}
