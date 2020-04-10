package com.week1.game.Model.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.ManaRewardComponent;
import com.week1.game.Model.Events.DamageEvent;
import com.week1.game.Pair;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DeathSystem implements ISystem, Subscriber<DamageEvent> {

    /* Service to remove an entity from the GameState, given its ID.*/
    IService<Integer, Void> deleteService;
    /* Service to reward the player who killed the dying entity. */
    IService<Pair<Integer, Integer>, Void> rewardService;
    Queue<DamageEvent> deaths = new ConcurrentLinkedQueue<>();
    
    private IntMap<ManaRewardComponent> manaRewardComponents = new IntMap<>();

    public DeathSystem(IService<Integer, Void> deleteService, IService<Pair<Integer, Integer>, Void> rewardService) {
        this.deleteService = deleteService;
        this.rewardService = rewardService;
    }

    @Override
    public void update(float delta) {
        for (DamageEvent event: deaths) {
            Gdx.app.log("DeathSystem", "deleting " + event.victimID);
            rewardService.query(new Pair<>(event.damagerPlayerID, manaRewardComponents.get(event.victimID).deathReward));
            deleteService.query(event.victimID);
        }
        deaths.clear();
    }

    @Override
    public void remove(int entID) {
    }

    @Override
    public void process(DamageEvent damageEvent) {
        deaths.add(damageEvent);
    }

    public void addManaReward(int entID, ManaRewardComponent manaRewardComponent) {
        manaRewardComponents.put(entID, manaRewardComponent);
    }
}
