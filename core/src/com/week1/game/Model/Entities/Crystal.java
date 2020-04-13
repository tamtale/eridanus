package com.week1.game.Model.Entities;

import com.badlogic.gdx.math.Vector3;
import com.week1.game.Model.Components.HealthComponent;
import com.week1.game.Model.Components.ManaRewardComponent;
import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.Damage;

/*
 * Crystals are damageable entities that give mana any time it's hit by a unit.
 */
public class Crystal {
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

    public float getX() {
        return positionComponent.position.x;
    }

    public float getY() {
        return positionComponent.position.y;
    }

    public float getZ() {
        return positionComponent.position.z;
    }

    public int getPlayerId() {
        return -1;
    }

    public PositionComponent getPositionComponent() {
        return positionComponent;
    }
}

