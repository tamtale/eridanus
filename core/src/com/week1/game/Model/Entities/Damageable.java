package com.week1.game.Model.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
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
//        float currentHealth = getCurrentHealth();
//        float maxHealth = getMaxHealth();
//        ModelBatch batch = config.getModelBatch();
//        Camera cam = config.getCam();
//        this.getDisplayPos(unitPosition);
//
//        cam.project(unitPosition); // go from world coordinates to screen coordinates
//        
//        // Make something
//        // Make a model instance from it
//        
//        batch.render()
        drawHealthBar2d(config);
    }
    
    default void drawHealthBar2d(RenderConfig config) {
        float currentHealth = getCurrentHealth();
        float maxHealth = getMaxHealth();
        SpriteBatch batch = config.getBatch();
        Camera cam = config.getCam();
        this.getDisplayPos(unitPosition);

        cam.project(unitPosition); // go from world coordinates to screen coordinates

        float scale = (float)Math.pow(1.1, config.zoomFactor);
        float width = 100 * scale;
        float height = 10 * scale;
        float above = 80 * scale;
        batch.draw(healthBarBackground, unitPosition.x - (width / 2) , unitPosition.y + above, width, height);
        batch.draw(getHealthBar(getCurrentHealth(), getMaxHealth()),
                unitPosition.x - (width / 2), unitPosition.y + above, currentHealth / maxHealth * width, height);
    }

    <T> T accept(DamageableVisitor<T> visitor);

}
