package com.week1.game.Model.Systems;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DeathSystem implements ISystem, Subscriber<DeathSystem.DeathEvent> {

    /* Service to remove an entity from the GameState, given its ID.*/
    IService<Integer, Void> deleteService;
    Queue<DeathEvent> deathEvents = new ConcurrentLinkedQueue<>();

    public DeathSystem(IService<Integer, Void> deleteService) {
        this.deleteService = deleteService;
    }

    @Override
    public void update(float delta) {
        for (DeathEvent deathEvent: deathEvents) {
            deleteService.query(deathEvent.victimID);
        }
        deathEvents.clear();
    }

    @Override
    public void remove(int entID) {
    }

    @Override
    public void process(DeathEvent deathEvent) {
    }

    public static class DeathEvent {
        int victimID; // The entity ID of the dead.
        int causeID; // The entity who caused the death.

        public DeathEvent(int victimID, int causeID) {
            this.victimID = victimID;
            this.causeID = causeID;
        }
    }
}
