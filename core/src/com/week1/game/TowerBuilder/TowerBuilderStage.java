package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.GameController;

import java.util.ArrayList;

public class TowerBuilderStage {
    private TowerBuilderScreen towerscreen;
    public Stage stage;
    public StatsWidget sw;
    private ArrayList<TextButton> towerButtons = new ArrayList<>();
    public TowerBuilderCamera builder;
    private TextButton startGame;

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
        sw.setLblTxt(builder.getCurrTower().getHp(), builder.getCurrTower().getAtk(),
                builder.getCurrTower().getRange(), builder.getCurrTower().getPrice());


        for (int i = 0; i < builder.presets.NUM_PRESETS; i ++) {
            towerButtons.add(new TextButton("Tower" + Integer.toString(i + 1),
                    new Skin(Gdx.files.internal("uiskin.json"))));
        }

        startGame = new TextButton("Start Game", new Skin(Gdx.files.internal("uiskin.json")));
    }

    private void configureWidgets() {
        //Stats widget things
        sw.setSize(200,150);
        sw.setPosition(GameController.VIRTUAL_WIDTH - 250, GameController.VIRTUAL_HEIGHT - 200);
        stage.addActor(sw);

        //Toggle presets buttons
        for (int i = 0; i < builder.presets.NUM_PRESETS; i ++) {
            towerButtons.get(i).setSize(128, 48);
            towerButtons.get(i).setPosition(i * 128, 0);
            stage.addActor(towerButtons.get(i));
        }


        //Start Game button
        startGame.setSize(128, 48);
        startGame.setPosition(64, GameController.VIRTUAL_HEIGHT - 200);
        stage.addActor(startGame);

    }

    private void setListeners() {
        for (int i = 0; i < builder.presets.NUM_PRESETS; i ++) {

            //copying i to effectively final temp variable so that reference in the click listener works
            int finalI = i;

            towerButtons.get(i).addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Tower currTower = builder.presets.getTower(finalI + 1);
                    builder.setCurrTower(currTower);
                    sw.setLblTxt(currTower.getHp(), currTower.getAtk(), currTower.getRange(), currTower.getPrice());
                }
            });


        }


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



