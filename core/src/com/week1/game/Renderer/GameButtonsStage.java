package com.week1.game.Renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.GameController;
import com.week1.game.Renderer.SpawnInfo.*;

public class GameButtonsStage {

    private IGameButtonsToClickOracleAdapter clickOracleAdapter;
    public Stage stage;
    private TextButton unitButton;
    private TextButton tower1Button;
    private TextButton tower2Button;
    private TextButton tower3Button;

    private Button previouslySelected;

    private Button.ButtonStyle pressed;
    private Button.ButtonStyle normal;


    public GameButtonsStage(IGameButtonsToClickOracleAdapter clickOracleAdapter) {
        stage = new Stage(new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));
        this.clickOracleAdapter = clickOracleAdapter;

        // TODO figure out how to actually change the appearance of buttons. Will need to make a json skin file but will probably wait until we are making it pretty
        // https://github.com/libgdx/libgdx/wiki/Skin#skin-json  https://badlogicgames.com/forum/viewtopic.php?f=11&t=14659

        normal = new TextButton.TextButtonStyle(new ButtonStyle().up, new Button.ButtonStyle().down, new ButtonStyle().checked, new BitmapFont());
        pressed = new TextButton.TextButtonStyle(new Button.ButtonStyle().down, new Button.ButtonStyle().down, new Button.ButtonStyle().down, new BitmapFont());

        setWidgets();
        configureWidgets();
        setListeners();

        unitButton.setStyle(pressed);
        previouslySelected = unitButton;
        clickOracleAdapter.setSelectedSpawnState(new SpawnInfo(SpawnType.UNIT));
    }

    private void setWidgets() {
        unitButton   = new TextButton("Spawn Units",   new Skin(Gdx.files.internal("uiskin.json")));
        unitButton.setStyle(pressed);
        tower1Button = new TextButton("Spawn Tower 1", new Skin(Gdx.files.internal("uiskin.json")));
        tower1Button.setStyle(normal);
        tower2Button = new TextButton("Spawn Tower 2", new Skin(Gdx.files.internal("uiskin.json")));
        tower2Button.setStyle(normal);
        tower3Button = new TextButton("Spawn Tower 3", new Skin(Gdx.files.internal("uiskin.json")));
        tower3Button.setStyle(normal);
    }

    private void configureWidgets() {
        unitButton.setSize(128, 48);
        tower1Button.setSize(128, 48);
        tower2Button.setSize(128, 48);
        tower3Button.setSize(128, 48);

        unitButton.setPosition(64,  20);
        tower1Button.setPosition(192, 20);
        tower2Button.setPosition(320, 20);
        tower3Button.setPosition(448, 20);

        stage.addActor(unitButton);
        stage.addActor(tower1Button);
        stage.addActor(tower2Button);
        stage.addActor(tower3Button);
    }

    public void unselectAndReselect(Button oldButton, Button newButton) {
        oldButton.setStyle(normal);
        newButton.setStyle(pressed);
        previouslySelected = newButton;
    }

    public void setListeners() {
        unitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("pjb3 - GameButtonsStage", "Clicked the Unit button");
                clickOracleAdapter.setSelectedSpawnState(new SpawnInfo(SpawnType.UNIT));
                unselectAndReselect(previouslySelected, unitButton);
            }
        });

        tower1Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("pjb3 - GameButtonsStage", "Clicked the Tower 1 button");
                clickOracleAdapter.setSelectedSpawnState(new SpawnInfo(SpawnType.TOWER1));
                unselectAndReselect(previouslySelected, tower1Button);
            }
        });

        tower2Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("pjb3 - GameButtonsStage", "Clicked the Tower 2 button");
                clickOracleAdapter.setSelectedSpawnState(new SpawnInfo(SpawnType.TOWER2));
                unselectAndReselect(previouslySelected, tower2Button);
            }
        });

        tower3Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("pjb3 - GameButtonsStage", "Clicked the Tower 3 button");
                clickOracleAdapter.setSelectedSpawnState(new SpawnInfo(SpawnType.TOWER3));
                unselectAndReselect(previouslySelected, tower3Button);
            }
        });

    }

    public void render() {
        stage.draw();
    }
}
