package com.week1.game.Model;

import com.week1.game.Model.Entities.TowerType;

import java.util.HashMap;
import java.util.Map;

public class TowerInfo {
    
    Map<Integer, TowerFootprint> footprints = new HashMap<Integer, TowerFootprint>(){{ // TODO: dynamically loaded from an initialization message
        this.put(-1, new TowerFootprint(TowerFootprint.fpForBase));
        this.put(TowerType.BASIC.ordinal(), new TowerFootprint(TowerFootprint.fpForBasic));
        this.put(TowerType.SNIPER.ordinal(), new TowerFootprint(TowerFootprint.fpForBasic));
        this.put(TowerType.TANK.ordinal(), new TowerFootprint(TowerFootprint.fpForBasic));
    }};
   
    public TowerInfo() {
        
    }
    
    public TowerFootprint getTowerFootprint(int towerType) {
        return footprints.get(towerType);
    }
}
