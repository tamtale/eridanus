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

    /*
     * Sets the position displayPos to the display position of the damageable, as rendered on the screen.
     * By default, will just give the logical position of the damageable.
     */
    default void getDisplayPos(Vector3 displayPos) {
        getPos(displayPos);
    }

    float getX();
    float getY();

    float getCurrentHealth();
    float getMaxHealth();

    /*
     * Mana reward for destroying the unit.
     */
    float getReward();

    /*
     * Used for render calculations. DO NOT ACCESS PUBLICLY lol
     */
    Vector3 unitPosition = new Vector3();

    default void drawHealthBar(RenderConfig config) {
        float currentHealth = getCurrentHealth();
        float maxHealth = getMaxHealth();
        Batch batch = config.getBatch();
        Camera cam = config.getCam();
        this.getDisplayPos(unitPosition);
        cam.project(unitPosition);
        batch.draw(healthBarBackground, unitPosition.x, unitPosition.y, 10f, 2f);
        batch.draw(getHealthBar(getCurrentHealth(), getMaxHealth()),
            unitPosition.x, unitPosition.y, currentHealth / maxHealth * 10f, 2f);
    }

    <T> T accept(DamageableVisitor<T> visitor);

}
