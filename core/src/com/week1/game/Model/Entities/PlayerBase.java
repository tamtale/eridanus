package com.week1.game.Model.Entities;

import com.week1.game.TowerBuilder.TowerDetails;

public class PlayerBase extends Tower {
    public PlayerBase(float x, float y, float z, TowerDetails towerDetails, int playerID, int towerType) {
        super(x, y, z, towerDetails, playerID, towerType);
    }


    @Override
    public <T> T accept(DamageableVisitor<T> visitor) {
        return visitor.acceptBase(this);
    }

    @Override
    public String toString() {
        return "PlayerBase {" +
            "x=" + x +
            ", y=" + y +
            ", playerID=" + playerID +
            ", towerType=" + towerType +
            ", hp=" + hp +
            ", maxHp=" + maxHp +
            ", dmg=" + dmg +
            ", range=" + range +
            ", cost=" + cost +
            '}';
    }
}
