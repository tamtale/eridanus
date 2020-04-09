package com.week1.game.Model.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Model.Components.HealthComponent;
import com.week1.game.Model.Components.OwnedComponent;
import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.CrystalToStateAdapter;
import com.week1.game.Model.Damage;
import com.week1.game.Model.GameState;
import com.week1.game.Renderer.RenderConfig;

import javax.swing.text.Position;

import static com.week1.game.Renderer.TextureUtils.makeTexture;

/*
 * Crystals are damageable entities that give mana any time it's hit by a unit.
 */
public class Crystal extends Damageable {
    
    public int ID;
   
    private CrystalToStateAdapter adapter; // TODO: does this fit?
    
    private PositionComponent positionComponent;
    private HealthComponent healthComponent;
//    private static final OwnedComponent ownedComponent = new OwnedComponent(-1); // owned by -1, so that the crystal appears as enemy to all units

    public Crystal(PositionComponent positionComponent, HealthComponent healthComponent, int ID) {
        this.positionComponent = positionComponent;
        this.healthComponent = healthComponent;
        this.ID = ID;
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
        return this.positionComponent.position.x;
    }

    @Override
    public float getY() {
        return this.positionComponent.position.y;
    }

    @Override
    public float getZ() { return this.positionComponent.position.z; }


    public boolean isDead() {
        return this.healthComponent.curHealth <= 0;
    }

    @Override
    public int getPlayerId() {
        return -1;
    }

    @Override
    public void getPos(Vector3 pos) {
        pos.set(this.positionComponent.position.x, this.positionComponent.position.y, this.positionComponent.position.z);

    }

    @Override
    public float getCurrentHealth() {
        return healthComponent.curHealth;
    }
    @Override
    public float getMaxHealth() {
        return healthComponent.maxHealth;
    }

    @Override
    public boolean takeDamage(Damaging attacker, double dmg, Damage.type damageType) {
        this.healthComponent.curHealth -= dmg;
        adapter.rewardPlayer(attacker.getPlayerId(), dmg);
        if (this.healthComponent.curHealth <= 0) {
            return true;
        } else {
            return false;
        }
    }


    public PositionComponent getPositionComponent() {
        return positionComponent;
    }
}

