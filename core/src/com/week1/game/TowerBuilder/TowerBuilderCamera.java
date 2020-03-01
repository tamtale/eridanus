package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.*;
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
    private ModelInstance highlightedAddBlock = null;
    private ModelInstance highlightedTowerBlock = null;


    TowerBuilderScreen towerScreen;
    Boolean loading;
    private List<Vector3> invisiPoses = new ArrayList<>();
    private List<Vector3> poses = new ArrayList<>();

    public PerspectiveCamera getCam() {
        return cam;
    }

    public void setCamController(CameraInputController camController) {
        this.camController = camController;
    }

    public void setCurrTowerDetails(TowerDetails currTowerDetails) {
        this.currTowerDetails = currTowerDetails;
        this.instances = currTowerDetails.getModel();
        this.calcInvisiBlox();
    }

    public TowerDetails getCurrTowerDetails() {
        return this.currTowerDetails;
    }

    private void initEnvironment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

    }

    public void initPersCamera() {
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(20f, 20f, 20f);
        cam.lookAt(0, 0, 0);
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


    public void render() {

        Gdx.gl.glClear(GL20.GL_ALPHA_BITS);
        camController.update();


        modelBatch.begin(cam);
        modelBatch.render(currTowerDetails.getModel(), environment);
        if (highlightedAddBlock != null) {
            modelBatch.render(highlightedAddBlock, environment);
        }

        if (highlightedTowerBlock != null) {
            modelBatch.render(highlightedTowerBlock);
        }

        modelBatch.end();


    }

    public void pause() {

    }

    public void resume() {

    }

    public void dispose() {

    }


    // ------------------------------------
    //Tower Editor methods

    private void calcInvisiBlox() {

        for (BlockSpec b : currTowerDetails.getLayout()) {
            Vector3 curpos = new Vector3(b.getX(), b.getY(), b.getZ());
            poses.add(curpos);
        }

        for (Vector3 p : poses) {

            for (int i = -1; i < 2; i += 2) {
                Vector3 nbr = new Vector3(p.x + i, p.y, p.z);
                if (nbr.x < 3 & nbr.x > -3 & !poses.contains(nbr) & !invisiPoses.contains(nbr)) {
                    invisiPoses.add(nbr);
                }

                nbr = new Vector3(p.x, p.y + i, p.z);
                if (nbr.y > -1 & nbr.y < 8 & !poses.contains(nbr) & !invisiPoses.contains(nbr)) {
                    invisiPoses.add(nbr);
                }

                nbr = new Vector3(p.x, p.y, p.z + i);
                if (nbr.z > -3 & nbr.z < 3 & !poses.contains(nbr) & !invisiPoses.contains(nbr)) {
                    invisiPoses.add(nbr);
                }
            }
        }

//        Gdx.app.log("skv2", "Recalculated inbisiblocks: \n"+ invisiPoses.toString());

    }

    private int selectBlockfromArray(int screenX, int screenY, List<Vector3> blocks) {
        Ray ray = cam.getPickRay(screenX, screenY);

        int result = -1;
        float distance = -1;

        for (int i = 0; i < blocks.size(); i++) {
            Vector3 curblock = blocks.get(i);

            position = new Vector3(curblock.x * 5f, curblock.y * 5f, curblock.z * 5f);
            Vector3 dimensions = new Vector3(5, 5, 5);


            float dist2 = ray.origin.dst2(position);
            if (distance >= 0f && dist2 > distance)
                continue;

            if (Intersector.intersectRaySphere(ray, position, dimensions.len() / 2f, null)) {
                result = i;
                distance = dist2;
            }
        }
        return result;
    }


    private int selectInvisiblock(int screenX, int screenY) {
        return selectBlockfromArray(screenX, screenY, invisiPoses);
    }

    private int selectBlock(int screenX, int screenY) {
        return selectBlockfromArray(screenX, screenY, poses);
    }

    public void addBlock(int screenX, int screenY, String materialSelection) {
        highlightedAddBlock = null;
        int result = selectInvisiblock(screenX, screenY);

        if (result > -1) {
//            System.out.println(invisiPoses.get(result));
            ModelInstance newbloc = new ModelInstance(TowerMaterials.modelMap.get(TowerMaterials.materialCodes.get(materialSelection)));
            newbloc.transform.setToTranslation(invisiPoses.get(result).x * 5f, invisiPoses.get(result).y * 5f, invisiPoses.get(result).z * 5f);
            //TODO -- this changes the preset permanently. need to fix that
            instances.add(newbloc);

            //update invisiblox
            updateInvisibloxOnAdd(invisiPoses.get(result));
            System.out.println("Added a block");
            highlightBlock(screenX, screenY);
        } else {
            System.out.println("did not add a block");
        }


    }

    private void updateInvisibloxOnAdd(Vector3 addedBlock) {
        this.invisiPoses.remove(addedBlock);
        this.poses.add(addedBlock);
        float oldX = addedBlock.x;
        float oldY = addedBlock.y;
        float oldZ = addedBlock.z;
        for (int i = -1; i < 2; i += 2) {
            Vector3 newX = new Vector3(oldX + i, oldY, oldZ);
            if (!this.invisiPoses.contains(newX) & !this.poses.contains(newX) & oldX + i < 3 & oldX + i > -3) {
                this.invisiPoses.add(newX);
            }

            Vector3 newY = new Vector3(oldX, oldY + i, oldZ);
            if (!this.invisiPoses.contains(newY) & !this.poses.contains(newY) & oldY + i < 8 & oldY + i > 0) {
                this.invisiPoses.add(newY);
            }

            Vector3 newZ = new Vector3(oldX, oldY, oldZ + i);
            if (!this.invisiPoses.contains(newZ) & !this.poses.contains(newZ) & oldZ + i < 3 & oldZ + i > -3) {
                this.invisiPoses.add(newZ);
            }
        }

//        System.out.println(this.invisiPoses);

    }

    public void deleteBlock(int screenX, int screenY) {
        int result = selectBlock(screenX, screenY);
        if (result != -1) {
            Vector3 selectedPos = poses.get(result);
            for (int i = 0; i < this.instances.size; i++) {
                ModelInstance block = this.instances.get(i);

                Vector3 translation = new Vector3();
                block.transform.getTranslation(translation);
                if (translation.x == selectedPos.x * 5f & translation.y == selectedPos.y * 5f & translation.z == selectedPos.z * 5f) {

                    this.instances.removeValue(block, true);
                    updateInvisibloxOnDel(selectedPos);


                    break;
                }
            }

            highlightTowerBlock(screenX, screenY);
        }

    }

    private boolean isNbr(Vector3 pos1, Vector3 pos2) {
        if (pos1.x == pos2.x & pos1.y == pos2.y) {
            if (Math.abs(pos1.z - pos2.z) == 1) {
                return true;
            }
        } else if (pos1.y == pos2.y & pos1.z == pos2.z) {
            if (Math.abs(pos1.x - pos2.x) == 1) {
                return true;
            }
        } else if (pos1.x == pos2.x & pos1.z == pos2.z) {
            if (Math.abs(pos1.y - pos2.y) == 1) {
                return true;
            }
        }
        return false;
    }


    private void updateInvisibloxOnDel(Vector3 selectedPos) {
        poses.remove(selectedPos);
        invisiPoses.add(selectedPos);

        List<Vector3> nbrs = new ArrayList<>();
        for (int i = 0; i < invisiPoses.size(); i ++) {
            if (isNbr(selectedPos, invisiPoses.get(i))) {
                nbrs.add(invisiPoses.get(i));
            }
        }

        List<Integer> nbrRemovals = new ArrayList<>();
        for (int i = 0; i < nbrs.size(); i++) {
            for (int j = 0; j < invisiPoses.size(); j++) {
                if (isNbr(nbrs.get(i), invisiPoses.get(j))) {
                    nbrRemovals.add(i);
                    break;
                }
            }
        }

        for (Integer idx: nbrRemovals) {
            nbrs.remove(idx);
        }

        for (Vector3 pos: nbrs) {
            invisiPoses.remove(pos);
        }

    }

    public void highlightBlock(int screenX, int screenY) {
        int result = selectInvisiblock(screenX, screenY);
        if (result != -1) {
            ModelInstance newbloc = new ModelInstance(TowerMaterials.modelMap.get(0));
            newbloc.transform.setToTranslation(invisiPoses.get(result).x * 5f, invisiPoses.get(result).y * 5f, invisiPoses.get(result).z * 5f);
            highlightedAddBlock = newbloc;

        } else {
            highlightedAddBlock = null;
        }
    }

    public void highlightTowerBlock(int screenX, int screenY) {
        int result = selectBlock(screenX, screenY);
        if (result != -1) {
            Vector3 selectedPos = poses.get(result);
            for (int i = 0; i < this.instances.size; i++) {
                ModelInstance block = this.instances.get(i);

                Vector3 translation = new Vector3();
                block.transform.getTranslation(translation);
                if (translation.x == selectedPos.x * 5f & translation.y == selectedPos.y * 5f & translation.z == selectedPos.z * 5f) {

                    ModelInstance newbloc = new ModelInstance(TowerMaterials.modelMap.get(0));
                    newbloc.transform.setToTranslation(translation);
                    highlightedAddBlock = newbloc;

                    break;
                }
            }
        } else {
            highlightedTowerBlock = null;
        }


    }


    public void stopHighlighting() {
        highlightedAddBlock = null;
        highlightedTowerBlock = null;
        return;
    }
}
