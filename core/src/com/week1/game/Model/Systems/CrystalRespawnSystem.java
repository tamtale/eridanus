package com.week1.game.Model.Systems;

import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.Events.DeathEvent;
import com.week1.game.Pair;

import java.util.ArrayList;
import java.util.List;

import static com.week1.game.Model.StatsConfig.CRYSTAL_RESPAWN_INTERVAL;
import static com.week1.game.Model.StatsConfig.SECONDARY_CRYSTAL_RESPAWN_INTERVAL;

public class CrystalRespawnSystem implements ISystem, Subscriber<DeathEvent> {

    IService<Integer, PositionComponent> crystalService; // service to check if an id corresponds to a crystal entity
    IService<PositionComponent, Boolean> respawnService; // service to respawn a crystal returns true on successful respawn
    List<Pair<Integer, PositionComponent>> crystalsWaitingForRespawn = new ArrayList<>(); // TODO: doesn't need to be threadsafe, afaik

    public CrystalRespawnSystem(IService<PositionComponent, Boolean> respawnService) {
        this.respawnService = respawnService;
    }

    @Override
    public void update(float delta) {
        List<Pair<Integer, PositionComponent>> remainingWaitingCrystals = new ArrayList<>();

        for (Pair<Integer, PositionComponent> waitingCrystal : crystalsWaitingForRespawn) {

            if (--waitingCrystal.key == 0) { // Decrement turns to wait, are we there yet?
                // use respawnService to attempt a respawn
                if (!respawnService.query(waitingCrystal.value)) {
                    // respawn failed (presumably because there's something in the way)
                    // try again in a little bit
                    waitingCrystal.key = SECONDARY_CRYSTAL_RESPAWN_INTERVAL;
                    remainingWaitingCrystals.add(waitingCrystal);
                }
            } else {
                // not there yet, keep waiting
                remainingWaitingCrystals.add(waitingCrystal);
            }
        }
        
        // update the remaining waiting crystals 
        crystalsWaitingForRespawn = remainingWaitingCrystals;
    }

    @Override
    public void remove(int entID) {
    }

    @Override
    public void process(DeathEvent deathEvent) {
        // Only keep the deaths of crystals
        PositionComponent pC = crystalService.query(deathEvent.victimID);
        if (pC != null) {
            crystalsWaitingForRespawn.add(new Pair<>(CRYSTAL_RESPAWN_INTERVAL, pC));
        }
    }

    public void addCrystalService(IService<Integer, PositionComponent> crystalService) {
        this.crystalService = crystalService;
    }
}
