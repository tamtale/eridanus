package com.week1.game.Model.Entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.week1.game.Model.Damage;

import static com.week1.game.Model.Entities.HealthBar.getHealthBar;
import static com.week1.game.Model.Entities.HealthBar.healthBarBackground;

public interface Damageable {

    /* This is a short-hand version for taking normal damage as default */
    default boolean takeDamage(double dmg) {
        return takeDamage(dmg, Damage.type.BASIC);
    }

    /*
     * This function must be implemented. It returns false if the entity was not destroyed
     * and returns true if the entity was destroyed
     */
    boolean takeDamage(double dmg, Damage.type damageType);

    float getX();
    float getY();
    boolean isDead();
    int getPlayerId();
    
    default void drawHealthBar(Batch batch, float x, float y, float offset, int sideLength, double currentHp, double maxHp) {
       
        float xPosition = x - (sideLength / 2f) + offset;
        float yPosition = y + ((sideLength / 2f) + 0.5f) + offset;
        
        batch.draw(healthBarBackground, 
                xPosition, yPosition,
                sideLength, .5f);
        batch.draw(getHealthBar(currentHp, maxHp), 
                xPosition, yPosition,
                (float)((currentHp / maxHp) * sideLength), .5f);
        
    }
}
