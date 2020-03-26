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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.GameController;
import com.week1.game.MenuScreens.MainMenuScreen;

public class TowerBuilderStage {

    private TowerBuilderScreen screen;
    public Stage stage;
    public Stage dialogStage;
    public Stage backgroundImgStage;
    private StatsWidget sw;
    private TextButton startGame;
    private SelectBox<TowerDetails> displaySelection;
    private Label viewTower;

    //Build mode buttons
    private TextButton buildModeBtn;
    private TextButton saveTowerBtn;
    private TextButton addBlockBtn;
    private TextButton removeBlockBtn;
    private SelectBox<String> materialSelection;
    private Dialog dialog;
    Texture tex;
    private Label blockType;

    //make skins
    private static TextButton.TextButtonStyle normalStyle = new TextButton.TextButtonStyle(
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round", Color.valueOf("8e7186")), new BitmapFont());


    //old color - 574053
    private static TextButton.TextButtonStyle pressedStyle = new TextButton.TextButtonStyle(
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.valueOf("3e363c")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.valueOf("3e363c")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.valueOf("3e363c")), new BitmapFont());

    private static ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle(new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-rect", Color.valueOf("9e8196")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-scroll", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-large", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-scroll", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-large", Color.valueOf("8e7186")));

    private static  List.ListStyle listStyle = new List.ListStyle(new BitmapFont(), Color.WHITE, Color.GRAY, new Skin(Gdx.files.internal("uiskin.json")).newDrawable("selection"));

    private static SelectBox.SelectBoxStyle normalSelectBox = new SelectBox.SelectBoxStyle(new BitmapFont(),
            Color.WHITE, new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-select", Color.valueOf("9e8196")),
            scrollStyle, listStyle);

    private static ScrollPane.ScrollPaneStyle disabledScrollStyle = new ScrollPane.ScrollPaneStyle(new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-rect", Color.valueOf("3e363c")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-scroll", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-large", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-scroll", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-large", Color.valueOf("8e7186")));

    private static  List.ListStyle disabledListStyle = new List.ListStyle(new BitmapFont(), Color.DARK_GRAY, Color.DARK_GRAY, new Skin(Gdx.files.internal("uiskin.json")).newDrawable("selection"));


    private static SelectBox.SelectBoxStyle disabledSelectBox = new SelectBox.SelectBoxStyle(new BitmapFont(),
            Color.DARK_GRAY, new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-select", Color.valueOf("3e363c")),
            disabledScrollStyle, disabledListStyle);


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

        buildModeBtn = new TextButton("Build Mode", normalStyle);
        viewTower = new Label("View Tower: ", new Skin(Gdx.files.internal("uiskin.json")));

        //Build mode buttons
        addBlockBtn = new TextButton("Add block", normalStyle);
        removeBlockBtn = new TextButton("Remove Block", normalStyle);
        saveTowerBtn = new TextButton("Save Tower", normalStyle);
        blockType = new Label("Block Type: ", new Skin(Gdx.files.internal("uiskin.json")));

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
                System.out.println(twrName.getText());
                screen.saveTower(twrName.getText());
                dialog.hide();
                twrName.setText("");
            }});
        cancelBtn.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            twrName.setText("");
            dialog.hide();
        }});
        dialog.getContentTable().add(enterName);
        dialog.getContentTable().add(cancelBtn);




        startGame = new TextButton("Main Menu", new Skin(Gdx.files.internal("uiskin.json")));
        startGame.setStyle(normalStyle);
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
        displaySelection.setSize(128, 48);
        displaySelection.setPosition(0, 0);
        stage.addActor(displaySelection);

        viewTower.setSize(128, 48);
        viewTower.setPosition(0, 48);
        stage.addActor(viewTower);


        //Build Mode buttons
        buildModeBtn.setSize(128, 48);
        buildModeBtn.setPosition(128, 0);
        stage.addActor(buildModeBtn);

        addBlockBtn.setSize(128, 48);
        addBlockBtn.setPosition(384, 0);
        stage.addActor(addBlockBtn);
        addBlockBtn.setVisible(false);

        materialSelection.setSize(128, 48);
        materialSelection.setPosition(512, 0);
        stage.addActor(materialSelection);
        materialSelection.setVisible(false);

        blockType.setSize(128, 48);
        blockType.setPosition(512, 48);
        stage.addActor(blockType);
        blockType.setVisible(false);

        removeBlockBtn.setSize(128, 48);
        removeBlockBtn.setPosition(256, 0);
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


        buildModeBtn.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               isBuildMode = !isBuildMode;
               if (buildModeBtn.isChecked()) {
                   screen.displayBuildCore();
                   sw.setLblTxt(screen.getTowerStats());
                   buildModeBtn.setStyle(pressedStyle);
                   activateBuildMode();
               } else {
                   screen.setCamTower(displaySelection.getSelected());
                   sw.setLblTxt(screen.getTowerStats());

                   buildModeBtn.setStyle(normalStyle);
                   deactivateBuildMode();
               }

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
                dialog.show(dialogStage);
            }
        });



        startGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("pjb3 - TowerBuilderStage", "Trying to go back to the main Menu Screen");
                gameController.setScreen(new MainMenuScreen(gameController));
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
        addBlockBtn.setStyle(pressedStyle);
        addBlockBtn.setChecked(true);

    }

    private void deactivateAdd() {
        isAddMode = false;

        //uncheck the button
        addBlockBtn.setChecked(false);
        addBlockBtn.setStyle(normalStyle);
    }

    private void activateRemove() {
        if (isAddMode) {
            deactivateAdd();
        }

        isDelMode = true;

        //press button and stop highlighting previous mode block
        screen.stopHighlighting();
        removeBlockBtn.setStyle(pressedStyle);
        removeBlockBtn.setChecked(true);
    }

    private void deactivateRemove() {
        isDelMode = false;
        removeBlockBtn.setStyle(normalStyle);
        removeBlockBtn.setChecked(false);
    }

    private void activateBuildMode() {
        //display build mode actors
        addBlockBtn.setVisible(true);
        materialSelection.setVisible(true);
        removeBlockBtn.setVisible(true);
        saveTowerBtn.setVisible(true);
        blockType.setVisible(true);

        //hide display select box
        displaySelection.setDisabled(true);
        displaySelection.setStyle(disabledSelectBox);

        //uncheck all buttons
        addBlockBtn.setChecked(false);
        removeBlockBtn.setChecked(false);

    }

    private void deactivateBuildMode() {
        deactivateAdd();
        deactivateRemove();

        //hide actors
        addBlockBtn.setVisible(false);
        materialSelection.setVisible(false);
        removeBlockBtn.setVisible(false);
        saveTowerBtn.setVisible(false);
        blockType.setVisible(false);

        //unhighlight blocks
        screen.stopHighlighting();

        //re-enable display
        displaySelection.setDisabled(false);
        displaySelection.setStyle(normalSelectBox);
    }

}



