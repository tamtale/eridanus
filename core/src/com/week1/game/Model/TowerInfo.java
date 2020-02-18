package com.week1.game.Model;

import com.week1.game.Model.Entities.TowerType;
import com.week1.game.TowerBuilder.BlockSpec;
import com.week1.game.TowerBuilder.Tower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TowerInfo {
    
    private List<List<Tower>> details = new ArrayList<>();

    public TowerInfo(List<List<List<BlockSpec>>> details) {
        for (List<List<BlockSpec>> player: details) {
            List<Tower> playerTowers = new ArrayList<>();
            for (List<BlockSpec> tower: player) {
                playerTowers.add(new Tower(tower));
            }
            this.details.add(playerTowers);
        }
    }
    
    public Tower getTowerDetails(int playerId, int towerType) {
        if (playerId == -1 && towerType == -1) {
            return new Tower(new TowerFootprint(TowerFootprint.fpForBase), 500, -1, -1, 0);
        }
        return details.get(playerId).get(towerType);
    }
}
