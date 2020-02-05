package com.week1.game.Renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.GameController;

public class GameButtonsStage {

//    private TowerBuilderScreen towerscreen;
    public Stage stage;
    private TextButton unitButton;
    private TextButton tower1Button;
    private TextButton tower2Button;
    private TextButton tower3Button;

    // .setStyle(ButtonStyle.down)


    public GameButtonsStage() {
        stage = new Stage(new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));

        setWidgets();
        configureWidgets();
        setListeners();
    }

    private void setWidgets() {
        unitButton = new TextButton("Spawn Units", new Skin(Gdx.files.internal("uiskin.json")));
        tower1Button = new TextButton("Spawn Tower 1", new Skin(Gdx.files.internal("uiskin.json")));
        tower2Button = new TextButton("Spawn Tower 2", new Skin(Gdx.files.internal("uiskin.json")));
        tower3Button = new TextButton("Spawn Tower 3", new Skin(Gdx.files.internal("uiskin.json")));
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

    public void setListeners() {
        unitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("pjb3 - GameButtonsStage", "Clicked the Unit button");
            }
        });

        tower1Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("pjb3 - GameButtonsStage", "Clicked the Tower 1 button");
            }
        });

        tower2Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("pjb3 - GameButtonsStage", "Clicked the Tower 2 button");
            }
        });

        tower3Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("pjb3 - GameButtonsStage", "Clicked the Tower 3 button");
            }
        });

    }

    public void render() {
        stage.draw();
    }
}
