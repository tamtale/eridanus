package com.week1.game.Model;

import static com.week1.game.Model.StatsConfig.*;

public class PlayerInfo {
    private float mana;
    private float regen_rate;

    public PlayerInfo(){
        this.mana = startingMana;
        this.regen_rate = manaRegenRate;
    }

    public float getMana() {
        return mana;
    }

    public boolean useMana(float amount) {
        if (amount > mana){
            return false;
        } else {
            mana -= amount;
            return true;
        }
    }

    public void regenMana() {
        mana += regen_rate;
    }
}
