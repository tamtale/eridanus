package com.week1.game.Model.Entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.week1.game.Model.Initializer;

import java.util.Arrays;

public class UnitLoader {

    public static final Model FIRE_MINION = null;
    public static Model WATER_MINION = null;
    public static Model EARTH_MINION = null;
    public static Model AIR_MINION = null;

    static {
        loadMinion(EARTH_MINION, "earth_minion");
        loadMinion(FIRE_MINION, "fire_minion");
        loadMinion(WATER_MINION, "water_minion");
        loadMinion(AIR_MINION, "ait_minion");

    }

    private static void loadMinion(Model minion, String folder_name) {
        String minion_name = "minion/" + folder_name + "/minion_with_weird_face.g3db";
        Initializer.assetManager.load(minion_name, Model.class);
        Initializer.assetManager.finishLoading();
        minion = Initializer.assetManager.get(minion_name, Model.class);

        // Adjust the model to fit nicely into the blocky world
        for (Node node : minion.nodes) {
            node.scale.set(0.5f,0.5f,0.5f);
            node.rotation.set(0,0,0.25f, 0);
        }
        minion.calculateTransforms();
    }


}
