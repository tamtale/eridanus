package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.GameController;

public class TowerBuilderStage {

    private TowerBuilderScreen screen;
    public Stage stage;
    public Stage dialogStage;
    public Stage backgroundImgStage;
    private StatsWidget sw;
    private TextButton startGame;

    //View mode buttons
    private SelectBox<TowerDetails> displaySelection;
    private Label viewTowerLbl;
    private TextButton deleteTowerBtn;
    private Dialog deleteTwrDialog;
    private Dialog deletePresetErrDialog;
    private TextButton editTwrBtn;
    private Dialog editPresetErrDialog;
    private TextButton newTwrBtn;


    //Build mode buttons
    private TextButton exitBuildBtn;
    private TextButton saveTowerBtn;
    private Dialog successfulEditsDialog;
    private Dialog failedEditsDialog;
    private boolean isEditing = false;
    private TextButton addBlockBtn;
    private TextButton removeBlockBtn;
    private SelectBox<String> materialSelection;
    private Dialog enterNameDialog;
    Texture tex;
    private Label blockTypeLbl;

    private int COMPONENTWIDTH = 133;
    private int COMPONENTHEIGHT = 48;

    private Label.LabelStyle panelstyle;
    private Label.LabelStyle inactive_panelstyle;

    //Button styles
    private static TextButton.TextButtonStyle normalBtnStyle = new TextButton.TextButtonStyle(
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round", Color.valueOf("8e7186")), new BitmapFont());

    private static TextButton.TextButtonStyle pressedBtnStyle = new TextButton.TextButtonStyle(
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.valueOf("3e363c")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.valueOf("3e363c")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.valueOf("3e363c")), new BitmapFont());

