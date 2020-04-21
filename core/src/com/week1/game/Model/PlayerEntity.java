package com.week1.game.Model;

import com.badlogic.gdx.graphics.Color;
import com.week1.game.Model.Components.*;

public class PlayerEntity {
    
    public static final PlayerEntity BLANK = new PlayerEntity(
            new OwnedComponent(-1),
            new ManaComponent(0),
            new NameComponent("blank"),
            new ColorComponent(Color.WHITE),
            new CrystalCounterComponent(),
            new PlayerStatsComponent()
    );
    
    private OwnedComponent ownedComponent;
    private ManaComponent manaComponent;
    private NameComponent nameComponent;
    private ColorComponent colorComponent;
    private PlayerStatsComponent playerStatsComponent;
    private CrystalCounterComponent crystalCounterComponent;
    
    public PlayerEntity(OwnedComponent ownedComponent, ManaComponent manaComponent, NameComponent nameComponent,
                        ColorComponent colorComponent, CrystalCounterComponent crystalCounterComponent,
                        PlayerStatsComponent playerStatsComponent) {
        this.ownedComponent = ownedComponent;
        this.manaComponent = manaComponent;
        this.nameComponent = nameComponent;
        this.colorComponent = colorComponent;
        this.crystalCounterComponent = crystalCounterComponent;
        this.playerStatsComponent = playerStatsComponent;
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
    
    public Color getColor() {
        return colorComponent.color;
    }

    @Override
    public String toString() {
        return "PlayerStat{" +
                "mana=" + manaComponent.mana +
                '}';
    }

    public double getMinionDamage() {
        return playerStatsComponent.minionDamage;
    }

    public float getMinionHealth() {
        return playerStatsComponent.minionHealth;
    }

    public int getCrystalCount() {
        return crystalCounterComponent.crystalsDestroyed;
    }
}
