package com.week1.game.TowerBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TowerUtils {

    public static List<TowerDetails> getCustomTowerList() {
        ArrayList<TowerDetails> customTowers = new ArrayList<>();

        File customTowerDir = new File("customTowers");
        File[] customTowerFiles = customTowerDir.listFiles();
        for (File f: customTowerFiles) {
            if (!f.getPath().equals("customTowers\\dummy.txt")){
                customTowers.add(new TowerDetails(f.getPath()));
            }

        }

        return customTowers;
    }
}
