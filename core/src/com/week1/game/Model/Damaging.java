package com.week1.game.Model;

import static com.week1.game.Model.StatsConfig.tempMinionRange;

public interface Damaging {
    boolean hasTargetInRange(Damageable victim);
    double getDamage();
    int getPlayerId();
}
