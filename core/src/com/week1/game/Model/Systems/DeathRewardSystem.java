package com.week1.game.Model.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.CrystalCounterComponent;
import com.week1.game.Model.Components.ManaComponent;
import com.week1.game.Model.Components.ManaRewardComponent;
import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.Events.DeathEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DeathRewardSystem implements ISystem, Subscriber<DeathEvent> {

    Queue<DeathEvent> deaths = new ConcurrentLinkedQueue<>();

    private IntMap<CrystalCounterComponent> crystalCounterComponents = new IntMap<>();
    private IntMap<ManaComponent> manaComponents = new IntMap<>(); // maps playerID to manaComponent
    private IntMap<ManaRewardComponent> manaRewardComponents = new IntMap<>(); // maps entityID to rewardComponent

    private IService<Integer, PositionComponent> crystalService;

    public DeathRewardSystem() {
    }

    @Override
    public void update(float delta) {
        for (DeathEvent event: deaths) {
            int reward = manaRewardComponents.get(event.victimID).deathReward;
            ManaComponent manaComponent = manaComponents.get(event.damagerPlayerID);
            if (manaComponent != null) {
                manaComponent.mana +=reward;
            }
            
            Gdx.app.log("DeathRewardSystem - lji1", "Rewarding player: " + event.damagerPlayerID +
                    " with " + reward + " mana for their kill");

            PositionComponent pC = crystalService.query(event.victimID);
            if (pC != null) {
                CrystalCounterComponent crystalCounterComponent = crystalCounterComponents.get(event.damagerPlayerID);

            }
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
    public void process(DeathEvent deathEvent) {
        deaths.add(deathEvent);
    }

    public void addManaReward(int entID, ManaRewardComponent manaRewardComponent) {
        manaRewardComponents.put(entID, manaRewardComponent);
    }
    
    public void addMana(int playerID, ManaComponent manaComponent) {
        manaComponents.put(playerID, manaComponent);
    }

    public void addCrystalCounters(int playerID, CrystalCounterComponent crystalCounterComponent) {
        crystalCounterComponents.put(playerID, crystalCounterComponent);
    }

    public void addCrystalService(IService<Integer, PositionComponent> crystalService) {
        this.crystalService = crystalService;
    }
}
