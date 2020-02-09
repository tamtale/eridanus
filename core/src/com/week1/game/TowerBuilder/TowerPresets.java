package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;

public class TowerPresets {

    TowerMaterials twrMat;

    public static final Integer NUM_PRESETS = 6;

    private Tower tower1;
    private Tower tower2;
    private Tower tower3;
    private Tower tower4;
    private Tower tower5;
    private Tower tower6;
    private ArrayList<Tower> presets = new ArrayList<>();

    private Tower logoT;
    private Tower logoC;


    private void initLogo() {

        ArrayList<BlockSpec> logoTlayout = new ArrayList<>();
        ArrayList<BlockSpec> logoClayout = new ArrayList<>();

        logoTlayout.add(new BlockSpec(4, 0, 0, 0));
        logoTlayout.add(new BlockSpec(7, 0, 1, 0));
        logoTlayout.add(new BlockSpec(4, 0, 2, 0));
        logoTlayout.add(new BlockSpec(4, 0, 3, 0));
        logoTlayout.add(new BlockSpec(1, 0, 4, 0));
        logoTlayout.add(new BlockSpec(7, 0, 4, -1));
        logoTlayout.add(new BlockSpec(6, 0, 4, 1));
        logoTlayout.add(new BlockSpec(4, 0, 4, 2));
        logoTlayout.add(new BlockSpec(4, 0, 4, -2));

        logoT = twrMat.getTower(logoTlayout);

        logoClayout.add(new BlockSpec(2, 0,0,0));
        logoClayout.add(new BlockSpec(3, 0,1,0));
        logoClayout.add(new BlockSpec(3, 0,2,0));
        logoClayout.add(new BlockSpec(5, 0,3,0));
        logoClayout.add(new BlockSpec(2, 0,4,0));
        logoClayout.add(new BlockSpec(4, 0,4,-1));
        logoClayout.add(new BlockSpec(5, 0,4,-2));
        logoClayout.add(new BlockSpec(2, 0,0,-1));
        logoClayout.add(new BlockSpec(4, 0,0,-2));

        logoC = twrMat.getTower(logoClayout);
    }

