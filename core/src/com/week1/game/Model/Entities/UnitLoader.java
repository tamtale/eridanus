package com.week1.game.Model.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.week1.game.Model.Initializer;
import com.week1.game.Util3D;

import java.util.HashMap;
import java.util.Map;

public class UnitLoader {

    public static Map<String, UnitModel> NAMES_TO_FACTIONS = new HashMap<>();
    public static Map<String, Color> NAMES_TO_COLORS = new HashMap<>();

    public static UnitModel EARTH_MINION = loadMinion("earth_minion", "Earth faction", Color.GREEN);
    public static UnitModel FIRE_MINION = loadMinion("fire_minion", "Fire faction", Color.RED);
    public static UnitModel WATER_MINION = loadMinion("water_minion", "Water faction", new Color(0, 0, 0.545f, 1f));
    public static UnitModel AIR_MINION =loadMinion("air_minion", "Air faction", new Color( 0.678f, 0.847f, 0.902f, 1f));

    //These are the defaults for people's factions
    private static Model box = Util3D.ONLY.createBox(1, 1, 1, Color.GRAY);;
    public static UnitModel EMPTY_FACTIONLESS = new UnitModel(box, "Factionless", Color.GRAY);
    public static UnitModel EMPTY_SELECTFACTION = new UnitModel(box, "Select a Faction", Color.GRAY);

//    static {
//        unitTypes.put("Factionless", EMPTY_FACTIONLESS);
//        unitTypes.put("Select a Faction", EMPTY_SELECTFACTION);
//    }

    private static UnitModel loadMinion(String folder_name, String faction_name, Color color) {
        String minion_name = "minion/" + folder_name + "/minion_with_weird_face.g3db";
        Initializer.assetManager.load(minion_name, Model.class);
        Initializer.assetManager.finishLoading();
        UnitModel minion = new UnitModel(Initializer.assetManager.get(minion_name, Model.class), faction_name, color);

        // Adjust the model to fit nicely into the blocky world
        for (Node node : minion.getModel().nodes) {
            node.scale.set(0.5f,0.5f,0.5f);
            node.rotation.set(0,0,0.25f, 0);
        }
        minion.getModel().calculateTransforms();

        NAMES_TO_FACTIONS.put(faction_name, minion);
        NAMES_TO_COLORS.put(faction_name, color);

        return minion;

    }


}
