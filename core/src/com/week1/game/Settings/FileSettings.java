package com.week1.game.Settings;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Optional;

public class FileSettings implements ISettings{
    public static Optional<FileSettings> fromFile(String path) {
        Gson gson = new Gson();
        try {
            FileSettings settings = gson.fromJson(new FileReader(path), FileSettings.class);
            return Optional.of(settings);
        } catch (FileNotFoundException e) {
            return Optional.empty();
        }
    }

    private boolean edgePan;
    @Override
    public boolean edgePan() {
        return edgePan;
    }

}
