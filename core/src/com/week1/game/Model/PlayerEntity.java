package com.week1.game.Model;

import com.badlogic.gdx.graphics.Color;
import com.week1.game.Model.Components.ColorComponent;
import com.week1.game.Model.Components.ManaComponent;
import com.week1.game.Model.Components.NameComponent;
import com.week1.game.Model.Components.OwnedComponent;

public class PlayerEntity {
    
    public static final PlayerEntity BLANK = new PlayerEntity(
            new OwnedComponent(-1),
            new ManaComponent(0),
            new NameComponent("blank"),
            new ColorComponent(Color.WHITE)
    );
    
    private OwnedComponent ownedComponent;
    private ManaComponent manaComponent;
    private NameComponent nameComponent;
    private ColorComponent colorComponent;
    
    public PlayerEntity(OwnedComponent ownedComponent, ManaComponent manaComponent, NameComponent nameComponent, ColorComponent colorComponent) {
        this.ownedComponent = ownedComponent;
        this.manaComponent = manaComponent;
        this.nameComponent = nameComponent;
        this.colorComponent = colorComponent;
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
    
    public String getName() {
        return nameComponent.name;
    }

    @Override
    public String toString() {
        return "PlayerStat{" +
                "mana=" + manaComponent.mana +
                '}';
    }
    
}
