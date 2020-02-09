package com.week1.game.Model;

import com.week1.game.Model.Entities.TowerType;

import java.util.HashMap;
import java.util.Map;

public class TowerInfo {
    
    private Map<Integer, TowerDetails> base = new HashMap<Integer, TowerDetails>() {{
        this.put(-1, new TowerDetails(new TowerFootprint(TowerFootprint.fpForBase)));
    }};
    
//    private Map<Integer, TowerFootprint> defaultTowers = new HashMap<Integer, TowerFootprint>() {{ // TODO: shouldn't need this either 
//            this.put(TowerType.BASIC.ordinal(), new TowerFootprint(TowerFootprint.fpForBasic));
//            this.put(TowerType.SNIPER.ordinal(), new TowerFootprint(TowerFootprint.fpForBasic));
//            this.put(TowerType.TANK.ordinal(), new TowerFootprint(TowerFootprint.fpForBasic));
//        }};
    
    private TowerDetails[][] details; 
//    = new HashMap<Integer, Map<Integer, TowerFootprint>>(){{ // TODO: dynamically loaded from an initialization message
//        this.put(-1, base);
//        this.put(0, defaultTowers);
//        this.put(1, defaultTowers);
//        this.put(2, defaultTowers);
//        this.put(3, defaultTowers);
//        this.put(4, defaultTowers);
//    }};
   
    public TowerInfo(TowerDetails[][] details) {
        this.details = details;
    }
    
    public TowerFootprint getTowerFootprint(int playerId, int towerType) {
        return details[playerId][towerType].footprint;
    }
}
