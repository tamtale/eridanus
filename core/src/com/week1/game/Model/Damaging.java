package com.week1.game.Model;

import static com.week1.game.Model.StatsConfig.tempMinionRange;

public interface Damaging {
    public boolean hasUnitInRange(Unit victim);
    public float getDamage();
    int getPlayerId();
}
