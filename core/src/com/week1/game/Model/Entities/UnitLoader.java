package com.week1.game.Model.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.Initializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnitLoader {

    public static Map<String, UnitModel> NAMES_TO_FACTIONS = new HashMap<>();
    public static Map<String, Color> NAMES_TO_COLORS = new HashMap<>();
    public static Array<String> FACTIONS = new Array<>();

    static{
        FACTIONS.add("Select A Faction");
        loadMinion("greenCube/new_cube_minion.g3db", "Green", Color.GREEN);
        loadMinion("redCube/new_cube_minion.g3db", "Red", Color.RED);
        loadMinion("blueCube/new_cube_minion.g3db", "Blue", new Color(0, 0, 0.545f, 1f));
        loadMinion("whiteCube/new_cube_minion.g3db", "White", new Color( 0.678f, 0.847f, 0.902f, 1f));
    }


    private static UnitModel loadMinion(String filepath, String faction_name, Color color) {
        String minion_name = "cube_minion/" + filepath;
        Initializer.assetManager.load(minion_name, Model.class);
        Initializer.assetManager.finishLoading();
        UnitModel minion = new UnitModel(Initializer.assetManager.get(minion_name, Model.class), faction_name);

        // Adjust the model to fit nicely into the blocky world
        for (Node node : minion.getModel().nodes) {
//            node.scale.set(0.5f,0.5f,0.5f);
            node.scale.set(0.40f, 0.40f, 0.40f);
            node.rotation.set(Vector3.Z, (float) Math.toRadians(90.0));
//            node.rotation.set(0,0f,0f, 0);
        }
        minion.getModel().calculateTransforms();

        FACTIONS.add(faction_name);
        NAMES_TO_FACTIONS.put(faction_name, minion);
        NAMES_TO_COLORS.put(faction_name, color);

        return minion;

    }


}
