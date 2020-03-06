package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.week1.game.GameController;

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
        multiplexer.addProcessor(towerStage.dialogStage);
        multiplexer.addProcessor(bip);
        multiplexer.addProcessor(camController);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public TowerBuilderScreen(GameController game) {
        this.game = game;
        towerStage = new TowerBuilderStage(game, this);
        towerCam = new TowerBuilderCamera(this);

        towerCam.initPersCamera();
        setInputProc();
        towerCam.setCamController(camController);

        //Initialization in the TowerStage that depends on TowerCam
        towerStage.completeCamDependentInit();
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //Delegate render to the camera (3d cam) and the stage (buttons and widgets)
        towerStage.render();
        towerCam.render();
        towerStage.renderDialogs();

    }

    @Override
    public void resize(int width, int height) {
        towerCam.resize(width, height);
        towerStage.stage.getViewport().update(width, height);
        towerStage.dialogStage.getViewport().update(width, height);

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

    public void setCamTower(TowerDetails tower) {
        towerCam.setCurrTowerDetails(tower);
    }

    public String getTowerStats() {
        TowerDetails curr;
        if (towerStage.isBuildMode) {
            curr = towerCam.getWIPTower();
        } else {
            curr = towerCam.getCurrTowerDetails();
        }
        return "HP: " + curr.getHp() +"\n Atk: " + curr.getAtk() + " \n Range: " + curr.getRange() + "\n Price: " + curr.getPrice();
    }

    public void updateTowerStats() {
        towerStage.updateStats(getTowerStats());
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
        towerCam.setCurrTowerDetails(TowerPresets.getBuildCore());
    }


    public void saveTower(String twrName) {
        System.out.println(twrName);
        towerCam.WIPTower.setName(twrName);
        towerCam.WIPTower.saveTower();
        towerStage.addTowertoSelections(towerCam.WIPTower);
    }

    public void showErrorDialog(String msg) {
        towerStage.showDialog(msg);
    }

}
