package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.Entities.TowerType;
import com.week1.game.Model.Entities.Unit;
import com.week1.game.Networking.Messages.Game.MoveMinionMessage;
import com.week1.game.Networking.Messages.Game.CreateMinionMessage;
import com.week1.game.Networking.Messages.Game.CreateTowerMessage;
import com.week1.game.Renderer.TextureUtils;

import java.util.HashMap;
import java.util.Map;

public class ClickOracle extends InputAdapter {

    private static final String TAG = "ClickOracle";

    private IClickOracleToRendererAdapter rendererAdapter;
    private IClickOracleToEngineAdapter engineAdapter;
    private IClickOracleToNetworkAdapter networkAdapter;

    private Vector3 touchPos = new Vector3();
    private Array<Unit> multiSelected = new Array<>();
    private Ray pickedRay = new Ray();

    private Vector3 selectionLocationStart = new Vector3();
    private Vector3 selectionLocationEnd = new Vector3();
    private boolean dragging = false;
    private Map<Integer, Direction> keycodeToDirection = new HashMap<>();
    {
        keycodeToDirection.put(Input.Keys.UP, Direction.UP);
        keycodeToDirection.put(Input.Keys.DOWN, Direction.DOWN);
        keycodeToDirection.put(Input.Keys.LEFT, Direction.LEFT);
        keycodeToDirection.put(Input.Keys.RIGHT, Direction.RIGHT);
    }

    private SpriteBatch cursorBatch = new SpriteBatch(); // TODO: is it okay that this is a different SpriteBatch than the one used in the GameEngine?
    {
        Matrix4 projection = new Matrix4();
        projection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cursorBatch.setProjectionMatrix(projection);
    }


    private SpriteBatch batch = new SpriteBatch();
    private SpawnInfo.SpawnType spawnType;

