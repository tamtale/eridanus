package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import java.util.Arrays;
import java.util.List;

public class TowerPresets {

//    private static TowerMaterials twrMat = new TowerMaterials();

    public static final Integer NUM_PRESETS = 6;

    private static TowerDetails towerDetails1 = new TowerDetails(Arrays.asList(new BlockSpec(2, 0,0,0),
                new BlockSpec(2, 0, 0, 1),
                new BlockSpec(2, 0, 0, -1),
                new BlockSpec(2, -1, 0, 0),
                new BlockSpec(2, 1, 0, 0),
                new BlockSpec(3, 0, 1, 0),
                new BlockSpec(5, 0, 2, 0)), "Preset 1");

    private static TowerDetails towerDetails2 =  new TowerDetails(Arrays.asList(new BlockSpec(4, 0,0,0),
            new BlockSpec(4, 0,1,0),
            new BlockSpec(4, 0,2,0),
            new BlockSpec(4, 0,3,0),
            new BlockSpec(7, 0,4,0),
            new BlockSpec(6, 0,5,0),
            new BlockSpec(5, 0,6,0)), "Preset 2");

    private static TowerDetails towerDetails3 = new TowerDetails(Arrays.asList(new BlockSpec(2, 0, 0, -1),
        new BlockSpec(2, 0, 0, 0),
        new BlockSpec(7, 0, 0, 1),
        new BlockSpec(2, -1, 0, -1),
        new BlockSpec(7, -1, 0, 0),
        new BlockSpec(2, -1, 0, 1),
        new BlockSpec(7, 1, 0, -1),
        new BlockSpec(2, 1, 0, 0),
        new BlockSpec(2, 1, 0, 1),
        new BlockSpec(2, 2, 0, -1),
        new BlockSpec(7, 2, 0, 0),
        new BlockSpec(2, 2, 0, 1)), "Preset 3");

    private static TowerDetails towerDetails4 = new TowerDetails(Arrays.asList(new BlockSpec(2, 0, 0, 0),
        new BlockSpec(2, 0, 1, 0),
        new BlockSpec(2, 1, 1, 0),
        new BlockSpec(2, -1, 1, 0),
        new BlockSpec(2, 0, 1, -1),
        new BlockSpec(2, 0, 1, 1),
        new BlockSpec(6, -1, 2, 0),
        new BlockSpec(4, 0, 2, 0),
        new BlockSpec(6, 1, 2, 0),
        new BlockSpec(2, -1, 2, 1),
        new BlockSpec(6, 0, 2, 1),
        new BlockSpec(2, 1, 2, 1),
        new BlockSpec(2, -1, 2, -1),
        new BlockSpec(6, 0, 2, -1),
        new BlockSpec(2, 1, 2, -1)), "Preset 4");

    private static TowerDetails towerDetails5 = new TowerDetails(Arrays.asList(new BlockSpec(3, -1, 0, 0),
        new BlockSpec(3, 0, 0, 0),
        new BlockSpec(3, 1, 0, 0),
        new BlockSpec(2, -1, 0, 1),
        new BlockSpec(3, 0, 0, 1),
        new BlockSpec(2, 1, 0, 1),
        new BlockSpec(2, -1, 0, -1),
        new BlockSpec(3, 0, 0, -1),
        new BlockSpec(2, 1, 0, -1),
        new BlockSpec(4, 0, 1, 0),
        new BlockSpec(4, 0, 2, 0),
        new BlockSpec(5, 0, 3, 0),
        new BlockSpec(5, 0, 4, 0)), "Preset 5");

    private static TowerDetails towerDetails6 = new TowerDetails(Arrays.asList(new BlockSpec(2, 0, 0, -1),
        new BlockSpec(2, -1, 0, -1),
        new BlockSpec(2, 0, 0, 0),
        new BlockSpec(2, -1, 0, 0),
        new BlockSpec(2, 0, 0, 1),
        new BlockSpec(2, -1, 0, 1),
        new BlockSpec(5, 0, 1, -1),
        new BlockSpec(4, -1, 1, -1),
        new BlockSpec(6, 0, 1, 0),
        new BlockSpec(6, -1, 1, 0),
        new BlockSpec(4, 0, 1, 1),
        new BlockSpec(5, -1, 1, 1),
        new BlockSpec(3, 0, 2, -1),
        new BlockSpec(3, -1, 2, -1),
        new BlockSpec(3, 0, 2, 0),
        new BlockSpec(3, -1, 2, 0),
        new BlockSpec(3, 0, 2, 1),
        new BlockSpec(3, -1, 2, 1)), "Preset 6");

    public static List<TowerDetails> presets = Arrays.asList(towerDetails1, towerDetails2, towerDetails3, towerDetails4, towerDetails5, towerDetails6);

    public static TowerDetails getTower(int towernum) {
        if (towernum < 1 || towernum > NUM_PRESETS) {
            Gdx.app.log("skv2", "Bad tower preset number"+ towernum);
            return presets.get(0);
        }

        return presets.get(towernum - 1);
    }

}
