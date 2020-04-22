package com.week1.game.Model.Components;

import com.week1.game.Model.Systems.IService;

public class UpgradeComponent extends AComponent {
    public float damageDealt;
    public float damageRequired;
    public IService<Integer, Void> upgrade;
    public IService<Integer, Void> undo;
    public int ticks;

    public UpgradeComponent(float damageRequired, int ticks, IService<Integer, Void> upgrade, IService<Integer, Void> undo){
        this.damageDealt = 0;
        this.damageRequired = damageRequired;
        this.ticks = ticks;
        this.upgrade = upgrade;
        this.undo = undo;
    }
}
