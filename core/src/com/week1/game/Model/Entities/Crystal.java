package com.week1.game.Model.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Model.CrystalToStateAdapter;
import com.week1.game.Model.Damage;
import com.week1.game.Model.GameState;
import com.week1.game.Renderer.RenderConfig;

import static com.week1.game.Renderer.TextureUtils.makeTexture;

/*
 * Crystals are damageable entities that give mana any time it's hit by a unit.
 */
public class Crystal extends Damageable {

    public  static int SIZE = 1;

    private static Texture selectedSkin = makeTexture(SIZE, SIZE, Color.CYAN);
    private Vector3 position = new Vector3();
    
    private CrystalToStateAdapter adapter;

    public Crystal(float x, float y, float z) {
        position.set(x, y, z);
    }
    
    public void setCrystalToStateAdapter(CrystalToStateAdapter adapter) {
        this.adapter = adapter;
    }

    public void draw(Batch batch) {
        batch.draw(selectedSkin, position.x - (SIZE / 2f), position.y - (SIZE / 2f), SIZE, SIZE);
    }

    @Override
    public float getReward() {
        return 100;
    }

    @Override
    public <T> T accept(DamageableVisitor<T> visitor) {
        return visitor.acceptCrystal(this);
    }

    public Texture getSelectedSkin(){
        return selectedSkin;
    }


    @Override
    public float getX() {
        return position.x;
    }

    @Override
    public float getY() {
        return position.y;
    }



    public boolean isDead() {
        return this.currentHealth <= 0;
    }

    @Override
    public int getPlayerId() {
        return -1;
    }

    @Override
    public void getPos(Vector3 pos) {
        System.out.println("Crystal position: " + position);
        pos.set(position);

    }


    private static final float maxHealth = 2000;
    private float currentHealth = 2000;
    
    @Override
    public float getCurrentHealth() {
        return currentHealth;
    }
    @Override
    public float getMaxHealth() {
        return maxHealth;
    }


    @Override
    public boolean takeDamage(Damaging attacker, double dmg, Damage.type damageType) {
        this.currentHealth -= dmg;
        adapter.rewardPlayer(attacker.getPlayerId(), dmg);
        if (this.currentHealth <= 0) {
            return true;
        } else {
            return false;
        }
    }
//    @Override
//    public void drawHealthBar(RenderConfig config) {
//        // Don't draw a health bar
//        System.out.println("Drawing crystal healthbar");
//    }
}

