package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.utils.Array;

// Need a class, or something, to convert a tower layout into stats

//Next things
// Color scheme
// Programmatic generation of tower stats for gameplay--
    //top down view (static of all presets)
    //generation of tower stats
    //tower footprint on ground and in all dimension
// Fill out ui -> need to assets, stage, etc classes
// tower building


public class TowerBuilderCamera {
    public PerspectiveCamera cam;
    public ModelBatch modelBatch;
    public Environment environment;
    public CameraInputController camController;
    public TowerPresets presets;
    AssetManager assets;
    public Tower currTower;


    TowerBuilderStage towerStage;
    ModelInstance space;
    Boolean loading;


    private void initEnvironment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

    }

    private void initPersCamera() {
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
    }

    private void initModelBatch() {
        modelBatch = new ModelBatch();
    }

    private void setInputProc() {
        //Init the camera
        camController = new CameraInputController(cam);
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(towerStage.stage);
        multiplexer.addProcessor(camController);
        Gdx.input.setInputProcessor(multiplexer);
    }


    public TowerBuilderCamera(TowerBuilderStage towerStage) {
        this.towerStage = towerStage;
        initEnvironment();
        initModelBatch();
        initPersCamera();
        setInputProc();

        presets = new TowerPresets();

        assets = new AssetManager();
        assets.load("spacesphere.obj", Model.class);
        loading = true;


        currTower = presets.getTower1();
    }


    public void resize(int width, int height) {
        cam.viewportHeight = height;
        cam.viewportWidth = width;
    }

    private void doneLoading() {
        space = new ModelInstance(assets.get("spacesphere.obj", Model.class));
        loading = false;
    }

    public void render() {
        if (loading && assets.update())
            doneLoading();

        camController.update();

        //Number keys toggle presests
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            currTower = presets.getTower1();
//            towerStage.sw.setLabelStyle(1);
            towerStage.sw.setLblTxt(currTower.getHp(), currTower.getAtk(), currTower.getRange());
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            currTower = presets.getTower2();
//            towerStage.sw.setLabelStyle(2);
            towerStage.sw.setLblTxt(currTower.getHp(), currTower.getAtk(), currTower.getRange());

        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
            currTower = presets.getTower3();
//            towerStage.sw.setLabelStyle(3);
            towerStage.sw.setLblTxt(currTower.getHp(), currTower.getAtk(), currTower.getRange());
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
            currTower = presets.getTower4();
//            towerStage.sw.setLabelStyle(4);
            towerStage.sw.setLblTxt(currTower.getHp(), currTower.getAtk(), currTower.getRange());
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_5)) {
            currTower = presets.getTower5();
//            towerStage.sw.setLabelStyle(5);
            towerStage.sw.setLblTxt(currTower.getHp(), currTower.getAtk(), currTower.getRange());
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_6)) {
            currTower = presets.getTower6();
//            towerStage.sw.setLabelStyle(6);
            towerStage.sw.setLblTxt(currTower.getHp(), currTower.getAtk(), currTower.getRange());
        }


        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);


        modelBatch.begin(cam);

        if (space != null) {
            modelBatch.render(space);
        }
        modelBatch.render(currTower.getModel(), environment);
        modelBatch.end();

//        gameUI.stage.draw();

    }

    public void pause() {

    }

    public void resume() {

    }

    public void dispose() {

    }
}
