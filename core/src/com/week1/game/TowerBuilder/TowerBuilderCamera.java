package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
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


//Next things
// Programmatic generation of tower stats for gameplay--
    //top down view (static of all presets)
    //tower footprint on ground -> 5 x 5,  and in all dimension
// Fill out ui -> need to assets, stage, etc classes
// tower building


public class TowerBuilderCamera {
    public PerspectiveCamera cam;
    public ModelBatch modelBatch;
    public Environment environment;
    public CameraInputController camController;
    public TowerPresets presets;
    AssetManager assets;
    private TowerDetails currTowerDetails;


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
        cam.near = 0.5f;
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


        currTowerDetails = presets.getTower(1);

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


        modelBatch.begin(cam);

        //Render the space background
//        if (space != null) {
//            modelBatch.render(space);
//        }

        modelBatch.render(currTowerDetails.getModel(), environment);
        modelBatch.end();


    }

    public void pause() {

    }

    public void resume() {

    }

    public void dispose() {

    }

    public void setCurrTowerDetails(TowerDetails currTowerDetails) {
        this.currTowerDetails = currTowerDetails;
    }

    public TowerDetails getCurrTowerDetails() {
        return this.currTowerDetails;
    }
}
