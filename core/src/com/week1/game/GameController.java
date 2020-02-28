package com.week1.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

public class GameController implements ApplicationListener {
    Screen currScreen;
    public static final float VIRTUAL_WIDTH = 800;
    public static final float VIRTUAL_HEIGHT = 800;

    public GameController() {
    }

    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
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
            System.out.println("Bad Error: Tried to set null screen");
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
