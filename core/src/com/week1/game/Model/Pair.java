package com.week1.game.Model;

import com.week1.game.Model.Entities.Damageable;
import com.week1.game.Model.Entities.Damaging;

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