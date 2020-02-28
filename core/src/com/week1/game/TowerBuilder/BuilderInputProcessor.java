package com.week1.game.TowerBuilder;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import javafx.scene.PerspectiveCamera;

public class BuilderInputProcessor extends InputAdapter {

    private TowerBuilderScreen screen;
//
//    //field for constructor -- camera -- so that it can unproject
    public BuilderInputProcessor(TowerBuilderScreen screen) {
        this.screen = screen;
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        System.out.println("builder things registered click " + screenX +  " "+ screenY );

        if (screen.towerStage.isBuildMode){
            screen.towerCam.getObject(screenX, screenY);
        }
        //if click loc is in build area
        //add a block



        return false;
    }

}
