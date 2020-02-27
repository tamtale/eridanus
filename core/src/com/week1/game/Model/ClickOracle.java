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
import com.week1.game.Model.Entities.Clickable;
import com.week1.game.Model.Entities.Unit;
import com.week1.game.Networking.Messages.Game.CreateMinionMessage;
import com.week1.game.Networking.Messages.Game.MoveMinionMessage;
import com.week1.game.Networking.Messages.Game.CreateTowerMessage;
import com.week1.game.Renderer.TextureUtils;

import java.util.HashMap;
import java.util.Map;

public class ClickOracle extends InputAdapter {

    private static final String TAG = "ClickOracle";

    private IClickOracleAdapter adapter;
    private Clickable selected = Clickable.NULL;
    private Clickable passiveSelected = Clickable.NULL;

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

    public ClickOracle(IClickOracleAdapter adapter) {
        this.adapter = adapter;
        
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
        selectionLocationStart.set(screenX, screenY, 0);
        adapter.unproject(selectionLocationStart);
        Gdx.app.log("ClickOracle - lji1", "Touchdown!");
        return false;
    }
    
    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) return false;
        dragging = true;
        selectionLocationEnd.set(screenX, screenY, 0);
        adapter.unproject(selectionLocationEnd);
        Gdx.app.log("ClickOracle - lji1", "Dragged: " + selectionLocationEnd.x + ", " + selectionLocationEnd.y);
        return false;
    }

    @Override
    public boolean mouseMoved (int screenX, int screenY) {
        setPassiveClickable(adapter.selectClickable(screenX, screenY, touchPos));
        return true;
    }

    private void setPassiveClickable (Clickable clickable) {
        passiveSelected.setSelected(false);
        passiveSelected = clickable;
        passiveSelected.setSelected(true);
    }

    private void setSelectedClickable(Clickable clickable) {
        selected.setSelected(false);
        selected = clickable;
        selected.setSelected(true);
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Gdx.app.log("lji1 - ClickOracle", "Click registered, dragging: " + dragging);

        // The player must be alive to be able to register any clicks
        if (!adapter.isPlayerAlive()) {
            Gdx.app.log("lji1 - ClickOracle", "Player has died.");
            return false;
        }

        if (dragging) {
            dragging = false;
            
            // mark the units in the box as selected
            Array<Unit> unitsToSelect = adapter.getUnitsInBox(selectionLocationStart, selectionLocationEnd);

            deMultiSelect();
            unitsToSelect.forEach(this::multiSelect);

            Gdx.app.log("lji1 - ClickOracle", "Cleared selection locations.");
            return false;
        }

        touchPos.set(screenX, screenY, 0);
        // for 3D, get the ray that the click represents.

        int currentGameHash = adapter.getGameStateHash();
//        Gdx.app.log("pjb3 - ClickOracle", "hash int is " + currentGameHash);
//        Gdx.app.log("pjb3 - ClickOracle", "the human readable is: " + adapter.getGameStateString());

        // for 2D, just unproject.
        adapter.unproject(touchPos);
        if (button == Input.Buttons.LEFT) {
            Gdx.app.log("lji1 - ClickOracle", "Left click.");

            // Create tower with left click and numberkey down.
            // For advanced users, we will keep this as the first check, then defer to the other users
//            if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
//                Gdx.app.log("lji1 - ClickOracle", "Spawn basic tower.");
//                adapter.sendMessage(new CreateTowerMessage(touchPos.x, touchPos.y, 1, 0, adapter.getPlayerId(), currentGameHash));
//            } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
//                Gdx.app.log("lji1 - ClickOracle", "Spawn sniper tower.");
//                adapter.sendMessage(new CreateTowerMessage(touchPos.x, touchPos.y, 1, 1, adapter.getPlayerId(), currentGameHash));
//            } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
//                Gdx.app.log("lji1 - ClickOracle", "Spawn tank tower.");
//                adapter.sendMessage(new CreateTowerMessage(touchPos.x, touchPos.y, 1, 2, adapter.getPlayerId(), currentGameHash));
//            } else {

                // Unit unit = engineAdapter.selectUnit(touchPos);
              setSelectedClickable(adapter.selectClickable(screenX, screenY, touchPos));
              System.out.println("selected: " + selected);
              selected.accept(new Clickable.ClickableVisitor<Void>() {
                  @Override
                  public Void acceptUnit(Unit unit) {
                      Gdx.app.log("GOTTEM", "GOTTEM");
                      return null;
                  }
                  
                  @Override
                  public Void acceptBlockLocation(Vector3 vector) {
                      Gdx.app.log("ClickOracle", "Accepting block location.");
                      
                      // TODO: only using this becasue buttons are broken
                      spawnType = SpawnInfo.SpawnType.TOWER1; // TODO: remove me !
                      
                        System.out.println("Sending message to spawn tower at: " + vector);
                      
//                      if (unit == null) {
                          if (spawnType == SpawnInfo.SpawnType.UNIT) {
                              Gdx.app.log("pjb3 - ClickOracle", "Spawn unit");
                              adapter.sendMessage(new CreateMinionMessage(touchPos.x, touchPos.y, 69, adapter.getPlayerId(), currentGameHash));
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
//                      } else {
//                          Gdx.app.log("ttl4 - ClickOracle", "selected a unit!");
//                          deMultiSelect();
//                          selectionLocationStart.set(unit.getX(), unit.getY(), 0);
//                          selectionLocationEnd.set(unit.getX(), unit.getY(), 0);
//                          multiSelect(unit);
//                      }
                  
                      return null;
                  }

                  @Override
                  public Void acceptNull() {
                    // TODO create an entity based on the pressed button.
                      return null;
                  }
              });
            return false;
        }
        // Right click
        if (multiSelected.notEmpty() && button == Input.Buttons.RIGHT) {
            System.out.println("start: " + selectionLocationStart + " end: " + selectionLocationEnd);
                adapter.sendMessage(new MoveMinionMessage(touchPos.x, touchPos.y,
                        adapter.getPlayerId(), multiSelected, currentGameHash));
//            }
            return false;

        }
        
        deMultiSelect();
        return false;
    }


    private void deMultiSelect() {
        if (multiSelected.notEmpty()) {
            multiSelected.forEach((u) -> u.setClicked(false));
            multiSelected.clear();
        }
    }
    
    private void multiSelect(Unit unit) {
        if (unit.getPlayerId() == adapter.getPlayerId()) {
            multiSelected.add(unit);
            unit.setClicked(true);
        }
    }

    public void setSpawnType(SpawnInfo newInfo) {
        spawnType = newInfo.getType();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public void render() {
        batch.setProjectionMatrix(adapter.getCamera().combined);
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
