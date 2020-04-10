package com.week1.game.Settings;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;

import java.util.Optional;

public class Settings {
    public static Settings DEFAULT = new Settings() {{setEdgePan(false);}};
    public static Optional<Settings> fromFile(String path) {
        Gson gson = new Gson();
        try {
            Settings settings = gson.fromJson(Gdx.files.internal(path).readString(), Settings.class);
            return Optional.of(settings);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private boolean edgePan;
    public boolean getEdgePan() {
        return edgePan;
    }

    public void setEdgePan(boolean edgePan) {
        this.edgePan = edgePan;
    }

}
