package com.week1.game.TowerBuilder;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public class TowerPresets {

    TowerLogic tg;

    private Tower tower1;
    private Tower tower2;
    private Tower tower3;
    private Tower tower4;
    private Tower tower5;
    private Tower tower6;

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

        logoT = tg.getTower(logoTlayout);

        logoClayout.add(new BlockSpec(2, 0,0,0));
        logoClayout.add(new BlockSpec(3, 0,1,0));
        logoClayout.add(new BlockSpec(3, 0,2,0));
        logoClayout.add(new BlockSpec(5, 0,3,0));
        logoClayout.add(new BlockSpec(2, 0,4,0));
        logoClayout.add(new BlockSpec(4, 0,4,-1));
        logoClayout.add(new BlockSpec(5, 0,4,-2));
        logoClayout.add(new BlockSpec(2, 0,0,-1));
        logoClayout.add(new BlockSpec(4, 0,0,-2));

        logoC = tg.getTower(logoClayout);
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

        tower1 =  tg.getTower(layout2);


        //Tower 2
        layout2 = new ArrayList<>();
        layout2.add(new BlockSpec(4, 0,0,0));
        layout2.add(new BlockSpec(4, 0,1,0));
        layout2.add(new BlockSpec(4, 0,2,0));
        layout2.add(new BlockSpec(4, 0,3,0));
        layout2.add(new BlockSpec(7, 0,4,0));
        layout2.add(new BlockSpec(6, 0,5,0));
        layout2.add(new BlockSpec(5, 0,6,0));

        tower2 =  tg.getTower(layout2);

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


        tower3 = tg.getTower(layout2);

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


        tower4 = tg.getTower(layout2);

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


        tower5 = tg.getTower(layout2);

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


        tower6 = tg.getTower(layout2);


    }

    public TowerPresets() {
        tg = new TowerLogic();
        initPresets();
        initLogo();

    }

    public Tower getTower1() {
        return tower1;
    }

    public Tower getTower2() {
        return tower2;
    }

    public Tower getTower3() {
        return tower3;
    }

    public Tower getTower4() {
        return tower4;
    }

    public Tower getTower5() {
        return tower5;
    }

    public Tower getTower6() {
        return tower6;
    }

}
