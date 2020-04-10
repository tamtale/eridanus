package com.week1.game.Model.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.DamagingComponent;
import com.week1.game.Model.Components.ManaComponent;
import com.week1.game.Model.Components.ManaRewardComponent;
import com.week1.game.Model.Events.DamageEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DamageRewardSystem implements ISystem, Subscriber<DamageEvent> {

    Queue<DamageEvent> damageEvents = new ConcurrentLinkedQueue<>();
    
    private IntMap<ManaComponent> manaComponents = new IntMap<>();
    private IntMap<ManaRewardComponent> manaRewardComponents = new IntMap<>();
    private IntMap<DamagingComponent> damagingComponents = new IntMap<>();

    public DamageRewardSystem() {
    }

    @Override
    public void update(float delta) {
        for (DamageEvent event: damageEvents) {
            int reward = (int)(manaRewardComponents.get(event.victimID).damageReward * damagingComponents.get(event.damagerID).baseDamage);
            manaComponents.get(event.damagerPlayerID).mana += reward;
            
            Gdx.app.log("DamageRewardSystem - lji1", "Rewarding player: " + event.damagerPlayerID +
                    " with " + reward + " mana for damage done");
        }
        damageEvents.clear();
    }

    @Override
    public void remove(int entID) {
        manaRewardComponents.remove(entID);
        damagingComponents.remove(entID);
    }
    
    public void removePlayer(int playerID) {
        manaComponents.remove(playerID);
    }

    @Override
    public void process(DamageEvent damageEvent) {
        damageEvents.add(damageEvent);
    }

    public void addManaReward(int entID, ManaRewardComponent manaRewardComponent) {
        manaRewardComponents.put(entID, manaRewardComponent);
    }
    
    public void addMana(int playerID, ManaComponent manaComponent) {
        manaComponents.put(playerID, manaComponent);
    }

    public void addDamage(int entID, DamagingComponent damagingComponent) {
        damagingComponents.put(entID, damagingComponent);
    }
}
