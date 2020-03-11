package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.week1.game.Model.Entities.Tower;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TowerPresets {

//    private static TowerMaterials twrMat = new TowerMaterials();


    private static TowerDetails towerDetails1 = new TowerDetails(Arrays.asList(
            new BlockSpec(BlockType.OBSIDIAN, 0,0,0),
            new BlockSpec(BlockType.OBSIDIAN, 0, 0, 1),
            new BlockSpec(BlockType.OBSIDIAN, 0, 0, -1),
            new BlockSpec(BlockType.OBSIDIAN, -1, 0, 0),
            new BlockSpec(BlockType.OBSIDIAN, 1, 0, 0),
//            new BlockSpec(BlockType.MOONSTONE, 0, 1, 0),
            new BlockSpec(BlockType.SPAWNER, 0, 1, 0),
//            new BlockSpec(BlockType.FIRE, 0, 2, 0)), "Preset 1");
            new BlockSpec(BlockType.SPAWNER, 0, 2, 0)), "Preset 1");

    private static TowerDetails towerDetails2 =  new TowerDetails(Arrays.asList(
            new BlockSpec(BlockType.SPACEGOLD, 0,0,0),
            new BlockSpec(BlockType.SPACEGOLD, 0,1,0),
            new BlockSpec(BlockType.SPACEGOLD, 0,2,0),
            new BlockSpec(BlockType.SPACEGOLD, 0,3,0),
            new BlockSpec(BlockType.EARTH, 0,4,0),
            new BlockSpec(BlockType.WATER, 0,5,0),
            new BlockSpec(BlockType.FIRE, 0,6,0)), "Preset 2");

    private static TowerDetails towerDetails3 = new TowerDetails(Arrays.asList(
            new BlockSpec(BlockType.OBSIDIAN, 0, 0, -1),
            new BlockSpec(BlockType.OBSIDIAN, 0, 0, 0),
            new BlockSpec(BlockType.EARTH, 0, 0, 1),
            new BlockSpec(BlockType.OBSIDIAN, -1, 0, -1),
            new BlockSpec(BlockType.EARTH, -1, 0, 0),
            new BlockSpec(BlockType.OBSIDIAN, -1, 0, 1),
            new BlockSpec(BlockType.EARTH, 1, 0, -1),
            new BlockSpec(BlockType.OBSIDIAN, 1, 0, 0),
            new BlockSpec(BlockType.OBSIDIAN, 1, 0, 1),
            new BlockSpec(BlockType.OBSIDIAN, 2, 0, -1),
            new BlockSpec(BlockType.EARTH, 2, 0, 0),
            new BlockSpec(BlockType.OBSIDIAN, 2, 0, 1)), "Preset 3");

    private static TowerDetails towerDetails4 = new TowerDetails(Arrays.asList(
            new BlockSpec(BlockType.OBSIDIAN, 0, 0, 0),
            new BlockSpec(BlockType.OBSIDIAN, 0, 1, 0),
            new BlockSpec(BlockType.OBSIDIAN, 1, 1, 0),
            new BlockSpec(BlockType.OBSIDIAN, -1, 1, 0),
            new BlockSpec(BlockType.OBSIDIAN, 0, 1, -1),
            new BlockSpec(BlockType.OBSIDIAN, 0, 1, 1),
            new BlockSpec(BlockType.WATER, -1, 2, 0),
            new BlockSpec(BlockType.SPACEGOLD, 0, 2, 0),
            new BlockSpec(BlockType.WATER, 1, 2, 0),
            new BlockSpec(BlockType.OBSIDIAN, -1, 2, 1),
            new BlockSpec(BlockType.WATER, 0, 2, 1),
            new BlockSpec(BlockType.OBSIDIAN, 1, 2, 1),
            new BlockSpec(BlockType.OBSIDIAN, -1, 2, -1),
            new BlockSpec(BlockType.WATER, 0, 2, -1),
            new BlockSpec(BlockType.OBSIDIAN, 1, 2, -1)), "Preset 4");

    private static TowerDetails towerDetails5 = new TowerDetails(Arrays.asList(
            new BlockSpec(BlockType.MOONSTONE, -1, 0, 0),
            new BlockSpec(BlockType.MOONSTONE, 0, 0, 0),
            new BlockSpec(BlockType.MOONSTONE, 1, 0, 0),
            new BlockSpec(BlockType.OBSIDIAN, -1, 0, 1),
            new BlockSpec(BlockType.MOONSTONE, 0, 0, 1),
            new BlockSpec(BlockType.OBSIDIAN, 1, 0, 1),
            new BlockSpec(BlockType.OBSIDIAN, -1, 0, -1),
            new BlockSpec(BlockType.MOONSTONE, 0, 0, -1),
            new BlockSpec(BlockType.OBSIDIAN, 1, 0, -1),
            new BlockSpec(BlockType.SPACEGOLD, 0, 1, 0),
            new BlockSpec(BlockType.SPACEGOLD, 0, 2, 0),
            new BlockSpec(BlockType.FIRE, 0, 3, 0),
            new BlockSpec(BlockType.FIRE, 0, 4, 0)), "Preset 5");

    private static TowerDetails towerDetails6 = new TowerDetails(Arrays.asList(
            new BlockSpec(BlockType.OBSIDIAN, 0, 0, -1),
            new BlockSpec(BlockType.OBSIDIAN, -1, 0, -1),
            new BlockSpec(BlockType.OBSIDIAN, 0, 0, 0),
            new BlockSpec(BlockType.OBSIDIAN, -1, 0, 0),
            new BlockSpec(BlockType.OBSIDIAN, 0, 0, 1),
            new BlockSpec(BlockType.OBSIDIAN, -1, 0, 1),
            new BlockSpec(BlockType.FIRE, 0, 1, -1),
            new BlockSpec(BlockType.SPACEGOLD, -1, 1, -1),
            new BlockSpec(BlockType.WATER, 0, 1, 0),
            new BlockSpec(BlockType.WATER, -1, 1, 0),
            new BlockSpec(BlockType.SPACEGOLD, 0, 1, 1),
            new BlockSpec(BlockType.FIRE, -1, 1, 1),
            new BlockSpec(BlockType.MOONSTONE, 0, 2, -1),
            new BlockSpec(BlockType.MOONSTONE, -1, 2, -1),
            new BlockSpec(BlockType.MOONSTONE, 0, 2, 0),
            new BlockSpec(BlockType.MOONSTONE, -1, 2, 0),
            new BlockSpec(BlockType.MOONSTONE, 0, 2, 1),
            new BlockSpec(BlockType.MOONSTONE, -1, 2, 1)), "Preset 6");


    public static TowerDetails highlightGround = new TowerDetails(Arrays.asList(new BlockSpec(BlockType.GROUND_HIGHLIGHT, -2, -1, 0),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, -1, -1, 0),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, 0, -1, 0),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, 1, -1, 0),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, 2, -1, 0),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, -2, -1, 1),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, -1, -1, 1),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, 0, -1, 1),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, 1, -1, 1),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, 2, -1, 1),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, -2, -1, 2),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, -1, -1, 2),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, 0, -1, 2),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, 1, -1, 2),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, 2, -1, 2),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, -2, -1, -1),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, -1, -1, -1),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, 0, -1, -1),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, 1, -1, -1),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, 2, -1, -1),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, -2, -1, -2),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, -1, -1, -2),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, 0, -1, -2),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, 1, -1, -2),
            new BlockSpec(BlockType.GROUND_HIGHLIGHT, 2, -1, -2)),
            "Highlight ground");

    public static TowerDetails base = new TowerDetails(Arrays.asList(
            new BlockSpec(BlockType.SPACEGOLD,-2, 0, -2),
            new BlockSpec(BlockType.OBSIDIAN,-2, 0,  -1),
            new BlockSpec(BlockType.OBSIDIAN,-2, 0, 0),
            new BlockSpec(BlockType.OBSIDIAN,-2, 0, 1),
            new BlockSpec(BlockType.SPACEGOLD,-2, 0, 2),
            new BlockSpec(BlockType.OBSIDIAN, -1, 0, -2),
            new BlockSpec(BlockType.OBSIDIAN, -1, 0,  -1),
            new BlockSpec(BlockType.OBSIDIAN, -1, 0, 0),
            new BlockSpec(BlockType.OBSIDIAN, -1, 0, 1),
            new BlockSpec(BlockType.OBSIDIAN, -1, 0, 2),
            new BlockSpec(BlockType.OBSIDIAN, 0, 0, -2),
            new BlockSpec(BlockType.OBSIDIAN, 0, 0,  -1),
            new BlockSpec(BlockType.OBSIDIAN, 0, 0, 0),
            new BlockSpec(BlockType.OBSIDIAN, 0, 0, 1),
            new BlockSpec(BlockType.OBSIDIAN, 0, 0, 2),
            new BlockSpec(BlockType.OBSIDIAN, 1, 0, -2),
            new BlockSpec(BlockType.OBSIDIAN, 1, 0,  -1),
            new BlockSpec(BlockType.OBSIDIAN, 1, 0, 0),
            new BlockSpec(BlockType.OBSIDIAN, 1, 0, 1),
            new BlockSpec(BlockType.OBSIDIAN, 1, 0, 2),
            new BlockSpec(BlockType.SPACEGOLD, 2, 0, -2),
            new BlockSpec(BlockType.OBSIDIAN, 2, 0,  -1),
            new BlockSpec(BlockType.OBSIDIAN, 2, 0, 0),
            new BlockSpec(BlockType.OBSIDIAN, 2, 0, 1),
            new BlockSpec(BlockType.SPACEGOLD, 2, 0, 2),

            new BlockSpec(BlockType.SPACEGOLD,-2, 1, -2),
            new BlockSpec(BlockType.OBSIDIAN,-2, 1,  -1),
            new BlockSpec(BlockType.OBSIDIAN,-2, 1, 0),
            new BlockSpec(BlockType.OBSIDIAN,-2, 1, 1),
            new BlockSpec(BlockType.SPACEGOLD,-2, 1, 2),
            new BlockSpec(BlockType.OBSIDIAN, -1, 1, -2),
            new BlockSpec(BlockType.OBSIDIAN, -1, 1,  -1),
            new BlockSpec(BlockType.OBSIDIAN, -1, 1, 0),
            new BlockSpec(BlockType.OBSIDIAN, -1, 1, 1),
            new BlockSpec(BlockType.OBSIDIAN, -1, 1, 2),
            new BlockSpec(BlockType.OBSIDIAN, 0, 1, -2),
            new BlockSpec(BlockType.OBSIDIAN, 0, 1,  -1),
            new BlockSpec(BlockType.OBSIDIAN, 0, 1, 0),
            new BlockSpec(BlockType.OBSIDIAN, 0, 1, 1),
            new BlockSpec(BlockType.OBSIDIAN, 0, 1, 2),
            new BlockSpec(BlockType.OBSIDIAN, 1, 1, -2),
            new BlockSpec(BlockType.OBSIDIAN, 1, 1,  -1),
            new BlockSpec(BlockType.OBSIDIAN, 1, 1, 0),
            new BlockSpec(BlockType.OBSIDIAN, 1, 1, 1),
            new BlockSpec(BlockType.OBSIDIAN, 1, 1, 2),
            new BlockSpec(BlockType.SPACEGOLD, 2, 1, -2),
            new BlockSpec(BlockType.OBSIDIAN, 2, 1,  -1),
            new BlockSpec(BlockType.OBSIDIAN, 2, 1, 0),
            new BlockSpec(BlockType.OBSIDIAN, 2, 1, 1),
            new BlockSpec(BlockType.SPACEGOLD, 2, 1, 2),
            new BlockSpec(BlockType.MOONSTONE, 0, 2, 0),
            new BlockSpec(BlockType.MOONSTONE, -1, 2, -1),
            new BlockSpec(BlockType.MOONSTONE, -1, 2, 1),
            new BlockSpec(BlockType.MOONSTONE, 1, 2, -1),
            new BlockSpec(BlockType.MOONSTONE, 1, 2, 1)
    ), "Base");


    public static List<TowerDetails> presets = Arrays.asList(towerDetails1, towerDetails2, towerDetails3, towerDetails4, towerDetails5, towerDetails6);
    public static final Integer NUM_PRESETS = 6;

    public static TowerDetails getTower(int towernum) {
        if (towernum < 1 || towernum > NUM_PRESETS) {
            Gdx.app.log("skv2", "Bad tower preset number"+ towernum);
            return presets.get(0);
        }

        return presets.get(towernum - 1);
    }



    public static TowerDetails getBuildCore() {
        ArrayList<BlockSpec> layout = new ArrayList<>();
        layout.add(new BlockSpec(BlockType.OBSIDIAN, 0, 0, 0));
        return new TowerDetails(layout, "Building core");

    }
}
