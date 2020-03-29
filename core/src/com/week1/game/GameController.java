package com.week1.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.week1.game.MenuScreens.MainMenuScreen;
import com.week1.game.Settings.Settings;
import org.apache.commons.cli.*;

public class GameController implements ApplicationListener {

    Screen currScreen;
    public static final float VIRTUAL_WIDTH = 800;
    public static final float VIRTUAL_HEIGHT = 800;
    private int logLevel = Gdx.app.LOG_INFO;
    private static Settings settings;
    public static Settings getSettings() {
        return settings;
    }

    public GameController(CommandLine commandLine) {
        // Set the logging level (by default, it'll be info).
        String logArg = commandLine.getOptionValue("log", "i");
        switch (logArg) {
            case "e":
                logLevel = Gdx.app.LOG_ERROR;
                break;
            case "d":
                logLevel = Gdx.app.LOG_DEBUG;
                break;
        }

        String configPath = commandLine.getOptionValue("config");
        if (configPath == null) configPath = "config/default.json";
        Settings.fromFile(configPath).ifPresent(settings -> {
            this.settings = settings;
        });

    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(logLevel);
        setScreen(new MainMenuScreen(this));
    }

    public Screen getScreen() {
        return this.currScreen;
    }
    public void setScreen(Screen screen) {
        if (this.currScreen != null) {
            this.currScreen.hide();
            this.currScreen.dispose();
        }

        this.currScreen = screen;

        //This is just for safety
        if (this.currScreen != null) {
            this.currScreen.show();
            this.currScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } else {
            Gdx.app.error("GameController", "Bad Error: Tried to set null screen");
        }
    }

    @Override
    public void resize(int width, int height) {
        this.currScreen.resize(width, height);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        currScreen.render(0);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
