package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.Entities.Clickable;
import com.week1.game.Model.Entities.Unit;
import com.week1.game.Networking.Messages.Game.CreateMinionMessage;
import com.week1.game.Networking.Messages.Game.MoveMinionMessage;
import com.week1.game.Networking.Messages.Game.CreateTowerMessage;
import com.week1.game.Renderer.RenderConfig;
import com.week1.game.Renderer.TextureUtils;

import java.util.HashMap;
import java.util.Map;

public class ClickOracle extends InputAdapter {

    private static final String TAG = "ClickOracle";

    private IClickOracleAdapter adapter;

    private Vector3 touchPos = new Vector3();
    
//    private Clickable selected = Clickable.NULL;
    private Clickable passiveSelected = Clickable.NULL;
    private Array<Unit> multiSelected = new Array<>();
    
    private RenderConfig renderConfig;

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

//    private SpriteBatch cursorBatch = new SpriteBatch(); // TODO: is it okay that this is a different SpriteBatch than the one used in the GameEngine?
//    {
//        Matrix4 projection = new Matrix4();
//        projection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        cursorBatch.setProjectionMatrix(projection);
//    }


//    private SpriteBatch batch = new SpriteBatch();
    private SpawnInfo.SpawnType spawnType;

    public ClickOracle(IClickOracleAdapter adapter, RenderConfig renderConfig) {
        this.adapter = adapter;
        this.renderConfig = renderConfig;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycodeToDirection.containsKey(keycode)) {
            adapter.setTranslationDirection(keycodeToDirection.get(keycode));
            return true;
        }
        return true;
    }


    @Override
    public boolean keyUp(int keycode) {
        if (keycodeToDirection.containsKey(keycode)) {
            adapter.setTranslationDirection(Direction.NONE);
        }
        return true;
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT) return false;
        selectionLocationStart.set(screenX, Gdx.graphics.getHeight() - screenY, 0);
        System.out.println("Start coords: " + selectionLocationStart);
        return false;
    }
    
    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) return false;
        dragging = true;
        passiveSelected.setHovered(false);
        selectionLocationEnd.set(screenX, Gdx.graphics.getHeight() - screenY, 0);
        Gdx.app.log("ClickOracle - lji1", "Dragged: " + selectionLocationEnd.x + ", " + selectionLocationEnd.y);
        return false;
    }

    long startTime = 0;
    long endTime = 0;
    int events = 0;
    int sum = 0;
    
    @Override
    public boolean mouseMoved (int screenX, int screenY) {
        endTime = System.nanoTime();
        int diff = (int)((endTime - startTime) / 1000000);
        if (diff < 1000) {
            events++;
            sum += diff;
        }
        startTime = System.nanoTime();
        
        setPassiveClickable(adapter.selectClickable(screenX, screenY, touchPos));
        return true;
    }

    private void setPassiveClickable (Clickable clickable) {
        passiveSelected.setHovered(false);
        passiveSelected = clickable;
        passiveSelected.setHovered(true);
    }

