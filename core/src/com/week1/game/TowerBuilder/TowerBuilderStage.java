package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.GameController;

public class TowerBuilderStage {
    private TowerBuilderScreen towerscreen;
    public Stage stage;
    public StatsWidget sw;
    private TextButton tower2Button;
    public TowerBuilderCamera builder;
    private TextButton startGame;

    public TowerBuilderStage(TowerBuilderScreen towerscreen) {
        this.towerscreen =towerscreen;
        stage = new Stage(new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));


        setWidgets();
        configureWidgets();
        setListeners();
    }

    public void setTowerBuilder(TowerBuilderCamera builder) {
        this.builder = builder;
    }

    private void setWidgets() {
        sw = new StatsWidget();
        tower2Button = new TextButton("Tower 2", new Skin(Gdx.files.internal("uiskin.json")));
        startGame = new TextButton("Start Game", new Skin(Gdx.files.internal("uiskin.json")));
    }

    private void configureWidgets() {
        //Stats widget things
        sw.setSize(200,150);
        sw.setPosition(GameController.VIRTUAL_WIDTH - 250, GameController.VIRTUAL_HEIGHT - 200);

        tower2Button.setSize(128, 48);
        tower2Button.setPosition(64, 24);

        startGame.setSize(128, 48);
        startGame.setPosition(64, GameController.VIRTUAL_HEIGHT - 200);

        stage.addActor(sw);
        stage.addActor(tower2Button);
        stage.addActor(startGame);

    }

    private void setListeners() {
        tower2Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                builder.currTower = builder.presets.getTower2();
                sw.setLblTxt(builder.presets.getTower2().getHp(), builder.presets.getTower2().getAtk(), builder.presets.getTower2().getRange());
            }
        });

        startGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                towerscreen.startGame();
            }
        });
    }

    public void render() {
        stage.draw();
    }


}



