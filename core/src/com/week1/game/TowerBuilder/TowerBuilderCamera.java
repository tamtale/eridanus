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
// top-down views of all towers
// Fill out ui -> need to assets, stage, etc classes
// tower building


public class TowerBuilderCamera {
    public PerspectiveCamera cam;
    public ModelBatch modelBatch;
    public Environment environment;
    public CameraInputController camController;
    public TowerPresets presets;
    AssetManager assets;
    public Array<ModelInstance> currTower;

    TowerBuilderStage gameUI;
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
        multiplexer.addProcessor(gameUI.stage);
        multiplexer.addProcessor(camController);
        Gdx.input.setInputProcessor(multiplexer);
    }


    public TowerBuilderCamera(TowerBuilderStage gameUI) {
        this.gameUI = gameUI;
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
            gameUI.sw.setLabelStyle(1);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            currTower = presets.getTower2();
            gameUI.sw.setLabelStyle(2);

        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
            currTower = presets.getTower3();
            gameUI.sw.setLabelStyle(3);

        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
            currTower = presets.getTower4();
            gameUI.sw.setLabelStyle(4);

        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_5)) {
            currTower = presets.getTower5();
            gameUI.sw.setLabelStyle(5);

        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_6)) {
            currTower = presets.getTower6();
            gameUI.sw.setLabelStyle(6);

        }


        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);


        modelBatch.begin(cam);

        if (space != null) {
            modelBatch.render(space);
        }
        modelBatch.render(currTower, environment);
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