    public ClickOracle(IClickOracleToRendererAdapter rendererAdapter,
                       IClickOracleToEngineAdapter engineAdapter,
                       IClickOracleToNetworkAdapter networkAdapter) {
        this.rendererAdapter = rendererAdapter;
        this.engineAdapter = engineAdapter;
        this.networkAdapter = networkAdapter;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycodeToDirection.containsKey(keycode)) {
            rendererAdapter.setTranslationDirection(keycodeToDirection.get(keycode));
            return true;
        }
        return true;
    }


    @Override
    public boolean keyUp(int keycode) {
        if (keycodeToDirection.containsKey(keycode)) {
            rendererAdapter.setTranslationDirection(Direction.NONE);
        }
        return true;
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT) return false;
        selectionLocationStart.set(screenX, screenY, 0);
        rendererAdapter.unproject(selectionLocationStart);
        Gdx.app.log("ClickOracle - lji1", "Touchdown!");
        return false;
    }
    
    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) return false;
        dragging = true;
        selectionLocationEnd.set(screenX, screenY, 0);
        rendererAdapter.unproject(selectionLocationEnd);
        Gdx.app.log("ClickOracle - lji1", "Dragged: " + selectionLocationEnd.x + ", " + selectionLocationEnd.y);
        return false;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Gdx.app.log("lji1 - ClickOracle", "Click registered, dragging: " + dragging);

        // The player must be alive to be able to register any clicks
        if (!engineAdapter.isPlayerAlive()) {
            Gdx.app.log("lji1 - ClickOracle", "Player has died.");
            return false;
        }

        if (dragging) {
            dragging = false;
            
            // mark the units in the box as selected
            Array<Unit> unitsToSelect = engineAdapter.getUnitsInBox(selectionLocationStart, selectionLocationEnd);

            deMultiSelect();
            unitsToSelect.forEach((unit) -> multiSelect(unit));

            Gdx.app.log("lji1 - ClickOracle", "Cleared selection locations.");
            return false;
        }

        touchPos.set(screenX, screenY, 0);
        // for 3D, get the ray that the click represents.
        pickedRay.set(rendererAdapter.getRay(screenX, screenY));
        // TODO generalize this.

        // for 2D, just unproject.
        rendererAdapter.unproject(touchPos);
        if (button == Input.Buttons.LEFT) {

            // Create tower with left click and numberkey down.
            // For advanced users, we will keep this as the first check, then defer to the other users
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
                Gdx.app.log("lji1 - ClickOracle", "Spawn basic tower.");
                networkAdapter.sendMessage(new CreateTowerMessage(touchPos.x, touchPos.y, TowerType.BASIC, networkAdapter.getPlayerId()));
            } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
                Gdx.app.log("lji1 - ClickOracle", "Spawn sniper tower.");
                networkAdapter.sendMessage(new CreateTowerMessage(touchPos.x, touchPos.y, TowerType.SNIPER, networkAdapter.getPlayerId()));
            } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
                Gdx.app.log("lji1 - ClickOracle", "Spawn tank tower.");
                networkAdapter.sendMessage(new CreateTowerMessage(touchPos.x, touchPos.y, TowerType.TANK, networkAdapter.getPlayerId()));
            } else {

                // Unit unit = engineAdapter.selectUnit(touchPos);
                Unit unit = engineAdapter.selectUnitFromRay(pickedRay);
                if (unit == null) {
                    Gdx.app.log("ttl4 - ClickOracle", "nothing selected!");
                    System.out.println("aaaaa");
                    if (spawnType == SpawnInfo.SpawnType.UNIT) {
                        networkAdapter.sendMessage(new CreateMinionMessage(touchPos.x, touchPos.y, 69, networkAdapter.getPlayerId()));
                    } else if (spawnType == SpawnInfo.SpawnType.TOWER1) {
                        Gdx.app.log("pjb3 - ClickOracle", "Spawn basic tower via state");
                        networkAdapter.sendMessage(new CreateTowerMessage(touchPos.x, touchPos.y, TowerType.BASIC, networkAdapter.getPlayerId()));
                    } else if (spawnType == SpawnInfo.SpawnType.TOWER2) {
                        Gdx.app.log("pjb3 - ClickOracle", "Spawn Tower 2 tower via state");
                        networkAdapter.sendMessage(new CreateTowerMessage(touchPos.x, touchPos.y, TowerType.SNIPER, networkAdapter.getPlayerId()));
                    } else if (spawnType == SpawnInfo.SpawnType.TOWER3) {
                        Gdx.app.log("pjb3 - ClickOracle", "Spawn basic tower via state");
                        networkAdapter.sendMessage(new CreateTowerMessage(touchPos.x, touchPos.y, TowerType.TANK, networkAdapter.getPlayerId()));
                    }
                } else {
                    Gdx.app.log("ttl4 - ClickOracle", "selected a unit!");
                    deMultiSelect();
                    selectionLocationStart.set(unit.x, unit.y, 0);
                    selectionLocationEnd.set(unit.x, unit.y, 0);
                    multiSelect(unit);
                }
            }
            return false;
        }
        // Right click
        if (multiSelected.notEmpty() && button == Input.Buttons.RIGHT) {
            System.out.println("start: " + selectionLocationStart + " end: " + selectionLocationEnd);
                networkAdapter.sendMessage(new MoveMinionMessage(touchPos.x, touchPos.y,
                        networkAdapter.getPlayerId(), multiSelected));
//            }
            return false;

        }
        
        deMultiSelect();
        return false;
    }


    private void deMultiSelect() {
        if (multiSelected.notEmpty()) {
            multiSelected.forEach((u) -> u.clicked = false);
            multiSelected.clear();
        }
    }
    
    private void multiSelect(Unit unit) {
        if (unit.getPlayerId() == networkAdapter.getPlayerId()) {
            multiSelected.add(unit);
            unit.clicked = true;
        }
    }

    public void setSpawnType(SpawnInfo newInfo) {
        spawnType = newInfo.getType();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public void render() {
        batch.setProjectionMatrix(rendererAdapter.getCamera().combined);
//        Gdx.app.log("projection matrix ", batch.getProjectionMatrix().toString());
        // selectionMatrix.setToOrtho2D(0, 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth());
        // batch.setProjectionMatrix(selectionMatrix);

        batch.setColor(1, 1,1, 0.5f);
        batch.begin();
        

        if (dragging) {

            System.out.println("start: " + selectionLocationStart + " end: " + selectionLocationEnd);

            Texture t = TextureUtils.makeUnfilledRectangle(1,1, Color.YELLOW);
            batch.draw(
                    t,
                    Math.min(selectionLocationStart.x, selectionLocationEnd.x),
                    Math.min(selectionLocationStart.y, selectionLocationEnd.y),
                    Math.abs((selectionLocationEnd.x - selectionLocationStart.x)),
                    Math.abs((selectionLocationEnd.y - selectionLocationStart.y))
            );
        }
        
        batch.end();
        batch.setColor(1, 1,1, 1);
    }

}
