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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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

    private Button previouslySelected;
    private boolean showAttack;
    private boolean showSpawn;

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
        stage = new Stage(new ScreenViewport());
        this.adapter = adapter;

        setWidgets();
        configureWidgets();
        setListeners();

        adapter.setSelectedSpawnState(new SpawnInfo(SpawnType.UNIT));
    }

    private void setWidgets() {
        unitButton   = new TextButton("Spawn Units",   new Skin(Gdx.files.internal("uiskin.json")));
        unitButton.setStyle(pressedStyle);
        previouslySelected = unitButton;

        tower1Button = new TextButton("Spawn Tower 1", new Skin(Gdx.files.internal("uiskin.json")));
        tower1Button.setStyle(normalStyle);
        tower2Button = new TextButton("Spawn Tower 2", new Skin(Gdx.files.internal("uiskin.json")));
        tower2Button.setStyle(normalStyle);
        tower3Button = new TextButton("Spawn Tower 3", new Skin(Gdx.files.internal("uiskin.json")));
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

        unitButton.setPosition(34,  20);
        tower1Button.setPosition(172, 20);
        tower2Button.setPosition(310, 20);
        tower3Button.setPosition(448, 20);
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
        stage.addActor(showAttackRadiusCheckBox);
        stage.addActor(showSpawnRadiusCheckBox);
    }

    public void setListeners() {
        unitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("pjb3 - GameButtonsStage", "Clicked the Unit button");
                adapter.setSelectedSpawnState(new SpawnInfo(SpawnType.UNIT));
                unselectAndReselect(previouslySelected, unitButton);
            }
        });

        tower1Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("pjb3 - GameButtonsStage", "Clicked the Tower 1 button");
                adapter.setSelectedSpawnState(new SpawnInfo(SpawnType.TOWER1));
                unselectAndReselect(previouslySelected, tower1Button);
            }
        });

        tower2Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("pjb3 - GameButtonsStage", "Clicked the Tower 2 button");
                adapter.setSelectedSpawnState(new SpawnInfo(SpawnType.TOWER2));
                unselectAndReselect(previouslySelected, tower2Button);
            }
        });

        tower3Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("pjb3 - GameButtonsStage", "Clicked the Tower 3 button");
                adapter.setSelectedSpawnState(new SpawnInfo(SpawnType.TOWER3));
                unselectAndReselect(previouslySelected, tower3Button);
            }
        });

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
                Gdx.app.log("pjb3 - RestartButton", "We are restarting the game now");
                adapter.restartGame();

            }
        });
    }

    public void renderUI(int mana) {
        stage.draw();
        manaLabel.setText(String.format("Mana: %d", mana));
    }

    /**
     * This deals with changing the button styles as new buttons are pressed
     * @param oldButton the old button to change back to normal style
     * @param newButton the new button to apply a 'selected' style of some sort
     */
    public void unselectAndReselect(Button oldButton, Button newButton) {
        oldButton.setStyle(normalStyle);
        newButton.setStyle(pressedStyle);
        previouslySelected = newButton;
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
