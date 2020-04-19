package com.week1.game.Renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.week1.game.GameController;
import com.week1.game.Model.SpawnInfo;
import com.week1.game.Model.SpawnInfo.SpawnType;

public class GameButtonsStage {

    private IRendererAdapter adapter;
    public Stage stage;
    private TextButton unitButton;
    private TextButton tower1Button;
    private TextButton tower2Button;
    private TextButton tower3Button;
    private TextButton showSpawnRadiusCheckBox;
    private TextButton showAttackRadiusCheckBox;
    private TextButton restartGame;
    private Label manaLabel;
    private Label winLabel;

    private Button selectedButton; // The currently pressed spawn button.
    private boolean showAttack;
    private boolean showSpawn;
    private boolean firstTimeRender = true;

    private static TextButton.TextButtonStyle normalStyle = new TextButton.TextButtonStyle(
            new Skin(Gdx.files.internal("uiskin.json")).getDrawable("default-round"),
            new Skin(Gdx.files.internal("uiskin.json")).getDrawable("default-round"),
            new Skin(Gdx.files.internal("uiskin.json")).getDrawable("default-round"), new BitmapFont());

    // TODO refactor to use isChecked()
    private static TextButton.TextButtonStyle pressedStyle = new TextButton.TextButtonStyle(
            new Skin(Gdx.files.internal("uiskin.json")).getDrawable("default-round-down"),
            new Skin(Gdx.files.internal("uiskin.json")).getDrawable("default-round-down"),
            new Skin(Gdx.files.internal("uiskin.json")).getDrawable("default-round-down"), new BitmapFont());