    private void initPresets() {
        //Tower 1
        ArrayList<BlockSpec> layout2 = new ArrayList<>();
        layout2.add(new BlockSpec(2, 0,0,0));
        layout2.add(new BlockSpec(2, 0, 0, 1));
        layout2.add(new BlockSpec(2, 0, 0, -1));
        layout2.add(new BlockSpec(2, -1, 0, 0));
        layout2.add(new BlockSpec(2, 1, 0, 0));
        layout2.add(new BlockSpec(3, 0, 1, 0));
        layout2.add(new BlockSpec(5, 0, 2, 0));
//        layout1.add(new BlockSpec(69, 0, 2, 0));

        tower1 =  twrMat.getTower(layout2);


        //Tower 2
        layout2 = new ArrayList<>();
        layout2.add(new BlockSpec(4, 0,0,0));
        layout2.add(new BlockSpec(4, 0,1,0));
        layout2.add(new BlockSpec(4, 0,2,0));
        layout2.add(new BlockSpec(4, 0,3,0));
        layout2.add(new BlockSpec(7, 0,4,0));
        layout2.add(new BlockSpec(6, 0,5,0));
        layout2.add(new BlockSpec(5, 0,6,0));

        tower2 =  twrMat.getTower(layout2);

        //Tower 3
        layout2 = new ArrayList<>();
        layout2.add(new BlockSpec(2, 0, 0, -1));
        layout2.add(new BlockSpec(2, 0, 0, 0));
        layout2.add(new BlockSpec(7, 0, 0, 1));
        layout2.add(new BlockSpec(2, -1, 0, -1));
        layout2.add(new BlockSpec(7, -1, 0, 0));
        layout2.add(new BlockSpec(2, -1, 0, 1));
        layout2.add(new BlockSpec(7, 1, 0, -1));
        layout2.add(new BlockSpec(2, 1, 0, 0));
        layout2.add(new BlockSpec(2, 1, 0, 1));
        layout2.add(new BlockSpec(2, 2, 0, -1));
        layout2.add(new BlockSpec(7, 2, 0, 0));
        layout2.add(new BlockSpec(2, 2, 0, 1));


        tower3 = twrMat.getTower(layout2);

        //Tower 4
        layout2 = new ArrayList<>();
        layout2.add(new BlockSpec(2, 0, 0, 0));
        layout2.add(new BlockSpec(2, 0, 1, 0));
        layout2.add(new BlockSpec(2, 1, 1, 0));
        layout2.add(new BlockSpec(2, -1, 1, 0));
        layout2.add(new BlockSpec(2, 0, 1, -1));
        layout2.add(new BlockSpec(2, 0, 1, 1));
        layout2.add(new BlockSpec(6, -1, 2, 0));
        layout2.add(new BlockSpec(4, 0, 2, 0));
        layout2.add(new BlockSpec(6, 1, 2, 0));
        layout2.add(new BlockSpec(2, -1, 2, 1));
        layout2.add(new BlockSpec(6, 0, 2, 1));
        layout2.add(new BlockSpec(2, 1, 2, 1));
        layout2.add(new BlockSpec(2, -1, 2, -1));
        layout2.add(new BlockSpec(6, 0, 2, -1));
        layout2.add(new BlockSpec(2, 1, 2, -1));


        tower4 = twrMat.getTower(layout2);

        //Tower 5
        layout2 = new ArrayList<>();
        layout2.add(new BlockSpec(3, -1, 0, 0));
        layout2.add(new BlockSpec(3, 0, 0, 0));
        layout2.add(new BlockSpec(3, 1, 0, 0));
        layout2.add(new BlockSpec(2, -1, 0, 1));
        layout2.add(new BlockSpec(3, 0, 0, 1));
        layout2.add(new BlockSpec(2, 1, 0, 1));
        layout2.add(new BlockSpec(2, -1, 0, -1));
        layout2.add(new BlockSpec(3, 0, 0, -1));
        layout2.add(new BlockSpec(2, 1, 0, -1));
        layout2.add(new BlockSpec(4, 0, 1, 0));
        layout2.add(new BlockSpec(4, 0, 2, 0));
        layout2.add(new BlockSpec(5, 0, 3, 0));
        layout2.add(new BlockSpec(5, 0, 4, 0));


        tower5 = twrMat.getTower(layout2);

        //Tower 6
        layout2 = new ArrayList<>();
        layout2.add(new BlockSpec(2, 0, 0, -1));
        layout2.add(new BlockSpec(2, -1, 0, -1));
        layout2.add(new BlockSpec(2, 0, 0, 0));
        layout2.add(new BlockSpec(2, -1, 0, 0));
        layout2.add(new BlockSpec(2, 0, 0, 1));
        layout2.add(new BlockSpec(2, -1, 0, 1));
        layout2.add(new BlockSpec(5, 0, 1, -1));
        layout2.add(new BlockSpec(4, -1, 1, -1));
        layout2.add(new BlockSpec(6, 0, 1, 0));
        layout2.add(new BlockSpec(6, -1, 1, 0));
        layout2.add(new BlockSpec(4, 0, 1, 1));
        layout2.add(new BlockSpec(5, -1, 1, 1));
        layout2.add(new BlockSpec(3, 0, 2, -1));
        layout2.add(new BlockSpec(3, -1, 2, -1));
        layout2.add(new BlockSpec(3, 0, 2, 0));
        layout2.add(new BlockSpec(3, -1, 2, 0));
        layout2.add(new BlockSpec(3, 0, 2, 1));
        layout2.add(new BlockSpec(3, -1, 2, 1));


        tower6 = twrMat.getTower(layout2);


        presets.add(tower1);
        presets.add(tower2);
        presets.add(tower3);
        presets.add(tower4);
        presets.add(tower5);
        presets.add(tower6);
    }

    public TowerPresets() {
        twrMat = new TowerMaterials();
        initPresets();
        initLogo();

    }

    public Tower getTower(int towernum) {
        if (towernum < 1 || towernum > NUM_PRESETS) {
            Gdx.app.log("skv2", "Bad tower preset number"+ towernum);
            return tower1;
        }

//        Gdx.app.log("skv2", "Tower " + (towernum) + " footprint: \n" + presets.get(towernum - 1).getFootprint().toString());

        return presets.get(towernum - 1);
    }

}
