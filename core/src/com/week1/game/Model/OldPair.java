package com.week1.game.Model;

public class OldPair {
    private Damaging first;
    private Damageable second;
    public OldPair(Damaging attacker, Damageable victim) {
        first = attacker;
        second = victim;
    }

    public Damaging getFirst() {
        return first;
    }

    public Damageable getSecond() {
        return second;
    }
}
