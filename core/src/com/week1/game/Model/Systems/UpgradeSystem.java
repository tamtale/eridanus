package com.week1.game.Model.Systems;

import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.UpgradeComponent;

public class UpgradeSystem implements ISystem{

    private IntMap<UpgradeComponent> upgradeComponents = new IntMap<>();
    private IntMap<Boolean> isUpgraded = new IntMap<>();

    @Override
    public void update(float delta) {
        for (IntMap.Entry<UpgradeComponent> upgradeComponent: upgradeComponents){
            int key = upgradeComponent.key;
            UpgradeComponent value = upgradeComponent.value;
            if (value.damageDealt > value.damageRequired && !isUpgraded.get(key)) {
                value.upgrade.query(key);
                isUpgraded.put(key, true);
            } if (isUpgraded.get(key)){
                value.ticks -= 1;
            } if (value.ticks < 0){
                value.undo.query(key);
                value.damageDealt = 0;
            }
        }
    }

    @Override
    public void remove(int entID) {

    }

    public void addUpgradeComponent(int id, UpgradeComponent upgradeComponent){
        upgradeComponents.put(id, upgradeComponent);
        isUpgraded.put(id, false);
    }

}
