package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;


import java.util.ArrayList;
import java.util.List;


//Next things
// Programmatic generation of tower stats for gameplay--
    //top down view (static of all presets)
    //tower footprint on ground -> 5 x 5,  and in all dimension
// Fill out ui -> need to assets, stage, etc classes
// tower building


public class TowerBuilderCamera {

    private PerspectiveCamera cam;
    public ModelBatch modelBatch;
    public Environment environment;
    public TowerPresets presets;
    AssetManager assets;
    private TowerDetails currTowerDetails;
    private Array<ModelInstance> instances;
    private Vector3 position = new Vector3();
    private CameraInputController camController;


//    TowerBuilderStage towerStage;
    TowerBuilderScreen towerScreen;
    ModelInstance space;
    Boolean loading;
    private List<ThreeD> poses;

    public PerspectiveCamera getCam() {
        return cam;
    }

    public void setCamController(CameraInputController camController) {
        this.camController = camController;
    }


    //keep a 3d internal representation of the grid and store whether each block is
    public static class ThreeD{
        public int x;
        public int y;
        public int z;

        public ThreeD(int x, int y, int z) {
            this.x = x;
            this.z = z;
            this.y = y;
        }

        @Override
        public String toString() {

            return new String(Integer.toString(x) + " " + y + " " + z);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ThreeD) {
                if((((ThreeD) obj).x == x) & (((ThreeD) obj).y == y) & (((ThreeD) obj).z == z) ) {
                    return true;
                }
                return false;
            }
            return false;
        }

    }

    private void calcInvisiBlox() {
        List<ThreeD> poses = new ArrayList<>();
        List<ThreeD> blox = new ArrayList<>();
        for (BlockSpec b: currTowerDetails.getLayout()) {
            ThreeD curpos = new ThreeD(b.getX(), b.getY(), b.getZ());
//            poses.add();
            blox.add(curpos);
        }

        for (ThreeD p : blox) {

            for (int i = -1; i < 2; i += 2) {
                ThreeD nbr = new ThreeD(p.x + i, p.y , p.z);
                if (nbr.x <3 & nbr.x > -3 & !blox.contains(nbr) & !poses.contains(nbr)) {
                    poses.add(nbr);
                }

                nbr = new ThreeD(p.x , p.y + i, p.z);
                if (nbr.y > -1 & nbr.y < 8 & !blox.contains(nbr) & !poses.contains(nbr)) {
                    poses.add(nbr);
                }

                nbr = new ThreeD(p.x , p.y , p.z + i);
                if (nbr.z > -3 & nbr.z < 3 & !blox.contains(nbr)& !poses.contains(nbr)) {
                    poses.add(nbr);
                }
            }
        }
        this.poses = poses;

    }

    private void initEnvironment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

    }

    public void initPersCamera() {
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(20f, 20f, 20f);
        cam.lookAt(0,0,0);
        cam.near = 0.5f;
        cam.far = 300f;
        cam.update();
    }

    private void initModelBatch() {
        modelBatch = new ModelBatch();
    }




    public TowerBuilderCamera(TowerBuilderScreen towerScreen) {
        this.towerScreen = towerScreen;
        initEnvironment();
        initModelBatch();
        initPersCamera();

        presets = new TowerPresets();

        assets = new AssetManager();
        assets.load("spacesphere.obj", Model.class);
        loading = true;


        currTowerDetails = presets.getTower(1);
        instances = currTowerDetails.getModel();
        calcInvisiBlox();

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
//        if (loading && assets.update())
//            doneLoading();

        camController.update();


        modelBatch.begin(cam);
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
        this.instances = currTowerDetails.getModel();
    }

    public TowerDetails getCurrTowerDetails() {
        return this.currTowerDetails;
    }

    //Some way to register clicks

    public int getObject (int screenX, int screenY) {
        Ray ray = cam.getPickRay(screenX, screenY);

        int result = -1;
        float distance = -1;

        for (int i = 0; i < poses.size(); i++) {
            ThreeD curblock = poses.get(i);
            int x = curblock.x;
            int y = curblock.y;
            int z = curblock.z;
            position = new Vector3(x * 5f, y * 5f, z * 5f);
            Vector3 dimensions = new Vector3(5,5,5);


            float dist2 = ray.origin.dst2(position);
            if (distance >= 0f && dist2 > distance)
                continue;

            if (Intersector.intersectRaySphere(ray, position, dimensions.len()/2f, null)) {
                result = i;
                distance = dist2;
            }
        }

        System.out.println("poses size: " + poses.size() + " result: " + result);
        if (result > -1) {
            System.out.println(poses.get(result));
            ModelInstance newbloc = new ModelInstance(TowerMaterials.modelMap.get(1));
            newbloc.transform.setToTranslation(poses.get(result).x * 5f, poses.get(result).y * 5f, poses.get(result).z * 5f);
            instances.add(newbloc);
        }
//        System.out.println(instances.get(result).transform.getValues());
        return result;
    }

}
