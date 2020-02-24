package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import java.util.Arrays;
import java.util.List;

public class TowerPresets {

//    private static TowerMaterials twrMat = new TowerMaterials();

    public static final Integer NUM_PRESETS = 6;

    private static TowerDetails towerDetails1 = new TowerDetails(Arrays.asList(
            new BlockSpec(BlockType.OBSIDIAN, 0,0,0),
            new BlockSpec(BlockType.OBSIDIAN, 0, 0, 1),
            new BlockSpec(BlockType.OBSIDIAN, 0, 0, -1),
            new BlockSpec(BlockType.OBSIDIAN, -1, 0, 0),
            new BlockSpec(BlockType.OBSIDIAN, 1, 0, 0),
            new BlockSpec(BlockType.MOONSTONE, 0, 1, 0),
            new BlockSpec(BlockType.WATER, 0, 2, 0)));

    private static TowerDetails towerDetails2 =  new TowerDetails(Arrays.asList(
            new BlockSpec(BlockType.SPACEGOLD, 0,0,0),
            new BlockSpec(BlockType.SPACEGOLD, 0,1,0),
            new BlockSpec(BlockType.SPACEGOLD, 0,2,0),
            new BlockSpec(BlockType.SPACEGOLD, 0,3,0),
            new BlockSpec(BlockType.EARTH, 0,4,0),
            new BlockSpec(BlockType.FIRE, 0,5,0),
            new BlockSpec(BlockType.WATER, 0,6,0)));

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
            new BlockSpec(BlockType.OBSIDIAN, 2, 0, 1)));

    private static TowerDetails towerDetails4 = new TowerDetails(Arrays.asList(
            new BlockSpec(BlockType.OBSIDIAN, 0, 0, 0),
            new BlockSpec(BlockType.OBSIDIAN, 0, 1, 0),
            new BlockSpec(BlockType.OBSIDIAN, 1, 1, 0),
            new BlockSpec(BlockType.OBSIDIAN, -1, 1, 0),
            new BlockSpec(BlockType.OBSIDIAN, 0, 1, -1),
            new BlockSpec(BlockType.OBSIDIAN, 0, 1, 1),
            new BlockSpec(BlockType.FIRE, -1, 2, 0),
            new BlockSpec(BlockType.SPACEGOLD, 0, 2, 0),
            new BlockSpec(BlockType.FIRE, 1, 2, 0),
            new BlockSpec(BlockType.OBSIDIAN, -1, 2, 1),
            new BlockSpec(BlockType.FIRE, 0, 2, 1),
            new BlockSpec(BlockType.OBSIDIAN, 1, 2, 1),
            new BlockSpec(BlockType.OBSIDIAN, -1, 2, -1),
            new BlockSpec(BlockType.FIRE, 0, 2, -1),
            new BlockSpec(BlockType.OBSIDIAN, 1, 2, -1)));

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
            new BlockSpec(BlockType.WATER, 0, 3, 0),
            new BlockSpec(BlockType.WATER, 0, 4, 0)));

    private static TowerDetails towerDetails6 = new TowerDetails(Arrays.asList(
            new BlockSpec(BlockType.OBSIDIAN, 0, 0, -1),
            new BlockSpec(BlockType.OBSIDIAN, -1, 0, -1),
            new BlockSpec(BlockType.OBSIDIAN, 0, 0, 0),
            new BlockSpec(BlockType.OBSIDIAN, -1, 0, 0),
            new BlockSpec(BlockType.OBSIDIAN, 0, 0, 1),
            new BlockSpec(BlockType.OBSIDIAN, -1, 0, 1),
            new BlockSpec(BlockType.WATER, 0, 1, -1),
            new BlockSpec(BlockType.SPACEGOLD, -1, 1, -1),
            new BlockSpec(BlockType.FIRE, 0, 1, 0),
            new BlockSpec(BlockType.FIRE, -1, 1, 0),
            new BlockSpec(BlockType.SPACEGOLD, 0, 1, 1),
            new BlockSpec(BlockType.WATER, -1, 1, 1),
            new BlockSpec(BlockType.MOONSTONE, 0, 2, -1),
            new BlockSpec(BlockType.MOONSTONE, -1, 2, -1),
            new BlockSpec(BlockType.MOONSTONE, 0, 2, 0),
            new BlockSpec(BlockType.MOONSTONE, -1, 2, 0),
            new BlockSpec(BlockType.MOONSTONE, 0, 2, 1),
            new BlockSpec(BlockType.MOONSTONE, -1, 2, 1)));

    public static List<TowerDetails> presets = Arrays.asList(towerDetails1, towerDetails2, towerDetails3, towerDetails4, towerDetails5, towerDetails6);

    public static TowerDetails getTower(int towernum) {
        if (towernum < 1 || towernum > NUM_PRESETS) {
            Gdx.app.log("skv2", "Bad tower preset number"+ towernum);
            return presets.get(0);
        }

//        Gdx.app.log("skv2", "Tower " + (towernum) + " footprint: \n" + presets.get(towernum - 1).getFootprint().toString());

        return presets.get(towernum - 1);
    }

}
