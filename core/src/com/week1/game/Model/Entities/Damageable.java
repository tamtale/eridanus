package com.week1.game.Model.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.ScreenUtils;
import com.week1.game.Model.Damage;
import com.week1.game.Renderer.RenderConfig;

import java.util.Arrays;

import static com.week1.game.Model.Entities.HealthBar.*;

public abstract class Damageable {

    public interface DamageableVisitor<T> {
        T acceptTower(Tower tower);
        T acceptUnit(Unit u);
        T acceptBase(PlayerBase base);
        T acceptCrystal(Crystal crystal);
    }

    /* This is a short-hand version for taking normal damage as default */
    public boolean takeDamage(Damaging attacker, double dmg) {
        return takeDamage(attacker, dmg, Damage.type.BASIC);
    }

    /*
     * This function must be implemented. It returns false if the entity was not destroyed
     * and returns true if the entity was destroyed
     */
    public abstract boolean takeDamage(Damaging attacker, double dmg, Damage.type damageType);

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
    abstract float getZ();

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

    
    Decal floatingName = null;
    public void drawName(RenderConfig config) {

        // Initialize the healthbar, if previously unrendered
        if (floatingName == null) {
            // Make a pixmap
            
            Pixmap fontPixmap = new Pixmap(Gdx.files.internal(new BitmapFont().getData().getImagePath(0)));

//            SpriteBatch spriteBatch = new SpriteBatch();
//
//            FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, 100, 100, false);
//            frameBuffer.begin();
//
//            Gdx.gl.glClearColor(0, 0, 1, 1);
//            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//            Matrix4 normalProjection = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());
//            spriteBatch.setProjectionMatrix(normalProjection);
//
//            spriteBatch.begin();
//            spriteBatch.setColor(Color.RED);
//
//            //do some drawing ***here's where you draw your dynamic texture***
//            font.draw(spriteBatch, "5\n6\n2016",  30, 30);
//
//            spriteBatch.end();//finish write to buffer
//
//            Pixmap map = ScreenUtils.getFrameBufferPixmap(0, 0, 100, 100);//write frame buffer to Pixmap
//
//            frameBuffer.end();
//            frameBuffer.dispose();
//            spriteBatch.dispose(); 
//            
//            
//            Pixmap map = new Pixmap(100, 100, Pixmap.Format.RGB888);
//            map.setColor(Color.BLUE);
//            map.fill();
            
            // pixmap -> texture
            Texture textTexture = new Texture(fontPixmap);
            
            // texture -> texture region
            TextureRegion textTextureRegion = new TextureRegion(textTexture);
            
            floatingName = Decal.newDecal(10, 10, textTextureRegion);
        }

        DecalBatch batch = config.getDecalBatch();

        // Orient the decal
        Plane p = config.getCam().frustum.planes[0];
        Intersector.intersectLinePlane(
                unitPosition.x, unitPosition.y, unitPosition.z,
                unitPosition.x + p.normal.x, unitPosition.y + p.normal.y, unitPosition.z + p.normal.z,
                p, lookAt);
        floatingName.lookAt(lookAt, config.getCam().up);

        // Set the position of the decal
        this.getDisplayPos(unitPosition);
        unitPosition.add(0,0,3.5f);
        floatingName.setPosition(unitPosition);

//        // Update decal texture (color and size)
//        hpBar.setTextureRegion(HealthBar.getHealthBarTexture(this.getCurrentHealth(), this.getMaxHealth()));
//        hpBar.setWidth(maxWidth * this.getCurrentHealth() / this.getMaxHealth());

        // Add the decal for drawing
        batch.add(floatingName);
        batch.flush();
    }
    
    public abstract <T> T accept(DamageableVisitor<T> visitor);

}
