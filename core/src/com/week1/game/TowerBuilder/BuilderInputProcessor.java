package com.week1.game.TowerBuilder;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import javafx.scene.PerspectiveCamera;

public class BuilderInputProcessor extends InputAdapter {

//    private PerspectiveCamera camera;
//
//    //field for constructor -- camera -- so that it can unproject
//    public BuilderInputProcessor(PerspectiveCamera cam) {
//        super();
//        this.camera = cam;
//    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        System.out.println("builder things registered click " + screenX +  " "+ screenY );


        //if click loc is in build area
        //add a block



        return false;
    }

}
