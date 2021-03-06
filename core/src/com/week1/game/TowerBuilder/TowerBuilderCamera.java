package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.GameController;


import java.util.ArrayList;
import java.util.List;


public class TowerBuilderCamera {

    private PerspectiveCamera cam;
    public ModelBatch modelBatch;
    public Environment environment;
    private CameraInputController camController;
    private ModelInstance highlightedAddBlock = null;
    private ModelInstance highlightedTowerBlock = null;
    private FitViewport viewport;

    private TowerDetails currTowerDetails;
    public TowerDetails WIPTower;
    TowerBuilderScreen towerScreen;
    private List<Vector3> invisiBlocks = new ArrayList<>();
    private List<Vector3> towerBlocks = new ArrayList<>();

    private float BLOCKLENGTH = TowerMaterials.BLOCKLENGTH;

    public PerspectiveCamera getCam() {
        return cam;
    }

    public void setCamController(CameraInputController camController) {
        this.camController = camController;
    }

    public void setWIPTower(boolean useBuildCore) {
        if (useBuildCore) {
            this.WIPTower = TowerPresets.getBuildCore();
        } else {
            //a copy of the current tower
            this.WIPTower = TowerDetails.copy(currTowerDetails);
        }
        calcNewInvisiBlox();
    }

    public void setCurrTowerDetails(TowerDetails selectedTower) {
        this.currTowerDetails = selectedTower;
    }

