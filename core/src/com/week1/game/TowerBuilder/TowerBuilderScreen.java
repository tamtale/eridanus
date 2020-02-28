package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.week1.game.GameController;
import com.week1.game.GameScreen;

public class TowerBuilderScreen implements Screen {

    private GameController game;
    public TowerBuilderStage towerStage;
    TowerBuilderCamera towerCam;
    InputMultiplexer multiplexer;
    CameraInputController camController;

    public void setInputProc() {
        camController = new CameraInputController(towerCam.getCam());
        multiplexer = new InputMultiplexer();
        BuilderInputProcessor bip = new BuilderInputProcessor(this);

        multiplexer.addProcessor(towerStage.stage);
        multiplexer.addProcessor(bip);
        multiplexer.addProcessor(camController);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public TowerBuilderScreen(GameController game) {
        this.game = game;
        towerStage = new TowerBuilderStage(this);
        towerCam = new TowerBuilderCamera(this);

        towerCam.initPersCamera();
        setInputProc();
        towerCam.setCamController(camController);

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

    public void startGame() {
        Gdx.app.log("Tower Builder Screen skv2", "starting game");
        game.setScreen(new GameScreen(game.gameArgs));
    }


}
