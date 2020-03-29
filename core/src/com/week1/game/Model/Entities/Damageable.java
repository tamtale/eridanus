package com.week1.game.Model.Entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Model.Damage;
import com.week1.game.Renderer.RenderConfig;

import static com.week1.game.Model.Entities.HealthBar.*;

public abstract class Damageable {

    public interface DamageableVisitor<T> {
        T acceptTower(Tower tower);
        T acceptUnit(Unit u);
        T acceptBase(PlayerBase base);
        T acceptCrystal(Crystal crystal);
    }

    @FunctionalInterface
    public interface DamageableCommand<T> {
        T execute(Damageable damageable);
    }

    /* This is a short-hand version for taking normal damage as default */
    public boolean takeDamage(double dmg) {
        return takeDamage(dmg, Damage.type.BASIC);
    }

    /*
     * This function must be implemented. It returns false if the entity was not destroyed
     * and returns true if the entity was destroyed
     */
    public abstract boolean takeDamage(double dmg, Damage.type damageType);

    public abstract boolean isDead();
    public abstract int getPlayerId();

    /*
     * Sets the position of pos to be the position of the damageable.
     */
    abstract void getPos(Vector3 pos);

    /*
     * Sets the position displayPos to the display position of the damageable, as rendered on the screen.
     * By default, will just give the logical position of the damageable.
     */
    void getDisplayPos(Vector3 displayPos) {
        getPos(displayPos);
    }

    abstract float getX();
    abstract float getY();

    abstract float getCurrentHealth();
    abstract float getMaxHealth();

    /*
     * Mana reward for destroying the unit.
     */
    public abstract float getReward();
    

    /*
     * Used for render calculations. DO NOT ACCESS PUBLICLY lol
     */
    Vector3 unitPosition = new Vector3();
//    Decal hpBar = Decal.newDecal(3, 0.2f, new TextureRegion(healthBarHigh));
    Decal background = null;
    Vector3 lookAt = new Vector3();
    float hpBarWidthFactor = 0.3f;
    float maxWidth;
    
    Decal hpBar;
//    Decal hpBarBackground;
    
    
    public void drawHealthBar(RenderConfig config) {
        // Initialize the healthbar, if previously unrendered
        if (hpBar == null) {
//            hpBarBackground = Decal.newDecal(hpBarWidth, 0.2f, new TextureRegion(healthBarBackground));
            hpBar = Decal.newDecal(maxWidth, 0.1f, new TextureRegion(healthBarHigh));
            maxWidth = hpBarWidthFactor * (float)Math.log(this.getMaxHealth());
        }
        
        DecalBatch batch = config.getDecalBatch();

        // Orient the decal
        Plane p = config.getCam().frustum.planes[0];
        Intersector.intersectLinePlane(
                unitPosition.x, unitPosition.y, unitPosition.z,
                unitPosition.x + p.normal.x, unitPosition.y + p.normal.y, unitPosition.z + p.normal.z,
                p, lookAt);
        hpBar.lookAt(lookAt, config.getCam().up);
        
        // Set the position of the decal
        this.getDisplayPos(unitPosition);
        unitPosition.add(0,0,1.5f);
        hpBar.setPosition(unitPosition);
        
        // Update decal texture (color and size)
        hpBar.setTextureRegion(HealthBar.getHealthBarTexture(this.getCurrentHealth(), this.getMaxHealth()));
        hpBar.setWidth(maxWidth * this.getCurrentHealth() / this.getMaxHealth());
                
        // Add the decal for drawing
//        batch.add(hpBarBackground);
        batch.add(hpBar);
        batch.flush();
    }
    
    public abstract <T> T accept(DamageableVisitor<T> visitor);

}
