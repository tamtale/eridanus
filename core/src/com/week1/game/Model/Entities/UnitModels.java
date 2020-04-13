package com.week1.game.Model.Entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.week1.game.Model.Initializer;

public class UnitModels {

    public static final Model MINION;

//    public static final Model WATER_MINION;

    static {
        String crystalName = "minion/fire_minion/minion_with_weird_face.g3db";
//        String crystalName = "minion/water_minion/minion_with_weird_face.g3db";
//        String crystalName = "minion/earth_minion/minion_with_weird_face.g3db";
        Initializer.assetManager.load(crystalName, Model.class);
        Initializer.assetManager.finishLoading();
        MINION = Initializer.assetManager.get(crystalName, Model.class);

        // Adjust the model to fit nicely into the blocky world
        for (Node node : MINION.nodes) {
            node.scale.set(0.5f,0.5f,0.5f);
            node.rotation.set(0,0,0.25f, 0);
        }
        MINION.calculateTransforms();


    }


}
