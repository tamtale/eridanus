package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.week1.game.GameController;
import com.week1.game.GameScreen;

public class TowerBuilderScreen implements Screen {

    private GameController game;
    private TowerBuilderStage towerStage;
    private TowerBuilderCamera towerCam;
    private InputMultiplexer multiplexer;
    private CameraInputController camController;

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

    public void setCamTower(TowerDetails tower) {
        towerCam.setCurrTowerDetails(tower);
    }

    public String getTowerStats() {
        TowerDetails curr = towerCam.getCurrTowerDetails();
        return "HP: " + curr.getHp() +"\n Atk: " + curr.getAtk() + " \n Range: " + curr.getRange() + "\n Price: " + curr.getPrice();
    }

    //Builder Input Proc to stage methods
    public boolean isBuildMode(){
        return towerStage.isBuildMode;
    }

    public boolean isAddMode() {
        return towerStage.isBuildMode & towerStage.isAddMode;
    }

    public boolean isDelMode() {
        return towerStage.isDelMode;
    }



    //Builder Input proc to cam methods
    public void addBlock(int screenX, int screenY) {
        towerCam.addBlock(screenX, screenY, towerStage.getMaterialSelection());
    }

    public void deleteBlock(int screenX, int screenY) {
        towerCam.deleteBlock(screenX, screenY);
    }

    public void highlightBlock(int screenX, int screenY) {
        towerCam.highlightBlock(screenX, screenY);
    }

    public void highlightTowerBlock(int screenX, int screenY) {
        towerCam.highlightTowerBlock(screenX, screenY);
    }

    public void stopHighlighting() {
        towerCam.stopHighlighting();
    }

    public void displayBuildCore() {
        towerCam.setCurrTowerDetails(TowerPresets.getBuildingCore());
    }
}
