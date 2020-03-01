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
    //TODO - make things static?

    private TowerBuilderScreen screen;
    public Stage stage;
    public StatsWidget sw;
    private TextButton startGame;
    private SelectBox<TowerDetails> displaySelection;
    private TextButton displayButton;

    //Build mode buttons
    private TextButton buildMode;
    private TextButton saveTower;
    private TextButton addBlock;
    private TextButton removeBlock;
    private SelectBox<String> materialSelection;

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
    public boolean isAddMode = false;
    public boolean isDelMode = false;


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

        displaySelection =new SelectBox(new Skin(Gdx.files.internal("uiskin.json")));
        Array<TowerDetails> presets = new Array<>();
        for (TowerDetails p: TowerPresets.presets) {
            presets.add(p);
        }
        displaySelection.setItems(presets);

        //Build mode buttons
        materialSelection = new SelectBox<String>(new Skin(Gdx.files.internal("uiskin.json")));
        Array<String> materials = new Array<>();
        for (String material: TowerMaterials.materialNames.keySet()) {
            materials.add(material);
        }
        materialSelection.setItems(materials);

        buildMode = new TextButton("Build Mode", normalStyle);
        addBlock = new TextButton("Add block", normalStyle);
        removeBlock = new TextButton("Remove Block", normalStyle);

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
        displaySelection.setSize(128, 48);
        displaySelection.setPosition(0, 0);
        stage.addActor(displaySelection);

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


        materialSelection.setSize(128, 48);
        materialSelection.setPosition(512, 0);
        stage.addActor(materialSelection);
        materialSelection.setVisible(false);

        removeBlock.setSize(128, 48);
        removeBlock.setPosition(640, 0);
        stage.addActor(removeBlock);
        removeBlock.setVisible(false);




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
                    TowerDetails selectedTower = displaySelection.getSelected();
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
                isAddMode = !isAddMode;
                if (addBlock.isChecked()) {
                    addBlock.setStyle(pressedStyle);

                    //uncheck other buttons
                    screen.stopHighlighting();

                    isDelMode = false;
                    removeBlock.setChecked(false);
                    removeBlock.setStyle(normalStyle);

                } else {
                    addBlock.setStyle(normalStyle);
                }
             }
        });


        removeBlock.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isDelMode = !isDelMode;
                if (removeBlock.isChecked()) {
                    removeBlock.setStyle(pressedStyle);

                    //uncheck other buttons
                    screen.stopHighlighting();

                    isAddMode = false;
                    addBlock.setChecked(false);
                    addBlock.setStyle(normalStyle);

                } else {
                    removeBlock.setStyle(normalStyle);
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
        materialSelection.setVisible(true);
        removeBlock.setVisible(true);
    }

    private void removeBuildButtons() {
        //Hide buttons
        addBlock.setVisible(false);
        materialSelection.setVisible(false);
        removeBlock.setVisible(false);

        //Change modes
        isAddMode = false;
        isDelMode = false;

        //set unpressed styles
        addBlock.setStyle(normalStyle);
        removeBlock.setStyle(normalStyle);

        //unhighlight blocks
        screen.stopHighlighting();
    }

    public String getMaterialSelection() {
        return materialSelection.getSelected();
    }
}



