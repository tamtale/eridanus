package com.week1.game.Model.Components;

public class ManaComponent extends AComponent {
    public double mana; // keeps track of the player's mana
    
    public ManaComponent(double mana) {
        this.mana = mana;
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
