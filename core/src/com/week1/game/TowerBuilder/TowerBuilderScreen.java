package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Screen;
import com.week1.game.GameController;

public class TowerBuilderScreen implements Screen {

    private GameController game;
    TowerBuilderStage towerStage;
    TowerBuilderCamera towerCam;

    public TowerBuilderScreen(GameController game) {
        this.game = game;
        towerStage = new TowerBuilderStage(this, game);
        towerCam = new TowerBuilderCamera(towerStage);
        towerStage.setTowerBuilder(towerCam);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //Delegate render to the camera (3d cam) and the stage (buttons and widgets)
        towerStage.render();
        towerCam.render();

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
}
