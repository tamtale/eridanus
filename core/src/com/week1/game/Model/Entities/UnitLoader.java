package com.week1.game.Model.Entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.week1.game.Model.Initializer;

import java.util.Arrays;

public class UnitLoader {

    public static UnitModel EARTH_MINION = loadMinion("earth_minion", "Earth faction");
    public static UnitModel FIRE_MINION = loadMinion("fire_minion", "Fire faction");
    public static UnitModel WATER_MINION = loadMinion("water_minion", "Water faction");
    public static UnitModel AIR_MINION =loadMinion("air_minion", "Air faction");


    private static UnitModel loadMinion(String folder_name, String faction_name) {
        String minion_name = "minion/" + folder_name + "/minion_with_weird_face.g3db";
        Initializer.assetManager.load(minion_name, Model.class);
        Initializer.assetManager.finishLoading();
        UnitModel minion = new UnitModel(Initializer.assetManager.get(minion_name, Model.class), faction_name);

        // Adjust the model to fit nicely into the blocky world
        for (Node node : minion.getModel().nodes) {
            node.scale.set(0.5f,0.5f,0.5f);
            node.rotation.set(0,0,0.25f, 0);
        }
        minion.getModel().calculateTransforms();
        return minion;

    }


}
