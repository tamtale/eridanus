package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TowerPresets {

//    private static TowerMaterials twrMat = new TowerMaterials();


    private static TowerDetails towerDetails1 = new TowerDetails(Arrays.asList(
            new BlockSpec(BlockType.ETHERITE, 0,0,0),
            new BlockSpec(BlockType.ETHERITE, 0, 0, 1),
            new BlockSpec(BlockType.ETHERITE, 0, 0, -1),
            new BlockSpec(BlockType.ETHERITE, -1, 0, 0),
            new BlockSpec(BlockType.ETHERITE, 1, 0, 0),
            new BlockSpec(BlockType.KUIPERIUM, 0, 1, 0),
            new BlockSpec(BlockType.FIRE, 0, 2, 0)), "Basic");

    private static TowerDetails towerDetails2 =  new TowerDetails(Arrays.asList(
            new BlockSpec(BlockType.NOVACORE, 0,0,0),
            new BlockSpec(BlockType.NOVACORE, 0,1,0),
            new BlockSpec(BlockType.NOVACORE, 0,2,0),
            new BlockSpec(BlockType.NOVACORE, 0,3,0),
            new BlockSpec(BlockType.EARTH, 0,4,0),
            new BlockSpec(BlockType.WATER, 0,5,0),
            new BlockSpec(BlockType.FIRE, 0,6,0)), "Sniper");

    //TODO -
    private static TowerDetails towerDetails3 = new TowerDetails(Arrays.asList(
            new BlockSpec(BlockType.ETHERITE, 0, 0, -1),
            new BlockSpec(BlockType.ETHERITE, 0, 0, 0),
            new BlockSpec(BlockType.EARTH, 0, 0, 1),
            new BlockSpec(BlockType.ETHERITE, -1, 0, -1),
            new BlockSpec(BlockType.EARTH, -1, 0, 0),
            new BlockSpec(BlockType.ETHERITE, -1, 0, 1),
            new BlockSpec(BlockType.EARTH, 1, 0, -1),
            new BlockSpec(BlockType.ETHERITE, 1, 0, 0),
            new BlockSpec(BlockType.ETHERITE, 1, 0, 1),
            new BlockSpec(BlockType.ETHERITE, 2, 0, -1),
            new BlockSpec(BlockType.EARTH, 2, 0, 0),
            new BlockSpec(BlockType.ETHERITE, 2, 0, 1)), "Spawner");

    private static TowerDetails towerDetails4 = new TowerDetails(Arrays.asList(
            new BlockSpec(BlockType.NOVACORE, 0, 2, 0),
            new BlockSpec(BlockType.ETHERITE, 0, 1, 0),
            new BlockSpec(BlockType.WATER, 1, 1, 0),
            new BlockSpec(BlockType.WATER, -1, 1, 0),
            new BlockSpec(BlockType.WATER, 0, 1, -1),
            new BlockSpec(BlockType.WATER, 0, 1, 1),
            new BlockSpec(BlockType.KUIPERIUM, -1, 0, 0),
            new BlockSpec(BlockType.ETHERITE, 0, 0, 0),
            new BlockSpec(BlockType.KUIPERIUM, 1, 0, 0),
            new BlockSpec(BlockType.ETHERITE, -1, 0, 1),
            new BlockSpec(BlockType.KUIPERIUM, 0, 0, 1),
            new BlockSpec(BlockType.ETHERITE, 1, 0, 1),
            new BlockSpec(BlockType.ETHERITE, -1, 0, -1),
            new BlockSpec(BlockType.KUIPERIUM, 0, 0, -1),
            new BlockSpec(BlockType.ETHERITE, 1, 0, -1)), "Fountain");

    private static TowerDetails towerDetails5 = new TowerDetails(Arrays.asList(
            new BlockSpec(BlockType.KUIPERIUM, -1, 0, 0),
            new BlockSpec(BlockType.KUIPERIUM, 0, 0, 0),
            new BlockSpec(BlockType.KUIPERIUM, 1, 0, 0),
            new BlockSpec(BlockType.ETHERITE, -1, 0, 1),
            new BlockSpec(BlockType.KUIPERIUM, 0, 0, 1),
            new BlockSpec(BlockType.ETHERITE, 1, 0, 1),
            new BlockSpec(BlockType.ETHERITE, -1, 0, -1),
            new BlockSpec(BlockType.KUIPERIUM, 0, 0, -1),
            new BlockSpec(BlockType.ETHERITE, 1, 0, -1),
            new BlockSpec(BlockType.NOVACORE, 0, 1, 0),
            new BlockSpec(BlockType.NOVACORE, 0, 2, 0),
            new BlockSpec(BlockType.FIRE, 0, 3, 0),
            new BlockSpec(BlockType.FIRE, 0, 4, 0)), "Balanced boi");

    //TODO - change to be buff
    private static TowerDetails towerDetails6 = new TowerDetails(Arrays.asList(
            new BlockSpec(BlockType.ETHERITE, 0, 0, -1),
            new BlockSpec(BlockType.ETHERITE, -1, 0, -1),
            new BlockSpec(BlockType.ETHERITE, 0, 0, 0),
            new BlockSpec(BlockType.ETHERITE, -1, 0, 0),
            new BlockSpec(BlockType.ETHERITE, 0, 0, 1),
            new BlockSpec(BlockType.ETHERITE, -1, 0, 1),
            new BlockSpec(BlockType.FIRE, 0, 1, -1),
            new BlockSpec(BlockType.NOVACORE, -1, 1, -1),
            new BlockSpec(BlockType.WATER, 0, 1, 0),
            new BlockSpec(BlockType.WATER, -1, 1, 0),
            new BlockSpec(BlockType.NOVACORE, 0, 1, 1),
            new BlockSpec(BlockType.FIRE, -1, 1, 1),
            new BlockSpec(BlockType.KUIPERIUM, 0, 2, -1),
            new BlockSpec(BlockType.KUIPERIUM, -1, 2, -1),
            new BlockSpec(BlockType.KUIPERIUM, 0, 2, 0),
            new BlockSpec(BlockType.KUIPERIUM, -1, 2, 0),
            new BlockSpec(BlockType.KUIPERIUM, 0, 2, 1),
            new BlockSpec(BlockType.KUIPERIUM, -1, 2, 1)), "Tank");


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
            new BlockSpec(BlockType.NOVACORE,-2, 0, -2),
            new BlockSpec(BlockType.ETHERITE,-2, 0,  -1),
            new BlockSpec(BlockType.ETHERITE,-2, 0, 0),
            new BlockSpec(BlockType.ETHERITE,-2, 0, 1),
            new BlockSpec(BlockType.NOVACORE,-2, 0, 2),
            new BlockSpec(BlockType.ETHERITE, -1, 0, -2),
            new BlockSpec(BlockType.ETHERITE, -1, 0,  -1),
            new BlockSpec(BlockType.ETHERITE, -1, 0, 0),
            new BlockSpec(BlockType.ETHERITE, -1, 0, 1),
            new BlockSpec(BlockType.ETHERITE, -1, 0, 2),
            new BlockSpec(BlockType.ETHERITE, 0, 0, -2),
            new BlockSpec(BlockType.ETHERITE, 0, 0,  -1),
            new BlockSpec(BlockType.ETHERITE, 0, 0, 0),
            new BlockSpec(BlockType.ETHERITE, 0, 0, 1),
            new BlockSpec(BlockType.ETHERITE, 0, 0, 2),
            new BlockSpec(BlockType.ETHERITE, 1, 0, -2),
            new BlockSpec(BlockType.ETHERITE, 1, 0,  -1),
            new BlockSpec(BlockType.ETHERITE, 1, 0, 0),
            new BlockSpec(BlockType.ETHERITE, 1, 0, 1),
            new BlockSpec(BlockType.ETHERITE, 1, 0, 2),
            new BlockSpec(BlockType.NOVACORE, 2, 0, -2),
            new BlockSpec(BlockType.ETHERITE, 2, 0,  -1),
            new BlockSpec(BlockType.ETHERITE, 2, 0, 0),
            new BlockSpec(BlockType.ETHERITE, 2, 0, 1),
            new BlockSpec(BlockType.NOVACORE, 2, 0, 2),

            new BlockSpec(BlockType.NOVACORE,-2, 1, -2),
            new BlockSpec(BlockType.ETHERITE,-2, 1,  -1),
            new BlockSpec(BlockType.ETHERITE,-2, 1, 0),
            new BlockSpec(BlockType.ETHERITE,-2, 1, 1),
            new BlockSpec(BlockType.NOVACORE,-2, 1, 2),
            new BlockSpec(BlockType.ETHERITE, -1, 1, -2),
            new BlockSpec(BlockType.ETHERITE, -1, 1,  -1),
            new BlockSpec(BlockType.ETHERITE, -1, 1, 0),
            new BlockSpec(BlockType.ETHERITE, -1, 1, 1),
            new BlockSpec(BlockType.ETHERITE, -1, 1, 2),
            new BlockSpec(BlockType.ETHERITE, 0, 1, -2),
            new BlockSpec(BlockType.ETHERITE, 0, 1,  -1),
            new BlockSpec(BlockType.ETHERITE, 0, 1, 0),
            new BlockSpec(BlockType.ETHERITE, 0, 1, 1),
            new BlockSpec(BlockType.ETHERITE, 0, 1, 2),
            new BlockSpec(BlockType.ETHERITE, 1, 1, -2),
            new BlockSpec(BlockType.ETHERITE, 1, 1,  -1),
            new BlockSpec(BlockType.ETHERITE, 1, 1, 0),
            new BlockSpec(BlockType.ETHERITE, 1, 1, 1),
            new BlockSpec(BlockType.ETHERITE, 1, 1, 2),
            new BlockSpec(BlockType.NOVACORE, 2, 1, -2),
            new BlockSpec(BlockType.ETHERITE, 2, 1,  -1),
            new BlockSpec(BlockType.ETHERITE, 2, 1, 0),
            new BlockSpec(BlockType.ETHERITE, 2, 1, 1),
            new BlockSpec(BlockType.NOVACORE, 2, 1, 2),
            new BlockSpec(BlockType.KUIPERIUM, 0, 2, 0),
            new BlockSpec(BlockType.KUIPERIUM, -1, 2, -1),
            new BlockSpec(BlockType.KUIPERIUM, -1, 2, 1),
            new BlockSpec(BlockType.KUIPERIUM, 1, 2, -1),
            new BlockSpec(BlockType.KUIPERIUM, 1, 2, 1)
    ), "Base");

    //These are
    public static TowerDetails guns = new TowerDetails(Arrays.asList(new BlockSpec(BlockType.FIRE, -2, 0, 0),
            new BlockSpec(BlockType.EARTH, 0, 0, 0),
            new BlockSpec(BlockType.WATER, 2, 0, 0)),
            "guns");


    public static TowerDetails materials = new TowerDetails(Arrays.asList(new BlockSpec(BlockType.KUIPERIUM, -2, 0, 0),
            new BlockSpec(BlockType.ETHERITE, 0, 0, 0),
            new BlockSpec(BlockType.NOVACORE, 2, 0, 0)),
            "materials");

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
        layout.add(new BlockSpec(BlockType.ETHERITE, 0, 0, 0));
        return new TowerDetails(layout, "Building core");

    }
}