    public TowerDetails getWIPTower() {
        return this.WIPTower;
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

        viewport = new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT, cam);
    }

    private void initModelBatch() {
        modelBatch = new ModelBatch();
    }


    public TowerBuilderCamera(TowerBuilderScreen towerScreen) {
        this.towerScreen = towerScreen;
        initEnvironment();
        initModelBatch();
        initPersCamera();

        setCurrTowerDetails(TowerPresets.getTower(1));

    }


    public void resize(int width, int height) {
        viewport.update(width, height);
    }


    public void render() {

        Gdx.gl.glClear(GL20.GL_ALPHA_BITS);
        camController.update();

        modelBatch.begin(cam);
        modelBatch.render(TowerPresets.highlightGround.getModel());

        if (towerScreen.isBuildMode()) {
            modelBatch.render(WIPTower.getModel(), environment);
        } else {
            modelBatch.render(currTowerDetails.getModel(), environment);
        }

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
        invisiBlocks.clear();

        for (int j = 0; j < towerBlocks.size(); j++) {
            Vector3 p = towerBlocks.get(j);

            for (int i = -1; i < 2; i += 2) {
                Vector3 nbr = new Vector3(p.x + i, p.y, p.z);
                if (nbr.x < 3 & nbr.x > -3 & !towerBlocks.contains(nbr) & !invisiBlocks.contains(nbr)) {
                    invisiBlocks.add(nbr);
                }

                nbr = new Vector3(p.x, p.y + i, p.z);
                if (nbr.y > -1 & nbr.y < 8 & !towerBlocks.contains(nbr) & !invisiBlocks.contains(nbr)) {
                    invisiBlocks.add(nbr);
                }

                nbr = new Vector3(p.x, p.y, p.z + i);
                if (nbr.z > -3 & nbr.z < 3 & !towerBlocks.contains(nbr) & !invisiBlocks.contains(nbr)) {
                    invisiBlocks.add(nbr);
                }
            }
        }


    }

    private void calcNewInvisiBlox() {

        towerBlocks.clear();


        for (int i = 0; i < WIPTower.getLayout().size(); i++) {
            BlockSpec b = WIPTower.getLayout().get(i);
            Vector3 curpos = new Vector3(b.getX(), b.getY(), b.getZ());
            towerBlocks.add(curpos);
        }

        calcInvisiBlox();
    }

    private int selectBlockfromArray(int screenX, int screenY, List<Vector3> blocks) {
        Ray ray = cam.getPickRay(screenX, screenY);

        int result = -1;
        float distance = -1;

        for (int i = 0; i < blocks.size(); i++) {
            Vector3 curblock = blocks.get(i);


            Vector3 position = new Vector3(curblock.x * BLOCKLENGTH, curblock.y * BLOCKLENGTH, curblock.z * BLOCKLENGTH);
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
        return selectBlockfromArray(screenX, screenY, invisiBlocks);
    }

    private int selectBlock(int screenX, int screenY) {
        return selectBlockfromArray(screenX, screenY, towerBlocks);
    }

    public void addBlock(int screenX, int screenY, String materialSelection) {
        highlightedAddBlock = null;
        int result = selectInvisiblock(screenX, screenY);

        if (result > -1) {


            WIPTower.addBlock(new BlockSpec(TowerMaterials.materialCodes.get(materialSelection),
                    (int) invisiBlocks.get(result).x, (int) invisiBlocks.get(result).y, (int) invisiBlocks.get(result).z));
            towerScreen.updateTowerStats();

            //update invisiblox
            updateInvisibloxOnAdd(invisiBlocks.get(result));
//            Gdx.app.log("skv2", "Tower editor added a block");
            highlightBlock(screenX, screenY);
        }
//        else {
//            Gdx.app.log("skv2", "Tower editor did not add a block");
//        }


    }

    private void updateInvisibloxOnAdd(Vector3 addedBlock) {
        this.invisiBlocks.remove(addedBlock);
        this.towerBlocks.add(addedBlock);
        float oldX = addedBlock.x;
        float oldY = addedBlock.y;
        float oldZ = addedBlock.z;
        for (int i = -1; i < 2; i += 2) {
            Vector3 newX = new Vector3(oldX + i, oldY, oldZ);
            if (!this.invisiBlocks.contains(newX) & !this.towerBlocks.contains(newX) & oldX + i < 3 & oldX + i > -3) {
                this.invisiBlocks.add(newX);
            }

            Vector3 newY = new Vector3(oldX, oldY + i, oldZ);
            if (!this.invisiBlocks.contains(newY) & !this.towerBlocks.contains(newY) & oldY + i < 8 & oldY + i > 0) {
                this.invisiBlocks.add(newY);
            }

            Vector3 newZ = new Vector3(oldX, oldY, oldZ + i);
            if (!this.invisiBlocks.contains(newZ) & !this.towerBlocks.contains(newZ) & oldZ + i < 3 & oldZ + i > -3) {
                this.invisiBlocks.add(newZ);
            }
        }

    }

    public void deleteBlock(int screenX, int screenY) {
        if (towerBlocks.size() == 1) {
            //Don't remove the last block
            return;
        }

        int result = selectBlock(screenX, screenY);
        if (result != -1) {
            Vector3 selectedPos = towerBlocks.get(result);

            boolean removed = WIPTower.removeBlock((int) selectedPos.x, (int) selectedPos.y, (int) selectedPos.z);
            if (!removed) {
                towerScreen.showErrorDialog("Couldn't remove selected block because it etiher disconnects the tower from itself or the ground");
                return;
            }

            towerScreen.updateTowerStats();
            updateInvisibloxOnDel(selectedPos);

            highlightTowerBlock(screenX, screenY);
        }

    }



    private void updateInvisibloxOnDel(Vector3 selectedPos) {
        //easier and not that much slower just to recalc

        towerBlocks.remove(selectedPos);

        calcInvisiBlox();

    }

    public void highlightBlock(int screenX, int screenY) {
        int result = selectInvisiblock(screenX, screenY);
        if (result != -1) {
            ModelInstance newbloc = new ModelInstance(TowerMaterials.modelMap.get(BlockType.HIGHLIGHT));
            newbloc.transform.setToTranslation(invisiBlocks.get(result).x * BLOCKLENGTH,
                    invisiBlocks.get(result).y * BLOCKLENGTH, invisiBlocks.get(result).z * BLOCKLENGTH);
            highlightedAddBlock = newbloc;

        } else {
            highlightedAddBlock = null;
        }
    }

    public void highlightTowerBlock(int screenX, int screenY) {
        int result = selectBlock(screenX, screenY);
        if (result != -1) {
            Vector3 selectedPos = towerBlocks.get(result);
            for (int i = 0; i < WIPTower.getModel().size; i++) {
                ModelInstance block = WIPTower.getModel().get(i);

                Vector3 translation = new Vector3();
                block.transform.getTranslation(translation);
                if (translation.x == selectedPos.x * BLOCKLENGTH & translation.y == selectedPos.y * BLOCKLENGTH &
                        translation.z == selectedPos.z * BLOCKLENGTH) {

                    ModelInstance newbloc = new ModelInstance(TowerMaterials.modelMap.get(BlockType.HIGHLIGHT));
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
    }


}
