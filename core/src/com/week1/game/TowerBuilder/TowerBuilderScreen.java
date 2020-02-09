package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.week1.game.GameController;
import com.week1.game.GameScreen;

public class TowerBuilderScreen implements Screen {

    private GameController game;
    TowerBuilderStage towerStage;
    TowerBuilderCamera towerCam;

    public TowerBuilderScreen(GameController game) {
        this.game = game;
        towerStage = new TowerBuilderStage(this);
        towerCam = new TowerBuilderCamera(towerStage);
        towerStage.setTowerBuilder(towerCam);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //Delegate render to the camera (3d cam) and the stage (buttons and widgets)
        towerCam.render();
        towerStage.render();

    }

    @Override
    public void resize(int width, int height) {
        towerCam.resize(width, height);
        towerStage.stage.getViewport().update(width, height);

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