    private static TextButton.TextButtonStyle pressedBlueStyle = new TextButton.TextButtonStyle(
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.BLUE),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.BLUE),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.BLUE), new BitmapFont());


    private static Label.LabelStyle clearStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

    public GameButtonsStage(IRendererAdapter adapter) {
        stage = new Stage(new ExtendViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));
        this.adapter = adapter;

        setWidgets();
        configureWidgets();
        setListeners();

        adapter.setSelectedSpawnState(new SpawnInfo(SpawnType.NONE));
    }

    private void setWidgets() {
        unitButton   = new TextButton("Spawn Unit\nCost: " + adapter.getUnitCost(),   new Skin(Gdx.files.internal("uiskin.json")));
        unitButton.setStyle(normalStyle);
        selectedButton = unitButton;

        tower1Button = new TextButton("TEMP 1", new Skin(Gdx.files.internal("uiskin.json")));
        tower1Button.setStyle(normalStyle);
        tower2Button = new TextButton("TEMP 2", new Skin(Gdx.files.internal("uiskin.json")));
        tower2Button.setStyle(normalStyle);
        tower3Button = new TextButton("TEMP 3", new Skin(Gdx.files.internal("uiskin.json")));
        tower3Button.setStyle(normalStyle);

        manaLabel = new Label(String.format("Mana: %d", 0),new Skin(Gdx.files.internal("uiskin.json")));
        manaLabel.setStyle(clearStyle);
        winLabel = new Label("", new Skin(Gdx.files.internal("uiskin.json")));
        winLabel.setFontScale(4);

        showSpawnRadiusCheckBox = new TextButton("Show Spawn Area", new Skin(Gdx.files.internal("uiskin.json")));
        showSpawnRadiusCheckBox.setStyle(pressedBlueStyle);
        showAttackRadiusCheckBox = new TextButton("Show Attack Radii", new Skin(Gdx.files.internal("uiskin.json")));
        showAttackRadiusCheckBox.setStyle(normalStyle);

        restartGame = new TextButton("Restart Match", new Skin(Gdx.files.internal("uiskin.json")));
        restartGame.setStyle(normalStyle);
    }

    private void configureWidgets() {
        unitButton.setSize(128, 48);
        tower1Button.setSize(128, 48);
        tower2Button.setSize(128, 48);
        tower3Button.setSize(128, 48);
        manaLabel.setSize(128, 48);
        winLabel.setSize(128,128);
        showSpawnRadiusCheckBox.setSize(124, 50);
        showAttackRadiusCheckBox.setSize(124, 50);
        restartGame.setSize(128, 48);

        tower1Button.setPosition(34,  20);
        tower2Button.setPosition(172, 20);
        tower3Button.setPosition(310, 20);
        unitButton.setPosition(448, 20);
        manaLabel.setPosition(586, 20);
        winLabel.setPosition(250, 100);
        restartGame.setPosition(300, 75);
        showAttack = false;
        showSpawn = true;
        showSpawnRadiusCheckBox.setPosition(674, 5);
        showAttackRadiusCheckBox.setPosition(674, 60);

        stage.addActor(unitButton);
        stage.addActor(tower1Button);
        stage.addActor(tower2Button);
        stage.addActor(tower3Button);
        stage.addActor(manaLabel);
//        stage.addActor(showAttackRadiusCheckBox); TODO add this back in if we get functionality
//        stage.addActor(showSpawnRadiusCheckBox); TODO add this back in if we get functionality
    }

    private ClickListener createSpawnListener(String buttonName, Button button, SpawnType type) {
        return new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("pjb3 - GameButtonsStage", "Clicked the " + buttonName + " button.");
                if (selectedButton == button) {
                    unselectAndReselect(selectedButton, null);
                    adapter.setSelectedSpawnState(new SpawnInfo(SpawnType.NONE));
                    return;
                }
                adapter.setSelectedSpawnState(new SpawnInfo(type));
                unselectAndReselect(selectedButton, button);
            }
        };
    }

    public void setListeners() {
        unitButton.addListener(createSpawnListener("Unit", unitButton, SpawnType.UNIT));
        tower1Button.addListener(createSpawnListener("Tower 1", tower1Button, SpawnType.TOWER1));
        tower2Button.addListener(createSpawnListener("Tower 2", tower2Button, SpawnType.TOWER2));
        tower3Button.addListener(createSpawnListener("Tower 3", tower3Button, SpawnType.TOWER3));
        showAttackRadiusCheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("pjb3 - GameButtonsStage", "Clicked the ShowAttackRadius toggle. Now: " + !showAttack);
                showAttack = !showAttack;
                if (showAttack) {
                    showAttackRadiusCheckBox.setStyle(pressedBlueStyle);
                } else {
                    showAttackRadiusCheckBox.setStyle(normalStyle);
                }
            }
        });

        showSpawnRadiusCheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("pjb3 - GameButtonsStage", "Clicked the ShowSpawnRadius toggle. Now: " + !showSpawn);
                showSpawn = !showSpawn;
                if (showSpawn) {
                    showSpawnRadiusCheckBox.setStyle(pressedBlueStyle);
                } else {
                    showSpawnRadiusCheckBox.setStyle(normalStyle);
                }

            }
        });

        restartGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("pjb3 - RestartButton", "We are restarting the game now");
                adapter.restartGame();

            }
        });
    }

    public void renderUI(int mana) {
        stage.draw();
        manaLabel.setText(String.format("Mana: %d", mana));
        if (firstTimeRender) {
            firstTimeRender = false;
            tower1Button.setText(adapter.getTowerName(0) + "\nCost: " + adapter.getTowerCost(0));
            tower2Button.setText(adapter.getTowerName(1) + "\nCost: " + adapter.getTowerCost(1));
            tower3Button.setText(adapter.getTowerName(2) + "\nCost: " + adapter.getTowerCost(2));
        }
    }

    /* Force the stage to select the button corresponding to this spawn type. */
    public void selectSpawnType(SpawnType type) {
        switch (type) {
            case NONE:
                unselectAndReselect(selectedButton, null);
                break;
            case UNIT:
                unselectAndReselect(selectedButton, unitButton);
                break;
            case TOWER1:
                unselectAndReselect(selectedButton, tower1Button);
                break;
            case TOWER2:
                unselectAndReselect(selectedButton, tower2Button);
                break;
            case TOWER3:
                unselectAndReselect(selectedButton, tower3Button);
                break;
        }

    }

    /**
     * This deals with changing the button styles as new buttons are pressed
     * @param oldButton the old button to change back to normal style
     * @param newButton the new button to apply a 'selected' style of some sort
     */
    public void unselectAndReselect(Button oldButton, Button newButton) {
        if (oldButton != null) {
            oldButton.setStyle(normalStyle);
        }
        if (newButton != null) {
            newButton.setStyle(pressedStyle);
        }
        selectedButton = newButton;
    }

    /**
     * Called when the game ends for this player. Displays win or loss message.
     * @param winState 1 if win, 0 if loss.
     */
    public void endGame(int winState) {
        if (winState == 1) {
            winLabel.setText("YOU WIN!!");
        } else if (winState == 0) {
            winLabel.setText("YOU LOST");
        }
        stage.addActor(winLabel);
    }

    public void setGameOver() {
        stage.addActor(restartGame);
    }


    public boolean getShowAttackRadius() {
        return showAttack;
    }
    public boolean getShowSpawnRadius() {
        return showSpawn;
    }
}
