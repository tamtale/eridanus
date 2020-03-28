package com.week1.game.Settings;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Optional;

public class Settings {
    public static Optional<Settings> fromFile(String path) {
        Gson gson = new Gson();
        try {
            Settings settings = gson.fromJson(new FileReader(path), Settings.class);
            return Optional.of(settings);
        } catch (FileNotFoundException e) {
            return Optional.empty();
        }
    }

    private boolean edgePan;
    public boolean getEdgePan() {
        return edgePan;
    }

}
