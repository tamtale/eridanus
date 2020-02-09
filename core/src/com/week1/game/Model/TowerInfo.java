package com.week1.game.Model;

import com.week1.game.Model.Entities.TowerType;

import java.util.HashMap;
import java.util.Map;

public class TowerInfo {
    
//    private Map<Integer, TowerDetails> base = new HashMap<Integer, TowerDetails>() {{
//        this.put(-1, new TowerDetails(new TowerFootprint(TowerFootprint.fpForBase)));
//    }};
    
//    private Map<Integer, TowerFootprint> defaultTowers = new HashMap<Integer, TowerFootprint>() {{ // TODO: shouldn't need this either 
//            this.put(TowerType.BASIC.ordinal(), new TowerFootprint(TowerFootprint.fpForBasic));
//            this.put(TowerType.SNIPER.ordinal(), new TowerFootprint(TowerFootprint.fpForBasic));
//            this.put(TowerType.TANK.ordinal(), new TowerFootprint(TowerFootprint.fpForBasic));
//        }};
    
//    = new HashMap<Integer, Map<Integer, TowerFootprint>>(){{ // TODO: dynamically loaded from an initialization message
//        this.put(-1, base);
//        this.put(0, defaultTowers);
//        this.put(1, defaultTowers);
//        this.put(2, defaultTowers);
//        this.put(3, defaultTowers);
//        this.put(4, defaultTowers);
//    }};

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
