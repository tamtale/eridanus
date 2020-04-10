package com.week1.game.Model.Systems;

import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.ManaComponent;

import static com.week1.game.Model.StatsConfig.manaRegenRate;

/*
 * System responsible for regenerating players' mana.
 */
public class ManaRegenSystem implements ISystem {

    private IntMap<ManaComponent> manaComponents = new IntMap<>();

    @Override
    public void update(float delta) {
        manaComponents.values().forEach((manaComponent) -> {
            manaComponent.mana += manaRegenRate * delta;
        });
    }

    @Override
    public void remove(int playerID) {
        manaComponents.remove(playerID);
    }

    public void addMana(int playerID, ManaComponent manaComponent) {
        manaComponents.put(playerID, manaComponent);
    }
}
