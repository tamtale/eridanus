package com.week1.game.Model;

public interface Damageable {

    /* This is a short-hand version for taking normal damage as default */
    default boolean takeDamage(double dmg) {
        return takeDamage(dmg, Damage.type.BASIC);
    }

    /*
     * This function must be implemented. It returns false if the entity was not destroyed
     * and returns true if the entity was destroyed
     */
    boolean takeDamage(double dmg, Damage.type damageType); // TODO change this
}
