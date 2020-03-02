package com.week1.game.Model.Entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Model.Damage;
import com.week1.game.Renderer.RenderConfig;

import static com.week1.game.Model.Entities.HealthBar.getHealthBar;
import static com.week1.game.Model.Entities.HealthBar.healthBarBackground;

public interface Damageable {

    interface DamageableVisitor<T> {
        T acceptTower(Tower tower);
        T acceptUnit(Unit u);
        T acceptBase(PlayerBase base);
        T acceptCrystal(Crystal crystal);
    }

    /* This is a short-hand version for taking normal damage as default */
    default boolean takeDamage(double dmg) {
        return takeDamage(dmg, Damage.type.BASIC);
    }

    /*
     * This function must be implemented. It returns false if the entity was not destroyed
     * and returns true if the entity was destroyed
     */
    boolean takeDamage(double dmg, Damage.type damageType);

    boolean isDead();
    int getPlayerId();

    /*
     * Sets the position of pos to be the position of the damageable.
     */
    void getPos(Vector3 pos);
    float getX();
    float getY();

    float getCurrentHealth();
    float getMaxHealth();

    /*
     * Mana reward for destroying the unit.
     */
    float getReward();


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

    /*
     * Used for render calculations. DO NOT ACCESS PUBLICLY lol
     */
    Vector3 unitPosition = new Vector3();

    default void drawHealthBar(RenderConfig config) {
        float currentHealth = getCurrentHealth();
        float maxHealth = getMaxHealth();
        Batch batch = config.getBatch();
        Camera cam = config.getCam();
        this.getPos(unitPosition);
        cam.project(unitPosition);
        batch.draw(healthBarBackground, unitPosition.x, unitPosition.y, 5, 1f);
        batch.draw(getHealthBar(getCurrentHealth(), getMaxHealth()),
            unitPosition.x, unitPosition.y, currentHealth / maxHealth * 5f, 1f);
    }

    <T> T accept(DamageableVisitor<T> visitor);

}
