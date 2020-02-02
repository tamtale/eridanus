package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.week1.game.GameController;
import com.week1.game.GameScreen;

public class TowerBuilderScreen implements Screen {

    private GameController game;
    TowerBuilderStage gameUI;
    TowerBuilderCamera builder;

    public TowerBuilderScreen(GameController game) {
        this.game = game;
        gameUI = new TowerBuilderStage(this);
        builder = new TowerBuilderCamera(gameUI);
        gameUI.setTowerBuilder(builder);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        builder.render();
        gameUI.render();

    }

    @Override
    public void resize(int width, int height) {

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

    }

    public void startGame() {
        Gdx.app.log("Tower Builder Screen skv2", "starting game");
        game.setScreen(new GameScreen(game.gameArgs));
    }

}
