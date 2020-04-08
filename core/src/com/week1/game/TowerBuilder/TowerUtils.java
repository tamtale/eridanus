package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.week1.game.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TowerUtils {

    public static List<TowerDetails> getCustomTowerList() {
        ArrayList<TowerDetails> customTowers = new ArrayList<>();
        if (true) return customTowers;

        File customTowerDir = Gdx.files.internal("customTowers").file();
        Gdx.app.log("TowerUtils", "" + customTowerDir.exists());
        File[] customTowerFiles = customTowerDir.listFiles();
        for (File f: customTowerFiles) {
            if (!f.getPath().equals(FileUtil.fixPath("customTowers/dummy.txt"))){
                customTowers.add(new TowerDetails(f.getPath()));
            }

        }

        return customTowers;
    }
}
