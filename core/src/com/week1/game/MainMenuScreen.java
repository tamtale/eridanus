package com.week1.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.TowerBuilder.TowerBuilderScreen;

public class MainMenuScreen implements Screen {
    GameController game;
    Stage stage;

    //Widgets
    TextButton playButton;


    public MainMenuScreen(GameController game) {
        this.game = game;
        stage = new Stage(new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));
        setWidgets();
        configureWidgets();
        setListeners();

        Gdx.input.setInputProcessor(stage);

    }

    private void setWidgets() {
        playButton = new TextButton("play", new Skin(Gdx.files.internal("uiskin.json")));
    }

    private void configureWidgets() {
        playButton.setSize(128,64);
        playButton.setPosition(GameController.VIRTUAL_WIDTH/2 - playButton.getWidth()/2 - 10, 24);
        //Set the background image
        stage.addActor(new Image(new TextureRegionDrawable(new Texture("nova_menu.png"))));
        stage.addActor(playButton);

    }

    private void setListeners() {
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new TowerBuilderScreen(game));
            }
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
