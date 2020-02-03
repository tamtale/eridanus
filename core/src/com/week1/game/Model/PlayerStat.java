package com.week1.game.Model;

import static com.week1.game.Model.StatsConfig.*;

public class PlayerStat {
    private double mana;
    private double regen_rate;

    public static PlayerStat BLANK = new PlayerStat(0,0);

    public PlayerStat(float customMana, float customManaRate){
        this.mana = customMana;
        this.regen_rate = customManaRate;
    }

    public PlayerStat(){
        this.mana = startingMana;
        this.regen_rate = manaRegenRate;
    }

    public double getMana() {
        return mana;
    }

    public boolean useMana(double amount) {
        if (amount > mana){
            return false;
        } else {
            mana -= amount;
            return true;
        }
    }

    public void giveMana(double amount) { this.mana += amount; }

    public void regenMana(float delta) { mana += regen_rate * delta; }
}
