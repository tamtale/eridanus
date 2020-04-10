package com.week1.game.Model.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.ManaComponent;
import com.week1.game.Model.Components.ManaRewardComponent;
import com.week1.game.Model.Events.DamageEvent;
import com.week1.game.Pair;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DeathRewardSystem implements ISystem, Subscriber<DamageEvent> {

    Queue<DamageEvent> deaths = new ConcurrentLinkedQueue<>();
    
    private IntMap<ManaComponent> manaComponents = new IntMap<>(); // maps playerID to manaComponent
    private IntMap<ManaRewardComponent> manaRewardComponents = new IntMap<>(); // maps entityID to rewardComponent

    public DeathRewardSystem() {
    }

    @Override
    public void update(float delta) {
        for (DamageEvent event: deaths) {
            int reward = manaRewardComponents.get(event.victimID).deathReward;
            manaComponents.get(event.damagerPlayerID).mana += reward;
            
            Gdx.app.log("DeathRewardSystem - lji1", "Rewarding player: " + event.damagerPlayerID +
                    " with " + reward + " mana for their kill");
        }
        deaths.clear();
    }

    @Override
    public void remove(int entID) {
        manaRewardComponents.remove(entID);
    }
    
    public void removePlayer(int playerID) {
        manaComponents.remove(playerID);
    }

    @Override
    public void process(DamageEvent damageEvent) {
        deaths.add(damageEvent);
    }

    public void addManaReward(int entID, ManaRewardComponent manaRewardComponent) {
        manaRewardComponents.put(entID, manaRewardComponent);
    }
    
    public void addMana(int playerID, ManaComponent manaComponent) {
        manaComponents.put(playerID, manaComponent);
    }
}
