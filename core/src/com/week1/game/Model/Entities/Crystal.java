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


    private Vector3 position = new Vector3();
    
    private CrystalToStateAdapter adapter;

    public Crystal(float x, float y, float z) {
        position.set(x, y, z);
    }
    
    public void setCrystalToStateAdapter(CrystalToStateAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public float getReward() {
        return 100;
    }

    @Override
    public <T> T accept(DamageableVisitor<T> visitor) {
        return visitor.acceptCrystal(this);
    }

    @Override
    public float getX() {
        return position.x;
    }

    @Override
    public float getY() {
        return position.y;
    }

    @Override
    public float getZ() { return position.z; }


    public boolean isDead() {
        return this.currentHealth <= 0;
    }

    @Override
    public int getPlayerId() {
        return -1;
    }

    @Override
    public void getPos(Vector3 pos) {
        pos.set(position);

    }


    private static final float maxHealth = 1000;
    private float currentHealth = 1000;
    
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
}

