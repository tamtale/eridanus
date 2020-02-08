package com.week1.game.Model;

import com.week1.game.Model.Entities.TowerType;

import java.util.HashMap;
import java.util.Map;

public class TowerInfo {
    
    Map<Integer, TowerFootprint> footprints = new HashMap<Integer, TowerFootprint>(){{ // TODO: dynamically loaded from an initialization message
        this.put(TowerType.BASIC.ordinal(), new TowerFootprint());
        this.put(TowerType.SNIPER.ordinal(), new TowerFootprint());
        this.put(TowerType.TANK.ordinal(), new TowerFootprint());
    }};
   
    public TowerInfo() {
        
    }
    
    public TowerFootprint getTowerFootprint(int towerType) {
        return footprints.get(towerType);
    }
}
