package com.week1.game.Model;

public interface Damaging {
    boolean hasTargetInRange(Damageable victim);
    double getDamage();
    int getPlayerId();
}
