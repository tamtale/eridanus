package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Networking.Messages.Game.MoveMinionMessage;
import com.week1.game.Networking.Messages.Game.CreateMinionMessage;
import com.week1.game.Networking.Messages.Game.CreateTowerMessage;
import com.week1.game.Renderer.TextureUtils;

public class ClickOracle extends InputAdapter {

    private Vector3 touchPos = new Vector3();
    private IClickOracleToRendererAdapter rendererAdapter;
    private IClickOracleToEngineAdapter engineAdapter;
    private Array<Unit> multiSelected = new Array<>();
    private IClickOracleToNetworkAdapter networkAdapter;
    
    private Vector3 selectionLocationStart = new Vector3();
    private Vector3 selectionLocationEnd = new Vector3();
    
    private boolean dragging = false;
    
    private SpriteBatch batch; 

    public ClickOracle(IClickOracleToRendererAdapter rendererAdapter, 
                       IClickOracleToEngineAdapter engineAdapter,
                       IClickOracleToNetworkAdapter networkAdapter) {
        this.rendererAdapter = rendererAdapter;
        this.engineAdapter = engineAdapter;
        this.networkAdapter = networkAdapter;
        this.batch = new SpriteBatch();
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        selectionLocationStart.set(screenX, screenY, 0);
        rendererAdapter.unproject(selectionLocationStart);
        Gdx.app.log("ClickOracle - lji1", "Touchdown!");
        return true;
    }
    
    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        dragging = true;
        selectionLocationEnd.set(screenX, screenY, 0);
        rendererAdapter.unproject(selectionLocationEnd);
        Gdx.app.log("ClickOracle - lji1", "Dragged: " + selectionLocationEnd.x + ", " + selectionLocationEnd.y);
        return true;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Gdx.app.log("lji1 - ClickOracle", "Click registered, dragging: " + dragging);
        if (dragging) {
            dragging = false;
            
            // mark the units in the box as selected
            Array<Unit> unitsToSelect = engineAdapter.getUnitsInBox(selectionLocationStart, selectionLocationEnd);
            
            deMultiSelect();
            unitsToSelect.forEach((unit) -> multiSelect(unit));

            Gdx.app.log("lji1 - ClickOracle", "Cleared selection locations.");
            return true;
        }

        touchPos.set(screenX, screenY, 0);
        rendererAdapter.unproject(touchPos);

        // The player must be alive to be able to register any clicks
        if (!engineAdapter.isPlayerAlive()) {
            Gdx.app.log("lji1 - ClickOracle", "Player has died.");
            return false;
        }

        if (button == Input.Buttons.LEFT) {

            // Create tower with left click and numberkey down
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

                Unit unit = engineAdapter.selectUnit(touchPos);
                if (unit == null) {
                    Gdx.app.log("ttl4 - ClickOracle", "nothing selected!");
                    System.out.println("aaaaa");
                    networkAdapter.sendMessage(new CreateMinionMessage(touchPos.x, touchPos.y, 69, networkAdapter.getPlayerId()));
                } else {
                    System.out.println("bbbbb");
                    Gdx.app.log("ttl4 - ClickOracle", "selected selected!");
                    deMultiSelect();
                    selectionLocationStart.set(unit.x, unit.y, 0);
                    selectionLocationEnd.set(unit.x, unit.y, 0);
                    multiSelect(unit);
                }
            }
            return true;
        }
        // Right click
        if (multiSelected.notEmpty() && button == Input.Buttons.RIGHT) {
            // TODO: steering agent behavior
            
            System.out.println("start: " + selectionLocationStart + " end: " + selectionLocationEnd);
            networkAdapter.sendMessage(new MoveMinionMessage(touchPos.x, touchPos.y,
                    networkAdapter.getPlayerId(), multiSelected));
            return true;

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
