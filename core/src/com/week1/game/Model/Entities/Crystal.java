package com.week1.game.Model.Entities;

import com.badlogic.gdx.math.Vector3;
import com.week1.game.Model.Components.HealthComponent;
import com.week1.game.Model.Components.ManaRewardComponent;
import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.Damage;

/*
 * Crystals are damageable entities that give mana any time it's hit by a unit.
 */
public class Crystal extends Damageable {
    public int ID;
   
    private PositionComponent positionComponent;
    private HealthComponent healthComponent;
    private ManaRewardComponent manaRewardComponent;

    public Crystal(PositionComponent positionComponent, HealthComponent healthComponent, ManaRewardComponent manaRewardComponent, int ID) {
        this.positionComponent = positionComponent;
        this.healthComponent = healthComponent;
        this.manaRewardComponent = manaRewardComponent;
        this.ID = ID;
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
        //TODO: delete (unused)
//        this.healthComponent.curHealth -= dmg;
//        adapter.rewardPlayer(attacker.getPlayerId(), dmg);
//        if (this.healthComponent.curHealth <= 0) {
//            return true;
//        } else {
//            return false;
//        }
        return false;
    }


    public PositionComponent getPositionComponent() {
        return positionComponent;
    }
}

