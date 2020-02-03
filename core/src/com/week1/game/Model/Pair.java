package com.week1.game.Model;

public class Pair {
    private Damaging first;
    private Damageable second;
    public Pair(Damaging attacker, Damageable victim) {
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
