package com.week1.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.week1.game.MenuScreens.MainMenuScreen;
import org.apache.commons.cli.CommandLine;

import java.io.File;

public class GameController implements ApplicationListener {

    Screen currScreen;
    public static Preferences PREFS;
    public static final float VIRTUAL_WIDTH = 800;
    public static final float VIRTUAL_HEIGHT = 800;
    private int logLevel = Gdx.app.LOG_INFO;

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

    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(logLevel);
        PREFS = Gdx.app.getPreferences("eridanusSavedContent");
        if (!PREFS.contains("saveDir")) {
            PREFS.putString("saveDir", System.getProperty("user.home"));
            PREFS.flush();
        }

        //Make the eridanus dir
        File eridanusDir = Gdx.files.internal(GameController.PREFS.getString("saveDir") +"/eridanus").file();
        if (!eridanusDir.exists()) {
            eridanusDir.mkdir();
        }
        returnToMainMenu();
    }

    public void returnToMainMenu() {
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
