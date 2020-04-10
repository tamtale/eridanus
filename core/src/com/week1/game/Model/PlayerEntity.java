package com.week1.game.Model;

import com.week1.game.Model.Components.ManaComponent;
import com.week1.game.Model.Components.OwnedComponent;

public class PlayerEntity {
    
    public static final PlayerEntity BLANK = new PlayerEntity(
            new OwnedComponent(-1),
            new ManaComponent(0)
    );
    
    private OwnedComponent ownedComponent;
    private ManaComponent manaComponent;
    
    public PlayerEntity(OwnedComponent ownedComponent, ManaComponent manaComponent) {
        this.ownedComponent = ownedComponent;
        this.manaComponent = manaComponent;
    }
    
    public int getPlayerID() {
        return ownedComponent.playerID;
    }
    
    public double getMana() {
        return manaComponent.mana;
    }

    public boolean useMana(double amount) {
        if (amount > manaComponent.mana){
            return false;
        } else {
            manaComponent.mana -= amount;
            return true;
        }
    }

    @Override
    public String toString() {
        return "PlayerStat{" +
                "mana=" + manaComponent.mana +
                '}';
    }
    
}