//    private void setSelectedClickable(Clickable clickable) {
//        Gdx.app.log("setSelectedClickable", "set selected: " + clickable.toString());
//        deMultiSelect();
//        selected = clickable;
//        selected.setSelected(true);
//    }
    private void addToMultiselected(Unit u) {
        u.setSelected(true);
        multiSelected.add(u);
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Gdx.app.log("lji1 - ClickOracle", "Click registered, dragging: " + dragging);

        // The player must be alive to be able to register any clicks
        if (!adapter.isPlayerAlive()) {
            Gdx.app.log("lji1 - ClickOracle", "Player has died.");
            return false;
        }

        touchPos.set(screenX, screenY, 0);
        // for 3D, get the ray that the click represents.
        
        int currentGameHash = adapter.getGameStateHash();
        
        
        // If the player was dragging, the friendly units in the drag box are selected
        if (dragging) {
            System.out.println("Done dragging");
            
            // Add the units in the drag box to multiselected
            deMultiSelect();
            Array<Unit> dragSelected = adapter.getUnitsInBox(selectionLocationStart, selectionLocationEnd, renderConfig);
            for(int u = 0; u < dragSelected.size; u++) { 
                if (dragSelected.get(u).getPlayerId() == adapter.getPlayerId()) {
                    addToMultiselected(dragSelected.get(u));
                }
            }
            
            dragging = false;
            return true;
        } else { // the player was not dragging, so maybe they clicked directly on something
            Clickable clicked = adapter.selectClickable(screenX, screenY, touchPos);

            if (button == Input.Buttons.LEFT) {
                Gdx.app.log("lji1 - ClickOracle", "Left click.");
                
                clicked.accept(new Clickable.ClickableVisitor<Void>() {
                    @Override
                    public Void acceptUnit(Unit unit) {
                        // if the player left clicks on a friendly unit, that single unit becomes the selected unit
                        if (unit.getPlayerId() == adapter.getPlayerId()) {
                            deMultiSelect();
                            addToMultiselected(unit);
                        }
                        return null;
                    }
                    @Override
                    public Void acceptBlockLocation(Vector3 vector) {
                        // if the player left clicks on a block, spawn something on that block
                        Gdx.app.log("ClickOracle", "Accepting block location.");
                        if (spawnType == SpawnInfo.SpawnType.UNIT) {
                            Gdx.app.log("pjb3 - ClickOracle", "Spawn unit");
                            adapter.sendMessage(new CreateMinionMessage(vector.x, vector.y, vector.z + 1, 69, adapter.getPlayerId(), currentGameHash));
                        } else if (spawnType == SpawnInfo.SpawnType.TOWER1) {
                            Gdx.app.log("pjb3 - ClickOracle", "Spawn basic tower via state");
                            adapter.sendMessage(new CreateTowerMessage(vector.x, vector.y, vector.z + 1, 0, adapter.getPlayerId(), currentGameHash));
                        } else if (spawnType == SpawnInfo.SpawnType.TOWER2) {
                            Gdx.app.log("pjb3 - ClickOracle", "Spawn Tower 2 tower via state");
                            adapter.sendMessage(new CreateTowerMessage(vector.x, vector.y, vector.z + 1, 1, adapter.getPlayerId(), currentGameHash));
                        } else if (spawnType == SpawnInfo.SpawnType.TOWER3) {
                            Gdx.app.log("pjb3 - ClickOracle", "Spawn basic tower via state");
                            adapter.sendMessage(new CreateTowerMessage(vector.x, vector.y, vector.z + 1, 2, adapter.getPlayerId(), currentGameHash));
                        }
                        return null;
                    }

                    @Override
                    public Void acceptNull() {
                        // if the player clicks on nothing, empty the selection
                        deMultiSelect();
                        return null;
                    }
                });
                return false;
            }
            // Right click
            if (button == Input.Buttons.RIGHT) {
                clicked.accept(new Clickable.ClickableVisitor<Void>() {
                    @Override
                    public Void acceptUnit(Unit unit) {
                        // TODO attack a different unit
                        return null;
                    }

                    @Override
                    public Void acceptBlockLocation(Vector3 vector) {
                        // the player right clicked on a location - move all selected minions to this location
                        if (multiSelected.notEmpty()) {
                            System.out.println("About to send move message with these minions: " + multiSelected);
                            adapter.sendMessage(new MoveMinionMessage(vector.x, vector.y, adapter.getPlayerId(), multiSelected, adapter.getGameStateHash()));
                        }
                        return null;
                    }

                    @Override
                    public Void acceptNull() {
                        return null;
                    }
                });
            }
            
            
            
        }


        return false;
    }


    private void deMultiSelect() {
//        selected.setSelected(false);
        multiSelected.forEach(clickable -> clickable.setSelected(false));
        multiSelected.clear();
    }
    
    public void setSpawnType(SpawnInfo newInfo) {
        spawnType = newInfo.getType();
    }

//    public SpriteBatch getBatch() {
//        return batch;
//    }

    public void render() {
        SpriteBatch batch = renderConfig.getBatch();

        batch.setColor(1, 1,1, 0.5f);
        batch.begin();

        if (dragging) {
            Texture t = TextureUtils.makeUnfilledRectangle(1,1, Color.YELLOW);
            batch.draw(
                    t,
//                    50, 50, 400, 400
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
