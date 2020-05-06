package com.week1.game.Model.Components;

import static com.week1.game.Model.StatsConfig.manaRegenRate;

public class ManaComponent extends AComponent {
    public double mana; // keeps track of the player's mana
    public double regenRate;
    
    public ManaComponent(double mana) {
        this.mana = mana;
        this.regenRate = manaRegenRate;
    }
    
    public boolean useMana(double amount) {
        if (amount > mana){
            return false;
        } else {
            mana -= amount;
            return true;
        }
    }
    
    @Override
    public String toString() {
        return "ManaComponent{" + 
                "mana=" + mana +
                "}";
                
    }
}