    //Label background Style
    private static ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle(new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-rect", Color.valueOf("9e8196")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-scroll", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-large", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-scroll", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-large", Color.valueOf("8e7186")));

    private static  List.ListStyle listStyle = new List.ListStyle(new BitmapFont(), Color.WHITE, Color.GRAY, new Skin(Gdx.files.internal("uiskin.json")).newDrawable("selection"));

    private static SelectBox.SelectBoxStyle normalSelectBox = new SelectBox.SelectBoxStyle(new BitmapFont(),
            Color.WHITE, new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-select", Color.valueOf("9e8196")),
            scrollStyle, listStyle);


    public boolean isBuildMode = false;
    public boolean isAddMode = false;
    public boolean isDelMode = false;


    private GameController gameController;

    public TowerBuilderStage(GameController game, TowerBuilderScreen towerscreen) {
        this.screen = towerscreen;
        this.gameController = game;
        stage = new Stage(new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));
        dialogStage = new Stage(new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));
        backgroundImgStage = new Stage(new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));
    }

    public void completeCamDependentInit() {
        //Once the towerBuilder field is populated by the TowerScreen, we can access the towers' info
        setWidgets();
        configureWidgets();
        setListeners();
    }

    private void setWidgets() {
        //Lable style
        panelstyle = new Label.LabelStyle();
        TextureRegionDrawable td2 = new TextureRegionDrawable(new Texture("label_active_background.png"));
        panelstyle.background = td2;
        panelstyle.font = new BitmapFont();

        inactive_panelstyle = new Label.LabelStyle();
        TextureRegionDrawable td = new TextureRegionDrawable(new Texture("label_inactive_background.png"));
        inactive_panelstyle.background = td;
        inactive_panelstyle.font = new BitmapFont();
        inactive_panelstyle.fontColor = Color.DARK_GRAY;


        //Displays Tower stats
        sw = new StatsWidget();
        sw.setLblTxt(screen.getTowerStats());

        //select and display presets
//        displayButton = new TextButton("Display", normalStyle);

        displaySelection =new SelectBox(normalSelectBox);
        Array<TowerDetails> presets = new Array<>();
        for (int i = 0; i < TowerPresets.NUM_PRESETS; i++) {
            presets.add(TowerPresets.presets.get(i));
        }
        //Add the custom towers
        java.util.List<TowerDetails> customTowers = TowerUtils.getCustomTowerList();
        for (int i = 0; i < customTowers.size(); i++) {
            presets.add(customTowers.get(i));
        }
      
        displaySelection.setItems(presets);

        //Build mode buttons
        materialSelection = new SelectBox<>(normalSelectBox);

        Array<String> materials = new Array<>();
        for (String material: TowerMaterials.materialCodes.keySet()) {
            materials.add(material);
        }

        materialSelection.setItems(materials);

        newTwrBtn = new TextButton("New Tower", normalBtnStyle);
        viewTowerLbl = new Label("View Tower: ", panelstyle);
        deleteTowerBtn = new TextButton("Delete Tower", normalBtnStyle);
        editTwrBtn = new TextButton("Edit Tower", normalBtnStyle);

        //Dialog for editing towers
        TextButton editErrOkBtn = new TextButton("OK", normalBtnStyle);
        editErrOkBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editPresetErrDialog.hide();
            }
        });
        editPresetErrDialog = new Dialog("Edit Preset Error", new Skin(Gdx.files.internal("uiskin.json")));
        editPresetErrDialog.text("You cannot edit a preset tower");
        editPresetErrDialog.getContentTable().row();
        editPresetErrDialog.getContentTable().add(editErrOkBtn);

        //Dialog for deleting towers and the buttons on the
        TextButton yesBtn = new TextButton("Yes", normalBtnStyle);
        TextButton noBtn = new TextButton("No", normalBtnStyle);
        yesBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.deleteTower(displaySelection.getSelected());
                deleteTwrDialog.hide();
            }
        });

        noBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                deleteTwrDialog.hide();
            }
        });
        yesBtn.setWidth(150);
        noBtn.setWidth(150);
        deleteTwrDialog = new Dialog("Delete Tower", new Skin(Gdx.files.internal("uiskin.json")));
        deleteTwrDialog.text("Are you sure you want to delete this tower?");
        deleteTwrDialog.getContentTable().row();
        deleteTwrDialog.getContentTable().add(yesBtn);
        deleteTwrDialog.getContentTable().add(noBtn);

        //dialog for deleting preset error
        TextButton okBtn = new TextButton("OK", normalBtnStyle);
        okBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                deletePresetErrDialog.hide();
            }
        });
        deletePresetErrDialog = new Dialog("Preset Delete Error", new Skin(Gdx.files.internal("uiskin.json")));
        deletePresetErrDialog.text("You can't delete a preset tower.");
        deletePresetErrDialog.getContentTable().row();
        deletePresetErrDialog.getContentTable().add(okBtn);

        //Build mode buttons
        exitBuildBtn = new TextButton("Exit Build Mode", normalBtnStyle);
        addBlockBtn = new TextButton("Add block", normalBtnStyle);
        removeBlockBtn = new TextButton("Remove Block", normalBtnStyle);
        saveTowerBtn = new TextButton("Save", normalBtnStyle);
        blockTypeLbl = new Label("Block Type: ", panelstyle);


        TextButton okEditSuccessBtn = new TextButton("OK", normalBtnStyle);
        okEditSuccessBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                successfulEditsDialog.hide();
            }
        });
        successfulEditsDialog = new Dialog("Saving Edits", new Skin(Gdx.files.internal("uiskin.json")));
        successfulEditsDialog.text("Edits Successfully saved");
        successfulEditsDialog.getContentTable().row();
        successfulEditsDialog.getContentTable().add(okEditSuccessBtn);



        TextButton okEditFailedBtn = new TextButton("OK", normalBtnStyle);
        okEditFailedBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                failedEditsDialog.hide();
            }
        });
        failedEditsDialog = new Dialog("Error: Saving Edits", new Skin(Gdx.files.internal("uiskin.json")));
        failedEditsDialog.text("Failed to save edits");
        failedEditsDialog.getContentTable().row();
        failedEditsDialog.getContentTable().add(okEditFailedBtn);

        TextField twrNameField = new TextField("", new Skin(Gdx.files.internal("uiskin.json")));
        enterNameDialog = new Dialog("Name your tower", new Skin(Gdx.files.internal("uiskin.json")));
        enterNameDialog.text("Enter a name for your tower below: ");
        enterNameDialog.getContentTable().row();
        enterNameDialog.getContentTable().add(twrNameField);
        enterNameDialog.getContentTable().row();
        TextButton enterNameBtn = new TextButton("Enter", new Skin(Gdx.files.internal("uiskin.json")));
        TextButton cancelBtn = new TextButton("Cancel", new Skin(Gdx.files.internal("uiskin.json")));

        enterNameBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.saveTowerAndView(twrNameField.getText());
                enterNameDialog.hide();
                twrNameField.setText("");

                //Exit build mode after tower save
                isBuildMode = false;
                deactivateBuildMode();

            }});
        cancelBtn.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            twrNameField.setText("");
            enterNameDialog.hide();
        }});
        enterNameDialog.getContentTable().add(enterNameBtn);
        enterNameDialog.getContentTable().add(cancelBtn);




        startGame = new TextButton("Main Menu", new Skin(Gdx.files.internal("uiskin.json")));
        startGame.setStyle(normalBtnStyle);
    }

    private void configureWidgets() {

        //Add the background image to the img stage
        Pixmap firePix = new Pixmap(Gdx.files.internal("star_background.png"));
        Pixmap firePixScaled = new Pixmap((int)GameController.VIRTUAL_WIDTH, (int)GameController.VIRTUAL_HEIGHT, firePix.getFormat());
        firePixScaled.drawPixmap(firePix,
                0, 0, firePix.getWidth(), firePix.getHeight(),
                0, 0, firePixScaled.getWidth(), firePixScaled.getHeight()
        );
        tex = new Texture(firePixScaled);
        firePix.dispose();
        firePixScaled.dispose();

        TextureRegionDrawable reg = new TextureRegionDrawable(tex);
        backgroundImgStage.addActor(new Image(reg));

        //Stats widget things
        sw.setSize(200,150);
        sw.setPosition(GameController.VIRTUAL_WIDTH - 250, GameController.VIRTUAL_HEIGHT - 200);
        stage.addActor(sw);

        //Select present and display
        displaySelection.setSize(COMPONENTWIDTH, COMPONENTHEIGHT);
        displaySelection.setPosition(COMPONENTWIDTH, 0);
        stage.addActor(displaySelection);

        viewTowerLbl.setSize(COMPONENTWIDTH, COMPONENTHEIGHT);
        viewTowerLbl.setPosition(0, 0);
        viewTowerLbl.setAlignment(Align.center);
        stage.addActor(viewTowerLbl);


        //Build Mode buttons
        deleteTowerBtn.setSize(COMPONENTWIDTH, COMPONENTHEIGHT);
        deleteTowerBtn.setPosition(3 * COMPONENTWIDTH, 0);
        stage.addActor(deleteTowerBtn);

        editTwrBtn.setSize(COMPONENTWIDTH, COMPONENTHEIGHT);
        editTwrBtn.setPosition(2 * COMPONENTWIDTH, 0);
        stage.addActor(editTwrBtn);

        newTwrBtn.setSize(COMPONENTWIDTH, COMPONENTHEIGHT);
        newTwrBtn.setPosition(4 * COMPONENTWIDTH, 0);
        stage.addActor(newTwrBtn);

        //Build Mode buttons -
        exitBuildBtn.setSize(COMPONENTWIDTH, COMPONENTHEIGHT);
        exitBuildBtn.setPosition(0,0);
        stage.addActor(exitBuildBtn);
        exitBuildBtn.setVisible(false);

        addBlockBtn.setSize(COMPONENTWIDTH, COMPONENTHEIGHT);
        addBlockBtn.setPosition(COMPONENTWIDTH , 0);
        stage.addActor(addBlockBtn);
        addBlockBtn.setVisible(false);

        blockTypeLbl.setSize(COMPONENTWIDTH, COMPONENTHEIGHT);
        blockTypeLbl.setPosition(COMPONENTWIDTH * 2, 0);
        blockTypeLbl.setAlignment(Align.center);
        stage.addActor(blockTypeLbl);
        blockTypeLbl.setVisible(false);

        materialSelection.setSize(COMPONENTWIDTH, COMPONENTHEIGHT);
        materialSelection.setPosition(COMPONENTWIDTH * 3, 0);
        stage.addActor(materialSelection);
        materialSelection.setVisible(false);

        removeBlockBtn.setSize(COMPONENTWIDTH, COMPONENTHEIGHT);
        removeBlockBtn.setPosition(COMPONENTWIDTH * 4, 0);
        stage.addActor(removeBlockBtn);
        removeBlockBtn.setVisible(false);

        saveTowerBtn.setSize(COMPONENTWIDTH, COMPONENTHEIGHT);
        saveTowerBtn.setPosition(COMPONENTWIDTH * 5, 0);
        stage.addActor(saveTowerBtn);
        saveTowerBtn.setVisible(false);


        //Start Game button
        startGame.setSize(COMPONENTWIDTH, COMPONENTHEIGHT);
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


        newTwrBtn.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               isBuildMode = true;
               screen.setBuildModeTower(true);
               sw.setLblTxt(screen.getTowerStats());
               activateBuildMode();
           }
        });

        deleteTowerBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (TowerPresets.presets.contains(displaySelection.getSelected())) {
                    deletePresetErrDialog.show(dialogStage);
                } else {
                    deleteTwrDialog.show(dialogStage);

                }
            }
        });

        editTwrBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (TowerPresets.presets.contains(displaySelection.getSelected())) {
                    editPresetErrDialog.show(dialogStage);
                } else {
                    isBuildMode = true;
                    isEditing = true;
                    screen.setBuildModeTower(false);
                    activateBuildMode();
                }

            }
        });

        exitBuildBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isBuildMode = false;

                screen.setCamTower(displaySelection.getSelected());
                sw.setLblTxt(screen.getTowerStats());
                deactivateBuildMode();
            }
        });

        addBlockBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (addBlockBtn.isChecked()) {
                    activateAdd();
                } else {
                    deactivateAdd();
                }
             }
        });


        removeBlockBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (removeBlockBtn.isChecked()) {
                    activateRemove();
                } else {
                    deactivateRemove();
                }
            }
        });

        saveTowerBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isEditing) {
                    //delete the tower details in the select box and replace with the new one
                    screen.saveEdits();
                } else {
                    enterNameDialog.show(dialogStage);
                }
            }
        });



        startGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("pjb3 - TowerBuilderStage", "Trying to go back to the main Menu Screen");
                gameController.returnToMainMenu();
            }
        });
    }

    public void render() {
        stage.act();
        stage.draw();
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

    public void renderBackgroundImg() {
        backgroundImgStage.draw();
    }

    public void showDialog(String msg) {
        TextButton Okbtn = new TextButton("OK", new Skin(Gdx.files.internal("uiskin.json")));

        Dialog d = new Dialog("Error", new Skin(Gdx.files.internal("uiskin.json")));
        d.text(msg);
        d.getContentTable().row();
        d.getContentTable().add(Okbtn);

        Okbtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                d.hide();
            }
        });

        d.show(dialogStage);
    }


    private void activateAdd() {
        if (isDelMode) {
            deactivateRemove();
        }

        isAddMode = true;

        //press button and stop highlighting previous mode block
        screen.stopHighlighting();
        addBlockBtn.setStyle(pressedBtnStyle);
        addBlockBtn.setChecked(true);

    }

    private void deactivateAdd() {
        isAddMode = false;

        //uncheck the button
        addBlockBtn.setChecked(false);
        addBlockBtn.setStyle(normalBtnStyle);
    }

    private void activateRemove() {
        if (isAddMode) {
            deactivateAdd();
        }

        isDelMode = true;

        //press button and stop highlighting previous mode block
        screen.stopHighlighting();
        removeBlockBtn.setStyle(pressedBtnStyle);
        removeBlockBtn.setChecked(true);
    }

    private void deactivateRemove() {
        isDelMode = false;
        removeBlockBtn.setStyle(normalBtnStyle);
        removeBlockBtn.setChecked(false);
    }

    private void activateBuildMode() {
        //de-activate and hide view mode things
        viewTowerLbl.setVisible(false);
        displaySelection.setVisible(false);
        newTwrBtn.setVisible(false);
        deleteTowerBtn.setVisible(false);
        editTwrBtn.setVisible(false);

        //display build mode actors
        exitBuildBtn.setVisible(true);
        addBlockBtn.setVisible(true);
        materialSelection.setVisible(true);
        removeBlockBtn.setVisible(true);
        blockTypeLbl.setVisible(true);
        saveTowerBtn.setVisible(true);

        //uncheck all buttons
        addBlockBtn.setChecked(false);
        removeBlockBtn.setChecked(false);

    }

    private void deactivateBuildMode() {
        //Re-activate view mode buttons
        newTwrBtn.setVisible(true);
        viewTowerLbl.setVisible(true);
        displaySelection.setVisible(true);
        deleteTowerBtn.setVisible(true);
        editTwrBtn.setVisible(true);

        isEditing = false;

        deactivateAdd();
        deactivateRemove();

        //hide actors
        exitBuildBtn.setVisible(false);
        addBlockBtn.setVisible(false);
        materialSelection.setVisible(false);
        removeBlockBtn.setVisible(false);
        saveTowerBtn.setVisible(false);
        blockTypeLbl.setVisible(false);

        //unhighlight blocks
        screen.stopHighlighting();

    }

    public void removeTowerFromSelection(TowerDetails twr) {
        Array<TowerDetails> towers = displaySelection.getItems();
        towers.removeValue(twr, true);
        displaySelection.setItems(towers);
    }

    public void addTowerAndView(TowerDetails editedTower) {
        Array<TowerDetails> selectionTowers = displaySelection.getItems();
        selectionTowers.add(editedTower);
        displaySelection.setItems(selectionTowers);

        displaySelection.setSelected(editedTower);
        sw.setLblTxt(screen.getTowerStats());
    }

    public void displaySuccessfulSave() {
        successfulEditsDialog.show(dialogStage);
    }

    public void displayFailedSave() {
        failedEditsDialog.show(dialogStage);
    }

    public void setSelectedTower(TowerDetails tower) {
        displaySelection.setSelected(tower);
        sw.setLblTxt(screen.getTowerStats());
    }
}



