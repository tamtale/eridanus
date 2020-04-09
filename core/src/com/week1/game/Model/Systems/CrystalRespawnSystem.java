package com.week1.game.Model.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.Components.HealthComponent;
import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.Entities.Crystal;
import com.week1.game.Model.Events.DamageEvent;
import com.week1.game.Model.World.Block;
import com.week1.game.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.week1.game.Model.StatsConfig.CRYSTAL_RESPAWN_INTERVAL;
import static com.week1.game.Model.StatsConfig.SECONDARY_CRYSTAL_RESPAWN_INTERVAL;

public class CrystalRespawnSystem implements ISystem, Subscriber<DamageEvent> {

    IService<Integer, PositionComponent> crystalService; // service to check if an id corresponds to a crystal entity
    IService<PositionComponent, Boolean> respawnService; // service to respawn a crystal returns true on successful respawn
    List<Pair<Integer, PositionComponent>> crystalsWaitingForRespawn = new ArrayList<>(); // TODO: doesn't need to be threadsafe, afaik

    public CrystalRespawnSystem(IService<PositionComponent, Boolean> respawnService, IService<Integer, PositionComponent> crystalService) {
        this.respawnService = respawnService;
        this.crystalService = crystalService;
    }

    @Override
    public void update(float delta) {
        List<Pair<Integer, PositionComponent>> remainingWaitingCrystals = new ArrayList<>();

        System.out.println("crystalsWaiting: " + crystalsWaitingForRespawn);
        for (Pair<Integer, PositionComponent> waitingCrystal : crystalsWaitingForRespawn) {

            if (--waitingCrystal.key == 0) { // Decrement turns to wait, are we there yet?
                // crystal is done waiting to respawn

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
    public void process(DamageEvent damageEvent) {
        // Only keep the deaths of crystals
        PositionComponent pC = crystalService.query(damageEvent.victimID);
        if (pC != null) {
            crystalsWaitingForRespawn.add(new Pair<>(CRYSTAL_RESPAWN_INTERVAL, pC));
            System.out.println("keeping crystal death");
        } else {
            System.out.println("throwing away a death");
        }
    }

}
