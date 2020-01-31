package com.week1.game.Model;

import com.week1.game.Networking.Player;

import static com.week1.game.Model.StatsConfig.*;

public class PlayerStat {
    private float mana;
    private float regen_rate;

    public static PlayerStat BLANK = new PlayerStat(0,0);

    public PlayerStat(float customMana, float customManaRate){
        this.mana = customMana;
        this.regen_rate = customManaRate;
    }

    public PlayerStat(){
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

    public void regenMana(float delta) {
        mana += regen_rate * delta;
    }
}
