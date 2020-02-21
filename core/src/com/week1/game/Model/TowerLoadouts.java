package com.week1.game.Model;

import com.week1.game.TowerBuilder.BlockSpec;
import com.week1.game.TowerBuilder.TowerDetails;

import java.util.ArrayList;
import java.util.List;

public class TowerLoadouts {
    
    private List<List<TowerDetails>> details = new ArrayList<>();

    public TowerLoadouts(List<List<List<BlockSpec>>> details) {
        for (List<List<BlockSpec>> player: details) {
            List<TowerDetails> playerTowerDetails = new ArrayList<>();
            for (List<BlockSpec> tower: player) {
                playerTowerDetails.add(new TowerDetails(tower, ""));
            }
            this.details.add(playerTowerDetails);
        }
    }
    
    public TowerDetails getTowerDetails(int playerId, int towerType) {
        if (playerId == -1 && towerType == -1) {
            return new TowerDetails(new TowerFootprint(TowerFootprint.fpForBase), 500, -1, -1, 0);
        }
        return details.get(playerId).get(towerType);
    }
}
