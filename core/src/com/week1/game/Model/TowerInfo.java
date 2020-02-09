package com.week1.game.Model;

import com.week1.game.Model.Entities.TowerType;

import java.util.HashMap;
import java.util.Map;

public class TowerInfo {
    
    private TowerDetails[][] details;

    public TowerInfo(TowerDetails[][] details) {
        this.details = details;
    }
    
    public TowerDetails getTowerDetails(int playerId, int towerType) {
        if (playerId == -1 && towerType == -1) {
            return new TowerDetails(new TowerFootprint(TowerFootprint.fpForBase), 500, -1, -1, 0);
        }
        return details[playerId][towerType];
    }
}
