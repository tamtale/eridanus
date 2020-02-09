package com.week1.game.Model;

public class TowerFootprint {
    private final static int MAXFOOTPRINTSIZE = 8;
    
    public final static boolean[][] fpForBasic = {
            {false, false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false},
            {false, false, false, true, true, true, false, false, false},
            {false, false, false, true, true, true, false, false, false},
            {false, false, false, true, true, true, false, false, false},
            {false, false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false}
    }; 
    
    public final static boolean[][] fpForBase = {
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true}
    }; 
    
    private boolean[][] fp;
    
    public TowerFootprint(boolean[][] fp) {
        this.fp = fp;
    }
    
    public static boolean overlap(TowerFootprint footprint1, int x1, int y1, TowerFootprint footprint2, int x2, int y2) {
        
        // assumes that maximum footprint is 5 x 5, so if further than that, just return immediately
        if (Math.abs(x1 - x2) >= MAXFOOTPRINTSIZE || Math.abs(y1 - y2) >= MAXFOOTPRINTSIZE) {
            return false;
        }
        
        // potential footprint overlap
        int xOffset = x1 - x2;
        int yOffset = y1 - y2;
        
        // only iterate over potentially conflicting indices
        for (int i = Math.max(0, -1 * xOffset); i < Math.min(MAXFOOTPRINTSIZE,  MAXFOOTPRINTSIZE - xOffset); i++) {
            for (int j = Math.max(0, -1 * yOffset); j < Math.min(MAXFOOTPRINTSIZE,  MAXFOOTPRINTSIZE - yOffset); j++) {
                if (footprint1.fp[i][j] && footprint2.fp[i + xOffset][j + yOffset]) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
