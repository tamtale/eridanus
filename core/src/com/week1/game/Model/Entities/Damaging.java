package com.week1.game.Model.Entities;

import com.week1.game.Model.Entities.Damageable;

public interface Damaging {
    boolean hasTargetInRange(Damageable victim);
    double getDamage();
    int getPlayerId();
}
