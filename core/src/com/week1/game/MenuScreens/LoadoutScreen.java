package com.week1.game.MenuScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.GameController;
import com.week1.game.Networking.NetworkObjects.Client;
import com.week1.game.TowerBuilder.BlockSpec;
import com.week1.game.TowerBuilder.TowerDetails;
import com.week1.game.TowerBuilder.TowerPresets;
import com.week1.game.TowerBuilder.TowerUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This is the screen that you chose your loadout in. This is the screen that is returned to
 * after a game ends so players can choose their loadout again and play another game
 */
public class LoadoutScreen implements Screen {
    private Stage loadoutStage;
    private Client networkClient;
    private boolean sentTowers = false, isHostingClient;
    private SelectBox<TowerDetails> tower1, tower2, tower3;
    private Array<TowerDetails> allTowerOptions;


    private TextButton startButton;
    private TextField mapSeedField;

    private static TextButton.TextButtonStyle normalStyle = new TextButton.TextButtonStyle(
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down"),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down"),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down"), new BitmapFont());

    private static TextButton.TextButtonStyle pressedStyle = new TextButton.TextButtonStyle(
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.DARK_GRAY),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.DARK_GRAY),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.DARK_GRAY), new BitmapFont());


    private static TextButton.TextButtonStyle disabledStyle = new TextButton.TextButtonStyle(
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.BLACK),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.BLACK),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.BLACK), new BitmapFont());

    private static ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle(new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-rect", Color.valueOf("9e8196")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-scroll", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-large", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-scroll", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-large", Color.valueOf("8e7186")));

    private static com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle listStyle = new com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle(new BitmapFont(), Color.WHITE, Color.GRAY, new Skin(Gdx.files.internal("uiskin.json")).newDrawable("selection"));

    private static SelectBox.SelectBoxStyle normalSelectBox = new SelectBox.SelectBoxStyle(new BitmapFont(),
            Color.WHITE, new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-select", Color.valueOf("9e8196")),
            scrollStyle, listStyle);

    public LoadoutScreen(Client client, boolean isHostingClient) {
        this.networkClient = client;
        this.isHostingClient = client.getScreenManager().getIsHost();
        this.loadoutStage = new Stage(new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));
        this.allTowerOptions = new Array<>();
        for (int i = 0; i < TowerPresets.presets.size(); i++) {
            allTowerOptions.add(TowerPresets.presets.get(i));
        }
        for (int i = 0; i < TowerUtils.getCustomTowerList().size(); i++) {
            allTowerOptions.add(TowerUtils.getCustomTowerList().get(i));
        }


        Pixmap firePix = new Pixmap(Gdx.files.internal("firedark.png"));
        Pixmap firePixScaled = new Pixmap((int)GameController.VIRTUAL_WIDTH, (int)GameController.VIRTUAL_HEIGHT, firePix.getFormat());
        firePixScaled.drawPixmap(firePix,
                0, 0, firePix.getWidth(), firePix.getHeight(),
                0, 0, firePixScaled.getWidth(), firePixScaled.getHeight()
        );
        Texture tex = new Texture(firePixScaled);
        firePix.dispose();
        firePixScaled.dispose();

        TextureRegionDrawable reg = new TextureRegionDrawable(tex);
        loadoutStage.addActor(new Image(reg));


        TextButton loadoutSelector = new TextButton("Confirm Your Loadout!", new Skin(Gdx.files.internal("uiskin.json")));
        loadoutSelector.getLabel().setFontScale(2);
        loadoutSelector.setSize(375,64);
        loadoutSelector.setPosition(
                GameController.VIRTUAL_WIDTH / 2 - loadoutSelector.getWidth()/2,
                GameController.VIRTUAL_HEIGHT / 2 - loadoutSelector.getHeight());

        loadoutStage.addActor(loadoutSelector);
        createLoadoutDropdowns();

        loadoutSelector.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                loadoutSelector.setStyle(pressedStyle);
                loadoutSelector.setText("Loadout confirmed.");
                loadoutSelector.setTouchable(Touchable.disabled);
                sendLoadout(Arrays.asList(
                        tower1.getSelected().getLayout(),
                        tower2.getSelected().getLayout(),
                        tower3.getSelected().getLayout()));
                tower1.setDisabled(false);
                tower1.setTouchable(Touchable.disabled);
                tower2.setDisabled(false);
                tower2.setTouchable(Touchable.disabled);
                tower3.setDisabled(false);
                tower3.setTouchable(Touchable.disabled);
            }
        });

        // Make the font for the title
        Label.LabelStyle label1Style = new Label.LabelStyle();
        BitmapFont myFont = new BitmapFont();
        label1Style.font = myFont;
        label1Style.fontColor = Color.WHITE;

        Label label1 = new Label("Loadout Selection. Choose 3 towers",label1Style);
        label1.setFontScale(2);
        label1.setSize(200, 64);
        label1.setPosition(GameController.VIRTUAL_WIDTH / 2 - label1.getWidth()/2,GameController.VIRTUAL_HEIGHT * 3 / 4 );
        label1.setAlignment(Align.center);
        loadoutStage.addActor(label1);


        if (isHostingClient) {
            // Set up the map seed field and label
            Label mapSeedLabel = new Label("Enter a map seed:  ", label1Style);
            mapSeedLabel.setFontScale(2);
            mapSeedLabel.setSize(200, 64);
            mapSeedLabel.setPosition(GameController.VIRTUAL_WIDTH / 2 - mapSeedLabel.getWidth(),GameController.VIRTUAL_HEIGHT / 2);
            mapSeedLabel.setAlignment(Align.right);
            loadoutStage.addActor(mapSeedLabel);
            
            String mapSeedPlaceholderText = generateRandomSeed();
            Skin uiskin = new Skin(Gdx.files.internal("uiskin.json"));
            TextField.TextFieldStyle textFieldStyle = uiskin.get(TextField.TextFieldStyle.class);
            textFieldStyle.font.getData().scale(ConnectionScreen.INPUTSCALE);
            mapSeedField = new TextField(mapSeedPlaceholderText, textFieldStyle);
            mapSeedField.setSize(250, 64);
            mapSeedField.setPosition(GameController.VIRTUAL_WIDTH / 2, GameController.VIRTUAL_HEIGHT / 2);
            loadoutStage.addActor(mapSeedField);

            startButton = new TextButton("Waiting for all players to chose loadouts...", disabledStyle);
            startButton.getLabel().setFontScale(2);
            startButton.setTouchable(Touchable.disabled);
            startButton.setDisabled(true);
            startButton.setSize(600, 64);
            startButton.setPosition(
                    GameController.VIRTUAL_WIDTH / 2 - startButton.getWidth()/2,
                    GameController.VIRTUAL_HEIGHT / 2 - 80 - startButton.getHeight());loadoutStage.addActor(startButton);

            startButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("pjb3 LoadoutScreen", "About to send start message.");
                    long enteredSeed = parseMapSeed(mapSeedField.getText());
                    Gdx.app.log("lji1 LoadoutScreen", "Using seed: " + enteredSeed);
                    networkClient.sendGoToGame(enteredSeed);
                }
            });

        }
        

        networkClient.getScreenManager().setGameReadySequence(()-> {
            if (isHostingClient) {
                startButton.setDisabled(false);
                startButton.setStyle(normalStyle);
                startButton.setTouchable(Touchable.enabled);
                startButton.setText("Launch Game");
            }
        });

        createNewGame(); // MAKE the game but dont start it yet.
        Gdx.input.setInputProcessor(loadoutStage);
 
 
    }
    private long parseMapSeed(String mapSeed) {
        try {
            return Long.parseLong(mapSeed);
        } catch (NumberFormatException e){
            return 123456789;
        }
    }

    private String generateRandomSeed() {
        StringBuilder randomSeed = new StringBuilder();
        
//        Character[] seedCharacters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 
//                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 
//                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        Character[] seedCharacters = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        int seedLength = 10;
        
        for (int i = 0; i < seedLength; i++) {
            randomSeed.append(seedCharacters[ThreadLocalRandom.current().nextInt(0, seedCharacters.length)]);
        }
        
        return randomSeed.toString();
    }

    public void createLoadoutDropdowns() {
        tower1 =new SelectBox(normalSelectBox);
        tower1.setItems(allTowerOptions);
        tower1.setSize(200, 64);
        tower1.setPosition(GameController.VIRTUAL_WIDTH / 8,GameController.VIRTUAL_HEIGHT * 5 / 6 );
        loadoutStage.addActor(tower1);

        tower2 =new SelectBox(normalSelectBox);
        tower2.setItems(allTowerOptions);
        tower2.setSize(200, 64);
        tower2.setPosition(GameController.VIRTUAL_WIDTH * 3.0f / 8,GameController.VIRTUAL_HEIGHT * 5 / 6 );
        loadoutStage.addActor(tower2);

        tower3 =new SelectBox(normalSelectBox);
        tower3.setItems(allTowerOptions);
        tower3.setSize(200, 64);
        tower3.setPosition(GameController.VIRTUAL_WIDTH * 5.0f / 8,GameController.VIRTUAL_HEIGHT * 5 / 6 );
        loadoutStage.addActor(tower3);
    }

    public void createNewGame() {
        GameScreen futureGame = new GameScreen(networkClient);
        networkClient.getScreenManager().setGameScreen(futureGame);
        Gdx.app.log("pjb3 - LoutoutScreen", "the GameScreen is being created NOW. It has been added to the client");
    }

    public void sendLoadout(List<List<BlockSpec>> details) {
        if (!sentTowers) {


            networkClient.sendLoadout(details);
            sentTowers = false;
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        loadoutStage.act();
        loadoutStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        loadoutStage.getViewport().update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        loadoutStage.dispose();
    }
}
