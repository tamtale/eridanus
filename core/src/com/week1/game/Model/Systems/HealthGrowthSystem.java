package com.week1.game.Model.Systems;

import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.HealthComponent;

public class HealthGrowthSystem implements ISystem{

    private IntMap<HealthComponent> healthComponents = new IntMap();
    @Override
    public void update(float delta) {
        healthComponents.values().forEach((healthComponent -> {
            healthComponent.curHealth += healthComponent.growthRate * delta;
        }));
    }

    @Override
    public void remove(int entID) {
        healthComponents.remove(entID);
    }

    public void addHealthGrowth(int entId, HealthComponent healthComponent){
        healthComponents.put(entId, healthComponent);
    }
}
