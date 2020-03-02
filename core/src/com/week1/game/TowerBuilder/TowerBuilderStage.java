package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

import java.util.ArrayList;

public class TowerBuilderStage {
    //TODO - make things static?

    private TowerBuilderScreen screen;
    public Stage stage;
    public Stage dialogStage;
    private StatsWidget sw;
    private TextButton startGame;
    private SelectBox<TowerDetails> displaySelection;
//    private TextButton displayButton;

    //Build mode buttons
    private TextButton buildModeBtn;
    private TextButton saveTowerBtn;
    private TextButton addBlockBtn;
    private TextButton removeBlockBtn;
    private SelectBox<String> materialSelection;
    private Dialog dialog;

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
        dialogStage = new Stage(new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));
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
//        displayButton = new TextButton("Display", normalStyle);

        displaySelection =new SelectBox(new Skin(Gdx.files.internal("uiskin.json")));
        Array<TowerDetails> presets = new Array<>();
        for (TowerDetails p: TowerPresets.presets) {
            presets.add(p);
        }
        //Add the custom towers
        for (TowerDetails custom: screen.getCustomTowerList()) {
            presets.add(custom);
        }
        displaySelection.setItems(presets);

        //Build mode buttons
        materialSelection = new SelectBox<String>(new Skin(Gdx.files.internal("uiskin.json")));
        Array<String> materials = new Array<>();
        for (String material: TowerMaterials.materialNames.keySet()) {
            materials.add(material);
        }
        materialSelection.setItems(materials);

        buildModeBtn = new TextButton("Build Mode", normalStyle);

        //Build mode buttons
        addBlockBtn = new TextButton("Add block", normalStyle);
        removeBlockBtn = new TextButton("Remove Block", normalStyle);
        saveTowerBtn = new TextButton("Save Tower", normalStyle);

        TextField twrName = new TextField("", new Skin(Gdx.files.internal("uiskin.json")));
        dialog = new Dialog("Name your tower", new Skin(Gdx.files.internal("uiskin.json")));
        dialog.text("Enter a name for your tower below: ");
        dialog.getContentTable().row();
        dialog.getContentTable().add(twrName);
        dialog.getContentTable().row();
        TextButton enterName = new TextButton("Enter", new Skin(Gdx.files.internal("uiskin.json")));
        TextButton cancelBtn = new TextButton("Cancel", new Skin(Gdx.files.internal("uiskin.json")));

        enterName.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.saveTower(twrName.getText());
                dialog.hide();
            }});
        cancelBtn.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            dialog.hide();
        }});
        dialog.getContentTable().add(enterName);
        dialog.getContentTable().add(cancelBtn);
//        dialog.setPosition(30, 30);
//        stage.addActor(dialog);


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

//        displayButton.setSize(128, 48);
//        displayButton.setPosition(128, 0);
//        stage.addActor(displayButton);

        //Build Mode buttons
        buildModeBtn.setSize(128, 48);
        buildModeBtn.setPosition(128, 0);
        stage.addActor(buildModeBtn);

        addBlockBtn.setSize(128, 48);
        addBlockBtn.setPosition(256, 0);
        stage.addActor(addBlockBtn);
        addBlockBtn.setVisible(false);

        materialSelection.setSize(128, 48);
        materialSelection.setPosition(384, 0);
        stage.addActor(materialSelection);
        materialSelection.setVisible(false);

        removeBlockBtn.setSize(128, 48);
        removeBlockBtn.setPosition(512, 0);
        stage.addActor(removeBlockBtn);
        removeBlockBtn.setVisible(false);

        saveTowerBtn.setSize(128, 48);
        saveTowerBtn.setPosition(640, 0);
        stage.addActor(saveTowerBtn);
        saveTowerBtn.setVisible(false);

        //Start Game button
        startGame.setSize(128, 48);
        startGame.setPosition(64, GameController.VIRTUAL_HEIGHT - 200);
        stage.addActor(startGame);

    }

    private void setListeners() {

        displaySelection.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.setCamTower(displaySelection.getSelected());
                sw.setLblTxt(screen.getTowerStats());
            }
        });

//        displayButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                if (!isBuildMode) {
//                    screen.setCamTower(displaySelection.getSelected());
//                    sw.setLblTxt(screen.getTowerStats());
//                }
//            }
//        });

        buildModeBtn.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               isBuildMode = !isBuildMode;
               if (buildModeBtn.isChecked()) {
                   screen.displayBuildCore();
                   sw.setLblTxt(screen.getTowerStats());

                   buildModeBtn.setStyle(pressedStyle);
                   addBuildButtons();
               } else {
                   screen.setCamTower(displaySelection.getSelected());
                   sw.setLblTxt(screen.getTowerStats());

                   buildModeBtn.setStyle(normalStyle);
                   removeBuildButtons();
               }

           }
        });

        addBlockBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isAddMode = !isAddMode;
                if (addBlockBtn.isChecked()) {
                    addBlockBtn.setStyle(pressedStyle);

                    //uncheck other buttons
                    screen.stopHighlighting();

                    isDelMode = false;
                    removeBlockBtn.setChecked(false);
                    removeBlockBtn.setStyle(normalStyle);

                } else {
                    addBlockBtn.setStyle(normalStyle);
                }
             }
        });


        removeBlockBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isDelMode = !isDelMode;
                if (removeBlockBtn.isChecked()) {
                    removeBlockBtn.setStyle(pressedStyle);

                    //uncheck other buttons
                    screen.stopHighlighting();

                    isAddMode = false;
                    addBlockBtn.setChecked(false);
                    addBlockBtn.setStyle(normalStyle);

                } else {
                    removeBlockBtn.setStyle(normalStyle);
                }
            }
        });

        saveTowerBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //TODO - this
                dialog.show(dialogStage);
//                screen.saveTower();
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
        addBlockBtn.setVisible(true);
        materialSelection.setVisible(true);
        removeBlockBtn.setVisible(true);
        saveTowerBtn.setVisible(true);
    }

    private void removeBuildButtons() {
        //Hide buttons
        addBlockBtn.setVisible(false);
        materialSelection.setVisible(false);
        removeBlockBtn.setVisible(false);
        saveTowerBtn.setVisible(false);

        //Change modes
        isAddMode = false;
        isDelMode = false;

        //set unpressed styles
        addBlockBtn.setStyle(normalStyle);
        removeBlockBtn.setStyle(normalStyle);

        //unhighlight blocks
        screen.stopHighlighting();
    }

    public String getMaterialSelection() {
        return materialSelection.getSelected();
    }

    public void updateStats(String towerStats) {
        sw.setLblTxt(towerStats);
    }

    public void addTowertoSelections(TowerDetails newTower) {
        //TODO -- only allow a certain number of customizable towers
        Array<TowerDetails> items = this.displaySelection.getItems();
        items.add(newTower);
        displaySelection.setItems(items);
    }

    public void renderDialogs() {
        dialogStage.act();
        dialogStage.draw();
    }
}



