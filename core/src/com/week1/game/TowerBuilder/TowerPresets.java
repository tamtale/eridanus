package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import java.util.Arrays;
import java.util.List;

public class TowerPresets {

    private static TowerMaterials twrMat = new TowerMaterials();

    public static final Integer NUM_PRESETS = 6;

    private static Tower tower1 = twrMat.getTower(Arrays.asList(new BlockSpec(2, 0,0,0),
                new BlockSpec(2, 0, 0, 1),
                new BlockSpec(2, 0, 0, -1),
                new BlockSpec(2, -1, 0, 0),
                new BlockSpec(2, 1, 0, 0),
                new BlockSpec(3, 0, 1, 0),
                new BlockSpec(5, 0, 2, 0)));

    private static Tower tower2 =  twrMat.getTower(Arrays.asList(new BlockSpec(4, 0,0,0),
            new BlockSpec(4, 0,1,0),
            new BlockSpec(4, 0,2,0),
            new BlockSpec(4, 0,3,0),
            new BlockSpec(7, 0,4,0),
            new BlockSpec(6, 0,5,0),
            new BlockSpec(5, 0,6,0)));

    private static Tower tower3 = twrMat.getTower(Arrays.asList(new BlockSpec(2, 0, 0, -1),
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
        new BlockSpec(2, 2, 0, 1)));

    private static Tower tower4 = twrMat.getTower(Arrays.asList(new BlockSpec(2, 0, 0, 0),
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
        new BlockSpec(2, 1, 2, -1)));

    private static Tower tower5 = twrMat.getTower(Arrays.asList(new BlockSpec(3, -1, 0, 0),
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
        new BlockSpec(5, 0, 4, 0)));

    private static Tower tower6 = twrMat.getTower(Arrays.asList(new BlockSpec(2, 0, 0, -1),
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
        new BlockSpec(3, -1, 2, 1)));


    public static List<Tower> presets = Arrays.asList(tower1, tower2, tower3, tower4, tower5, tower6);


    public static Tower getTower(int towernum) {
        if (towernum < 1 || towernum > NUM_PRESETS) {
            Gdx.app.log("skv2", "Bad tower preset number"+ towernum);
            return presets.get(0);
        }

//        Gdx.app.log("skv2", "Tower " + (towernum) + " footprint: \n" + presets.get(towernum - 1).getFootprint().toString());

        return presets.get(towernum - 1);
    }

}
