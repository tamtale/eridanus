package com.week1.game.Model;

import static com.week1.game.Model.StatsConfig.tempMinionRange;

public interface Damaging {
    boolean hasUnitInRange(Unit victim);
    double getDamage();
    int getPlayerId();
}
