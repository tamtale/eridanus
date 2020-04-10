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
    
    Queue<DamageEvent> deaths = new ConcurrentLinkedQueue<>();

    public DeathSystem(IService<Integer, Void> deleteService) {
        this.deleteService = deleteService;
    }

    @Override
    public void update(float delta) {
        for (DamageEvent event: deaths) {
            Gdx.app.log("DeathSystem", "deleting " + event.victimID);
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

}
