package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.week1.game.FileUtil;
import com.week1.game.GameController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TowerUtils {

    public static List<TowerDetails> getCustomTowerList() {
        ArrayList<TowerDetails> customTowers = new ArrayList<>();


        File customTowerDir = Gdx.files.internal(GameController.PREFS.getString("saveDir") +"/eridanus/customTowers").file();

        //This would run if this is user's first run or if they just changed save location
        if (!customTowerDir.exists()) {
            customTowerDir.mkdir();
        }

        Gdx.app.log("TowerUtils", "" + customTowerDir.exists());

        File[] customTowerFiles = customTowerDir.listFiles();
        System.out.println("HOME: " + System.getProperty("user.home"));
        for (File f: customTowerFiles) {
            if (!f.getPath().equals(FileUtil.fixPath(GameController.PREFS.getString("saveDir")+"eridanus/customTowers/dummy.txt"))){
                customTowers.add(new TowerDetails(f.getPath()));
            }

        }

        return customTowers;
    }
}
