package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.GameController;
import com.week1.game.Model.Entities.Clickable;
import com.week1.game.Model.Entities.Crystal;
import com.week1.game.Model.Entities.Tower;
import com.week1.game.Model.Entities.Unit;
import com.week1.game.Model.Events.SelectionEvent;
import com.week1.game.Model.Systems.Publisher;
import com.week1.game.Model.Systems.Subscriber;
import com.week1.game.Networking.Messages.Game.CreateMinionMessage;
import com.week1.game.Networking.Messages.Game.CreateTowerMessage;
import com.week1.game.Networking.Messages.Game.MoveMinionMessage;
import com.week1.game.Networking.Messages.Game.TargetMessage;
import com.week1.game.Renderer.RenderConfig;
import com.week1.game.Renderer.TextureUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClickOracle extends InputAdapter implements Publisher<SelectionEvent> {

    private static final String TAG = "ClickOracle";

    private IClickOracleAdapter adapter;

    private Vector3 touchPos = new Vector3();

    private boolean edgePan;

    private Clickable passiveSelected = Clickable.NULL;
    private Array<Unit> multiSelected = new Array<>();
    private List<Integer> selectedIDs = new ArrayList<>();

    private RenderConfig renderConfig;

    private Vector3 selectionLocationStart = new Vector3();
    private Vector3 selectionLocationEnd = new Vector3();
    private boolean dragging = false;
    /* Current screen coordinates for easy access of closures.*/
    private int curX;
    private int curY;

    private List<Subscriber<SelectionEvent>> selectionSubscribers = new ArrayList<>();

    private ClickOracleCommand nullCommand = () -> {};
    /* Toggle edge panning. */
    private ClickOracleCommand lockCamera = () -> edgePan = !edgePan;
    private ClickOracleCommand goHome = () -> adapter.goToBase();
    private ClickOracleCommand panStop = () -> adapter.setTranslationDirection(Direction.NONE);
    private CommandPair panUp = new CommandPair(() -> adapter.setTranslationDirection(Direction.UP), panStop);
    private CommandPair panDown = new CommandPair(() -> adapter.setTranslationDirection(Direction.DOWN), panStop);
    private CommandPair panLeft = new CommandPair(() -> adapter.setTranslationDirection(Direction.LEFT), panStop);
    private CommandPair panRight = new CommandPair(() -> adapter.setTranslationDirection(Direction.RIGHT), panStop);
    private ClickOracleCommand rotateStop = () -> adapter.setRotationDirection(RotationDirection.NONE);
    private CommandPair rotateClockwise = new CommandPair(() -> adapter.setRotationDirection(RotationDirection.CLOCKWISE), rotateStop);
    private CommandPair rotateCounterclockwise = new CommandPair(() -> adapter.setRotationDirection(RotationDirection.COUNTERCLOCKWISE), rotateStop);
    private ClickOracleCommand spawnNone = () -> {
        setSpawnType(SpawnInfo.SpawnType.NONE);
        adapter.setSpawnType(SpawnInfo.SpawnType.NONE);
    };
    private CommandPair setSpawn1 = new CommandPair(() -> {
        setSpawnType(SpawnInfo.SpawnType.TOWER1);
        adapter.setSpawnType(SpawnInfo.SpawnType.TOWER1);
    }, spawnNone);
    private CommandPair setSpawn2 = new CommandPair(() -> {
        setSpawnType(SpawnInfo.SpawnType.TOWER2);
        adapter.setSpawnType(SpawnInfo.SpawnType.TOWER2);
    }, spawnNone);
    private CommandPair setSpawn3 = new CommandPair(() -> {
        setSpawnType(SpawnInfo.SpawnType.TOWER3);
        adapter.setSpawnType(SpawnInfo.SpawnType.TOWER3);
    }, spawnNone);
    private CommandPair setSpawnUnit = new CommandPair(() -> {
        setSpawnType(SpawnInfo.SpawnType.UNIT);
        adapter.setSpawnType(SpawnInfo.SpawnType.UNIT);
    }, spawnNone);
    // Command to show or hide the crystal tracker display
    private CommandPair showCrystalTracker = new CommandPair(
        () -> {
            adapter.setCrystalTracker(true);
        },
        () -> {
            adapter.setCrystalTracker(false);
        });
    // Command to reset the clickoracle.
    private ClickOracleCommand reset = () -> {
        spawnNone.execute();
        deMultiSelect();
    };
    private IntMap<ClickOracleCommand> keyDownCommands = new IntMap<ClickOracleCommand>(); {

    }
    private IntMap<ClickOracleCommand> keyUpCommands = new IntMap<ClickOracleCommand>();
    {
        registerPair(Input.Keys.W, panUp);
        registerPair(Input.Keys.A, panLeft);
        registerPair(Input.Keys.S, panDown);
        registerPair(Input.Keys.D, panRight);
        registerPair(Input.Keys.E, rotateClockwise);
        registerPair(Input.Keys.Q, rotateCounterclockwise);
        registerPair(Input.Keys.NUM_1, setSpawn1);
        registerPair(Input.Keys.NUM_2, setSpawn2);
        registerPair(Input.Keys.NUM_3, setSpawn3);
        registerPair(Input.Keys.NUM_4, setSpawnUnit);
        registerPair(Input.Keys.TAB, showCrystalTracker);
        keyUpCommands.put(Input.Keys.ESCAPE, reset);
        keyUpCommands.put(Input.Keys.Y, lockCamera);
        keyUpCommands.put(Input.Keys.B, goHome);
    }

    private SpawnInfo.SpawnType spawnType;

    public ClickOracle(IClickOracleAdapter adapter, RenderConfig renderConfig) {
        this.adapter = adapter;
        this.renderConfig = renderConfig;
        if (!GameController.PREFS.contains("edgePan")) {
            GameController.PREFS.putBoolean("edgePan", false);
            GameController.PREFS.flush();
        }
        this.edgePan = GameController.PREFS.getBoolean("edgePan");
    }

    @Override
    public boolean keyDown(int keycode) {
        keyDownCommands.get(keycode, nullCommand).execute();
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        keyUpCommands.get(keycode, nullCommand).execute();
        return true;
    }

    private ClickOracleCommand touchDownCommand = () -> selectionLocationStart.set(curX, Gdx.graphics.getHeight() - curY, 0);

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT) return false;
        curX = screenX;
        curY = screenY;
        touchDownCommand.execute();
        return false;
    }

    /* Command to execute when the player drags with left mouse.*/
    private ClickOracleCommand leftDragCommand = () -> {
        dragging = true;
        passiveSelected.setHovered(false);
        selectionLocationEnd.set(curX, Gdx.graphics.getHeight() - curY, 0);
        Gdx.app.debug("ClickOracle - lji1", "Dragged: " + selectionLocationEnd.x + ", " + selectionLocationEnd.y);
    };

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        curX = screenX;
        curY = screenY;
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) return false;
        leftDragCommand.execute();
        return false;
    }

    long startTime = 0;
    long endTime = 0;
    int events = 0;
    int sum = 0;

    /* Visitor to change the cursor based on what's being hovered over. */
    Clickable.ClickableVisitor<Void> cursorVisitor = new Clickable.ClickableVisitor<Void>() {
        @Override
        public Void acceptUnit(Unit unit) {
            if (unit.getPlayerId() == adapter.getPlayerId()) {
                Gdx.graphics.setCursor(Initializer.defaultCursor);
            } else {
                Gdx.graphics.setCursor(Initializer.targetCursor);
            }
            return null;
        }

        @Override
        public Void acceptBlock(Clickable.ClickableBlock block) {
            Gdx.graphics.setCursor(Initializer.defaultCursor);
            return null;
        }

        @Override
        public Void acceptCrystal(Crystal crystal) {
            Gdx.graphics.setCursor(Initializer.targetCursor);
            return null;
        }

        @Override
        public Void acceptTower(Tower t) {
            if (t.getPlayerId() == adapter.getPlayerId()) {
                Gdx.graphics.setCursor(Initializer.defaultCursor);
            } else {
                Gdx.graphics.setCursor(Initializer.targetCursor);
            }
            return null;
        }

        @Override
        public Void acceptNull() {
            Gdx.graphics.setCursor(Initializer.defaultCursor);
            return null;
        }
    };

    /* Visitor to select a clickable (should be left click). */
    Clickable.ClickableVisitor<Void> selectionVisitor = new Clickable.ClickableVisitor<Void>() {
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
        public Void acceptBlock(Clickable.ClickableBlock block) {
            deMultiSelect();
            return null;
        }

        @Override
        public Void acceptCrystal(Crystal crystal) {
            deMultiSelect();
            return null;
        }

        @Override
        public Void acceptTower(Tower t) {
            deMultiSelect();
            return null;
        }

        @Override
        public Void acceptNull() {
            // if the player clicks on nothing, empty the selection
            deMultiSelect();
            return null;
        }
    };

    private static int SCREEN_THRESHOLD = 30;
    private boolean edgePanning = false; // Panning due to mouse on edge.
    @Override
    public boolean mouseMoved (int screenX, int screenY) {
        endTime = System.nanoTime();
        int diff = (int)((endTime - startTime) / 1000000);
        if (diff < 1000) {
            events++;
            sum += diff;
        }
        startTime = System.nanoTime();

        Clickable passive = adapter.selectClickable(screenX, screenY, touchPos);
        setPassiveClickable(passive);
        passive.accept(cursorVisitor);

        // If the mouse is on the edge of the screen, translate the camera.
        if (edgePan) {
            if (screenX < SCREEN_THRESHOLD) {
                edgePanning = true;
                panLeft.down.execute();
            } else if (screenX > Gdx.graphics.getWidth() - SCREEN_THRESHOLD) {
                edgePanning = true;
                panRight.down.execute();
            } else if (screenY < SCREEN_THRESHOLD) {
                edgePanning = true;
                panUp.down.execute();
            } else if (screenY > Gdx.graphics.getHeight() - SCREEN_THRESHOLD) {
                edgePanning = true;
                panDown.down.execute();
            } else if (edgePanning) {
                edgePanning = false;
                panStop.execute();
            }
        }
        return true;
    }

    private void setPassiveClickable (Clickable clickable) {
        passiveSelected.setHovered(false);
        passiveSelected = clickable;
        passiveSelected.setHovered(true);
    }

    private void addToMultiselected(Unit u) {
        u.setSelected(true);
        multiSelected.add(u);
        selectedIDs.add(u.ID);
    }

    /* Convenience method to send a move minion message on the current multiselected units to the given location. */
    private void moveMultiselected(float x, float y) {
        adapter.sendMessage(new MoveMinionMessage(x, y, adapter.getPlayerId(), multiSelected, adapter.getGameStateHash()));
    }

    /* Convenience method to send a target message on the current multiselected units */
    private void targetMultiselected(int targetID) {
        adapter.sendMessage(new TargetMessage(multiSelected, targetID, adapter.getPlayerId(), adapter.getGameStateHash()));
    }

    /* Visitor for when the player intends to execute a unit action (i.e. right mouse click, units selected) */
    private Clickable.ClickableVisitor<Void> unitActionVisitor = new Clickable.ClickableVisitor<Void>() {
        @Override
        public Void acceptUnit(Unit unit) {
            if (unit.getPlayerId() != adapter.getPlayerId()) {
                moveMultiselected((int) unit.getX(), (int) unit.getY());
                targetMultiselected(unit.ID);
            }
            return null;
        }

        @Override
        public Void acceptBlock(Clickable.ClickableBlock block) {
            Gdx.app.log("luke probably", "About to send move message with these minions: " + multiSelected);
            moveMultiselected(block.x, block.y);
            return null;
        }

        @Override
        public Void acceptCrystal(Crystal crystal) {
            // TODO attack this crystal
            moveMultiselected((int) crystal.getX(), (int) crystal.getY());
            targetMultiselected(crystal.ID);
            return null;
        }

        @Override
        public Void acceptTower(Tower t) {
            moveMultiselected((int) t.getX(), (int) t.getY());
            if (t.getPlayerId() != adapter.getPlayerId()) {
                targetMultiselected(t.ID);
            }
            return null;
        }

        @Override
        public Void acceptNull() {
            return null;
        }
    };

    /* Visitor for when the player intends to execute a spawn action.*/
    private Clickable.ClickableVisitor<Void> spawnActionVisitor = new Clickable.ClickableVisitor<Void>(){

        @Override
        public Void acceptUnit(Unit unit) {
            return null;
        }

        @Override
        public Void acceptBlock(Clickable.ClickableBlock block) {
            int currentGameHash = adapter.getGameStateHash();
            if (spawnType == SpawnInfo.SpawnType.UNIT) {
                Gdx.app.debug("pjb3 - ClickOracle", "Spawn unit");
                adapter.sendMessage(new CreateMinionMessage(block.x, block.y, block.z + 1, 69, adapter.getPlayerId(), currentGameHash));
            } else if (spawnType == SpawnInfo.SpawnType.TOWER1) {
                Gdx.app.debug("pjb3 - ClickOracle", "Spawn tower 1 via state");
                adapter.sendMessage(new CreateTowerMessage(block.x, block.y, block.z + 1, 0, adapter.getPlayerId(), currentGameHash));
            } else if (spawnType == SpawnInfo.SpawnType.TOWER2) {
                Gdx.app.debug("pjb3 - ClickOracle", "Spawn tower 2 via state");
                adapter.sendMessage(new CreateTowerMessage(block.x, block.y, block.z + 1, 1, adapter.getPlayerId(), currentGameHash));
            } else if (spawnType == SpawnInfo.SpawnType.TOWER3) {
                Gdx.app.debug("pjb3 - ClickOracle", "Spawn tower 3 via state");
                adapter.sendMessage(new CreateTowerMessage(block.x, block.y, block.z + 1, 2, adapter.getPlayerId(), currentGameHash));
            } else {
                deMultiSelect();
            }
            return null;
        }

        @Override
        public Void acceptCrystal(Crystal crystal) {
            return null;
        }

        @Override
        public Void acceptTower(Tower t) {
            return null;
        }

        @Override
        public Void acceptNull() {
            return null;
        }
    };

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Gdx.app.debug("lji1 - ClickOracle", "Click registered, dragging: " + dragging);

        // The player must be alive to be able to register any clicks
        if (!adapter.isPlayerAlive()) {
            Gdx.app.debug("lji1 - ClickOracle", "Player has died.");
            return false;
        }

        touchPos.set(screenX, screenY, 0);

        // If the player was dragging, the friendly units in the drag box are selected
        if (dragging) {
            Gdx.app.debug("ClickOracle", "Done dragging");

            // Add the units in the drag box to multiselected
            deMultiSelect();
            Array<Unit> dragSelected = adapter.getUnitsInBox(selectionLocationStart, selectionLocationEnd, renderConfig);
            for(int u = 0; u < dragSelected.size; u++) {
                if (dragSelected.get(u).getPlayerId() == adapter.getPlayerId()) {
                    addToMultiselected(dragSelected.get(u));
                }
            }
            publish(new SelectionEvent(selectedIDs));
            dragging = false;
            spawnType = SpawnInfo.SpawnType.NONE;
            adapter.setSpawnType(SpawnInfo.SpawnType.NONE);
            return true;
        } else { // the player was not dragging, so maybe they clicked directly on something
            Clickable clicked = adapter.selectClickable(screenX, screenY, touchPos);

            if (button == Input.Buttons.LEFT) {
                Gdx.app.debug("lji1 - ClickOracle", "Left click.");
                clicked.accept(selectionVisitor);
                return false;
            }
            // Right click
            if (button == Input.Buttons.RIGHT) {
                if (multiSelected.notEmpty()) {
                    clicked.accept(unitActionVisitor);
                } else {
                    clicked.accept(spawnActionVisitor);
                }
            }
        }
        return false;
    }

    private void deMultiSelect() {
        multiSelected.forEach(clickable -> clickable.setSelected(false));
        multiSelected.clear();
        selectedIDs.clear();
    }

    public void setSpawnType(SpawnInfo newInfo) {
        setSpawnType(newInfo.getType());
    }

    private void setSpawnType(SpawnInfo.SpawnType type) {
        spawnType = type;
        if (spawnType != SpawnInfo.SpawnType.NONE) {
            deMultiSelect();
        }
    }

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

    @Override
    public void addSubscriber(Subscriber<SelectionEvent> subscriber) {
        selectionSubscribers.add(subscriber);
    }

    @Override
    public Collection<Subscriber<SelectionEvent>> getSubscribers() {
        return selectionSubscribers;
    }
    /* Register the given CommandPair with the bindings for keydown and keyup. */
    private void registerPair(int key, CommandPair pair) {
        keyDownCommands.put(key, pair.down);
        keyUpCommands.put(key, pair.up);
    }

    /* Disallow any player actions that shouldn't be possible after loss/game over.*/
    public void stopInput() {
        leftDragCommand = nullCommand;
        touchDownCommand = nullCommand;
        selectionVisitor = Clickable.ClickableVisitor.EMPTY;
        spawnActionVisitor = Clickable.ClickableVisitor.EMPTY;
    }
}

@FunctionalInterface
/*
 * Interface for any command to execute by the ClickOracle.
 * This is primarily to allow easy modification of keybindings.
 */
interface ClickOracleCommand {
    void execute();
}
/* Utility class to organize commands that should go with each other (i.e. on keyUp/keyDown) */
class CommandPair {
    public ClickOracleCommand down;
    public ClickOracleCommand up;
    public CommandPair(ClickOracleCommand down, ClickOracleCommand up) {
        this.down = down;
        this.up = up;
    }
}

