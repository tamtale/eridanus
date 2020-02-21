package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.GameController;
import com.week1.game.Model.ClickOracle;

import java.util.ArrayList;

public class TowerBuilderStage {
    private TowerBuilderScreen towerscreen;
    public Stage stage;
    public StatsWidget sw;
    public TowerBuilderCamera builder;
    private TextButton startGame;
    private SelectBox<TowerDetails> selectBox;
    private TextButton displayButton;

    public TowerBuilderStage(TowerBuilderScreen towerscreen) {
        this.towerscreen =towerscreen;
        stage = new Stage(new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));
    }

    public void setTowerBuilder(TowerBuilderCamera builder) {
        //Once the towerBuilder field is populated by the TowerScreen, we can access the towers' info
        this.builder = builder;

        setWidgets();
        configureWidgets();
        setListeners();
    }



    private void setWidgets() {

        sw = new StatsWidget();
        sw.setLblTxt(
                (int)builder.getCurrTowerDetails().getHp(),
                (int)builder.getCurrTowerDetails().getAtk(),
                (int)builder.getCurrTowerDetails().getRange(),
                (int)builder.getCurrTowerDetails().getPrice()
        );

        displayButton = new TextButton("Display", new Skin(Gdx.files.internal("uiskin.json")));
        selectBox =new SelectBox(new Skin(Gdx.files.internal("uiskin.json")));
        Array<TowerDetails> presets = new Array<>();
        for (TowerDetails p: TowerPresets.presets) {
            presets.add(p);
        }
        selectBox.setItems(presets);


        startGame = new TextButton("Start Game", new Skin(Gdx.files.internal("uiskin.json")));
    }

    private void configureWidgets() {

        //Add the background image
        stage.addActor(new Image(new TextureRegionDrawable(new Texture("fuzzy_galaxy.png"))));

        //Stats widget things
        sw.setSize(200,150);
        sw.setPosition(GameController.VIRTUAL_WIDTH - 250, GameController.VIRTUAL_HEIGHT - 200);
        stage.addActor(sw);



        selectBox.setSize(128, 48);
        selectBox.setPosition(0, 0);
        stage.addActor(selectBox);

        displayButton.setSize(128, 48);
        displayButton.setPosition(128, 0);
        stage.addActor(displayButton);

        //Start Game button
        startGame.setSize(128, 48);
        startGame.setPosition(64, GameController.VIRTUAL_HEIGHT - 200);
        stage.addActor(startGame);

    }

    private void setListeners() {

        displayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TowerDetails selectedTower = selectBox.getSelected();
                builder.setCurrTowerDetails(selectedTower);
                sw.setLblTxt(
                            (int) selectedTower.getHp(),
                            (int) selectedTower.getAtk(),
                            (int) selectedTower.getRange(),
                            (int) selectedTower.getPrice()
                    );
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
        stage.act();
        stage.draw();
    }


}



