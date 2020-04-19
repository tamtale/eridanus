package com.week1.game.Model.Systems;

import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Entities.Tower;

import static com.week1.game.Model.StatsConfig.buildDelay;

public class TowerSpawnSystem implements ISystem{
    private IntMap<TowerSpawnNode> nodeMap = new IntMap<>();

    IService<Integer, Void> deleteService;
    IService<Tower, Void> addService;

    public TowerSpawnSystem(IService<Integer, Void> deleteService, IService<Tower, Void> addService){
        this.deleteService = deleteService;
        this.addService = addService;
    }

    @Override
    public void update(float delta) {
        for (TowerSpawnNode node: nodeMap.values()){
            if (node.ticks == 0){
                deleteService.query(node.dummyId);
                addService.query(node.tower);
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
