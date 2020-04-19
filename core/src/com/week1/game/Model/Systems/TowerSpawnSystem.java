package com.week1.game.Model.Systems;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.week1.game.Model.Entities.Tower;
import com.week1.game.Model.Events.DamageEvent;
import javafx.util.Pair;

import javax.xml.crypto.dsig.keyinfo.KeyValue;

import static com.week1.game.Model.StatsConfig.buildDelay;

public class TowerSpawnSystem implements ISystem, Subscriber<DamageEvent> {
    private IntMap<TowerSpawnNode> nodeMap = new IntMap<>();

    private IntSet deadUnfinishedTowers = new IntSet();
    IService<Integer, Void> deleteService;
    IService<Pair<Tower, Integer>, Void> addService;

    public TowerSpawnSystem(IService<Integer, Void> deleteService, IService<Pair<Tower, Integer>, Void> addService){
        this.deleteService = deleteService;
        this.addService = addService;
    }

    @Override
    public void update(float delta) {
        for (TowerSpawnNode node: nodeMap.values()){
            if (node.ticks == 0){
                //deleteService.query(node.dummyId);
                if (!deadUnfinishedTowers.contains(node.dummyId)) {
                    addService.query(new Pair<>(node.tower, node.dummyId));
                }
                nodeMap.remove(node.tower.ID);
            }
            node.ticks--;
        }
    }

    @Override
    public void remove(int entID) {
        nodeMap.remove(entID);
    }

    public void addNode(int id, Tower tower, int dummyId){
        nodeMap.put(id, new TowerSpawnNode(tower, dummyId, buildDelay*5));
    }

    @Override
    public void process(DamageEvent damageEvent) {
        deadUnfinishedTowers.add(damageEvent.victimID);
    }

    static class TowerSpawnNode{
        public Tower tower;
        public int dummyId;
        public int ticks;

        TowerSpawnNode(Tower tower, int dummyId, int ticks){
            this.tower = tower;
            this.dummyId = dummyId;
            this.ticks = ticks;
        }

    }
}
