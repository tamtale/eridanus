package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.GameController;

public class TowerBuilderStage {
    //TODO - cleanup + make things static

    private TowerBuilderScreen screen;
    public Stage stage;
    public StatsWidget sw;
    private TextButton startGame;
    private SelectBox<TowerDetails> selectBox;
    private TextButton displayButton;

    //add a build button
    //when clicked(selected) add a save button and a change material button and remove block button and add block button


    //Build mode buttons
    private TextButton buildMode;
    private TextButton saveTower;
    private TextButton addBlock;
    private TextButton removeBlock;
    private TextButton changeMaterial;
    private SelectBox<String> materials;

    //make skins
    private TextButton.TextButtonStyle normalStyle = new TextButton.TextButtonStyle(
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round", Color.valueOf("8e7186")), new BitmapFont());


    private TextButton.TextButtonStyle pressedStyle = new TextButton.TextButtonStyle(
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.valueOf("574053")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.valueOf("574053")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.valueOf("574053")), new BitmapFont());

    public boolean isBuildMode = false;

    public TowerBuilderStage(TowerBuilderScreen screen) {
        this.screen = screen;
        stage = new Stage(new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));
    }

    public void setTowerBuilder(TowerBuilderCamera builder) {
        //Once the towerBuilder field is populated by the TowerScreen, we can access the towers' info
        setWidgets();
        configureWidgets();
        setListeners();
    }

    private void setWidgets() {
        //Displays Tower stats
        sw = new StatsWidget();
        sw.setLblTxt(screen.getTowerStats());

        //select and display presets
        displayButton = new TextButton("Display", normalStyle);

        selectBox =new SelectBox(new Skin(Gdx.files.internal("uiskin.json")));
        Array<TowerDetails> presets = new Array<>();
        for (TowerDetails p: TowerPresets.presets) {
            presets.add(p);
        }
        selectBox.setItems(presets);

        //Build mode buttons
        buildMode = new TextButton("Build Mode", normalStyle);
        addBlock = new TextButton("Add block", normalStyle);

        startGame = new TextButton("Start Game", normalStyle);
    }

    private void configureWidgets() {

        //Add the background image
        stage.addActor(new Image(new TextureRegionDrawable(new Texture("fuzzy_galaxy.png"))));

        //Stats widget things
        sw.setSize(200,150);
        sw.setPosition(GameController.VIRTUAL_WIDTH - 250, GameController.VIRTUAL_HEIGHT - 200);
        stage.addActor(sw);

        //Select present and display
        selectBox.setSize(128, 48);
        selectBox.setPosition(0, 0);
        stage.addActor(selectBox);

        displayButton.setSize(128, 48);
        displayButton.setPosition(128, 0);
        stage.addActor(displayButton);

        //Build Mode buttons
        buildMode.setSize(128, 48);
        buildMode.setPosition(256, 0);
        stage.addActor(buildMode);

        addBlock.setSize(128, 48);
        addBlock.setPosition(384, 0);
        stage.addActor(addBlock);
        addBlock.setVisible(false);

        //Start Game button
        startGame.setSize(128, 48);
        startGame.setPosition(64, GameController.VIRTUAL_HEIGHT - 200);
        stage.addActor(startGame);

    }

    private void setListeners() {

        displayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isBuildMode) {
                    TowerDetails selectedTower = selectBox.getSelected();
                    screen.setCamTower(selectedTower);
                    sw.setLblTxt(screen.getTowerStats());
                }
            }
        });

        buildMode.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               isBuildMode = !isBuildMode;
               if (isBuildMode) {
                   buildMode.setStyle(pressedStyle);
                   addBuildButtons();
               } else {
                   buildMode.setStyle(normalStyle);
                   removeBuildButtons();
               }

           }
        });

        addBlock.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("clicked add block button");
                if (addBlock.isChecked()) {
                    addBlock.setStyle(pressedStyle);
                } else {
                    addBlock.setStyle(normalStyle);
                }
             }
        });



        startGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isBuildMode) {
                    screen.startGame();
                }
            }
        });
    }

    public void render() {
        stage.act();
        stage.draw();
    }

    private void addBuildButtons() {
        addBlock.setVisible(true);
    }

    private void removeBuildButtons() {
        addBlock.setVisible(false);
    }

}



