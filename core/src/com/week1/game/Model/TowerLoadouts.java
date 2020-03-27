package com.week1.game.Model;

import com.week1.game.Pair;
import com.week1.game.TowerBuilder.BlockSpec;
import com.week1.game.TowerBuilder.TowerDetails;
import com.week1.game.TowerBuilder.TowerPresets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TowerLoadouts {
    
    private List<List<TowerDetails>> details = new ArrayList<>();
    private TowerDetails baseDetails = TowerPresets.base;

    public TowerLoadouts(Map<Integer, List<TowerLite>> details) {
        for (List<TowerLite> player: details.values()) {
            List<TowerDetails> playerTowerDetails = new ArrayList<>();
            for (TowerLite tower: player) {
                playerTowerDetails.add(new TowerDetails(tower.getLayout(), tower.getName()));
            }
            this.details.add(playerTowerDetails);
        }
    }
    
    public TowerDetails getTowerDetails(int playerId, int towerType) {
        if (towerType == -1) {
            return baseDetails;
        }
        return details.get(playerId).get(towerType);
    }
}
