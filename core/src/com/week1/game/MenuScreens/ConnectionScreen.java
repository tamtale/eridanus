package com.week1.game.MenuScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.GameController;
import com.week1.game.GameControllerSetScreenAdapter;
import com.week1.game.Model.Entities.UnitLoader;
import com.week1.game.Model.PlayerInfo;
import com.week1.game.Networking.NetworkObjects.Client;
import com.week1.game.Networking.NetworkObjects.NetworkUtils;

import static com.week1.game.MenuScreens.MenuStyles.blueStyle;
import static com.week1.game.MenuScreens.MenuStyles.disabledStyle;

/**
 * This is the Screen where people chose to host or to join someone who is already hosting.
 * It is preceded by the MainMenu and followed by the LoadOut Screen.
 */
public class ConnectionScreen implements Screen {
    private String selectFaction = "Select A Faction";
    private Stage connectionStage;
    private Client networkClient;
    private GameControllerSetScreenAdapter gameAdapter;
    private boolean hosting;

    TextButton hostGameButton, joinGameButton, launchGameButton, returnToSpashButton;
    SelectBox<String> colorSelectBox;
    TextField nameField;
    Label joinedPlayersLabel, waitJoinMsg;
    TextField ipField;
    Label.LabelStyle labelStyle;
    float TEXTSCALE = 2f;
    float TITLESCALE = 2f;
    public static final float INPUTSCALE = 1.3f;



    public ConnectionScreen(GameControllerSetScreenAdapter gameAdapter) {
        this.gameAdapter = gameAdapter;
        connectionStage = new Stage(new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));


        Pixmap earthPix = new Pixmap(Gdx.files.internal("earthdark.png"));
        Pixmap earthPixScaled = new Pixmap((int)GameController.VIRTUAL_WIDTH, (int)GameController.VIRTUAL_HEIGHT, earthPix.getFormat());
        earthPixScaled.drawPixmap(earthPix,
                0, 0, earthPix.getWidth(), earthPix.getHeight(),
                0, 0, earthPixScaled.getWidth(), earthPixScaled.getHeight()
        );
        Texture tex = new Texture(earthPixScaled);
        earthPix.dispose();
        earthPixScaled.dispose();
        TextureRegionDrawable reg = new TextureRegionDrawable(tex);
        connectionStage.addActor(new Image(reg));

        getNewReturnToSpashButton();
        connectionStage.addActor(returnToSpashButton);
        returnToSpashButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                returnToSpashscreenSimple();
            }
        });

        hostGameButton = new TextButton("Begin Hosting", new Skin(Gdx.files.internal("uiskin.json")));
        hostGameButton.getLabel().setFontScale(TEXTSCALE);
        hostGameButton.setSize(350,64);
        hostGameButton.setPosition(GameController.VIRTUAL_WIDTH/2 - 20 - hostGameButton.getWidth(),
                GameController.VIRTUAL_HEIGHT/2 - hostGameButton.getHeight()/2);
        connectionStage.addActor(hostGameButton);
        hostGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hostGame();
            }
        });


        // Make the joinGameButton
        joinGameButton = new TextButton("Join Game", new Skin(Gdx.files.internal("uiskin.json")));
        joinGameButton.getLabel().setFontScale(TEXTSCALE);
        joinGameButton.setSize(350,64);
        joinGameButton.setPosition(GameController.VIRTUAL_WIDTH / 2 + 20 ,
                GameController.VIRTUAL_HEIGHT / 2 - joinGameButton.getHeight());
        connectionStage.addActor(joinGameButton);
        joinGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = nameField.getText();
                if (name == "") {
                   name = "Empty String";
                }
                name = name.substring(0, 15);
                joinGame(ipField.getText(), new PlayerInfo(name));
            }
        });

        launchGameButton = new TextButton("Create game with connected players!", new Skin(Gdx.files.internal("uiskin.json")));
        launchGameButton.getLabel().setFontScale(INPUTSCALE);
        launchGameButton.setSize(500,64);
        launchGameButton.setPosition(
                GameController.VIRTUAL_WIDTH / 2 - launchGameButton.getWidth()/2,
                GameController.VIRTUAL_HEIGHT * 2/ 3 - 80);
        launchGameButton.setTouchable(Touchable.disabled);
        launchGameButton.setStyle(disabledStyle);

        // Make the font for the title
        labelStyle = new Label.LabelStyle();
        BitmapFont myFont = new BitmapFont();
        labelStyle.font = myFont;
        labelStyle.fontColor = Color.WHITE;

        waitJoinMsg = new Label("Choose a unique faction and wait for \nall players to join and the host to start the game.", labelStyle);
        waitJoinMsg.setFontScale(TEXTSCALE);
        waitJoinMsg.setAlignment(Align.center);
        waitJoinMsg.setSize(300,64);
        waitJoinMsg.setPosition(GameController.VIRTUAL_WIDTH / 2 - waitJoinMsg.getWidth()/2, GameController.VIRTUAL_HEIGHT * 2/ 3 - 40);

        Skin uiskin = new Skin(Gdx.files.internal("uiskin.json"));
        TextField.TextFieldStyle textFieldStyle = uiskin.get(TextField.TextFieldStyle.class);
        textFieldStyle.font.getData().scale(INPUTSCALE);
        ipField = new TextField(NetworkUtils.getLocalHostAddr(), textFieldStyle);
        ipField.setSize(joinGameButton.getWidth(),64);
        ipField.setPosition(GameController.VIRTUAL_WIDTH / 2 + 20 ,GameController.VIRTUAL_HEIGHT / 2);
        connectionStage.addActor(ipField);


        Label label1 = new Label("Connection Stage. Choose Host OR Join", labelStyle);
        label1.setFontScale(TITLESCALE);
        label1.setSize(200, 64);
        label1.setPosition(GameController.VIRTUAL_WIDTH / 2 - 80,GameController.VIRTUAL_HEIGHT * 3 / 4 );
        label1.setAlignment(Align.center);
        connectionStage.addActor(label1);


        colorSelectBox = new SelectBox<>(uiskin);
        colorSelectBox.setItems( UnitLoader.FACTIONS);

        colorSelectBox.setSize(450,64);
        colorSelectBox.setSelectedIndex(0);
        colorSelectBox.setPosition(GameController.VIRTUAL_WIDTH/2 - colorSelectBox.getWidth()/2,GameController.VIRTUAL_HEIGHT  *2 / 3 + 80);
        colorSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                submitColor(colorSelectBox.getSelected());
            }
        });


        joinedPlayersLabel = new Label("Joined Players: ", labelStyle);
        joinedPlayersLabel.setFontScale(TITLESCALE);
        joinedPlayersLabel.setSize(200, 64);
        joinedPlayersLabel.setPosition(
                    GameController.VIRTUAL_WIDTH / 2 - joinedPlayersLabel.getWidth() / 2,
                    GameController.VIRTUAL_HEIGHT * 2 / 3 - 180);
        joinedPlayersLabel.setAlignment(Align.center);

        nameField = new TextField("Enter Your Name", textFieldStyle);
        nameField.setSize(joinGameButton.getWidth(),64);
        nameField.setAlignment(Align.center);
        nameField.setPosition(GameController.VIRTUAL_WIDTH / 2 - nameField.getWidth()/2 ,GameController.VIRTUAL_HEIGHT  *2 / 3);
        connectionStage.addActor(nameField);


        Gdx.input.setInputProcessor(connectionStage);
    }

    private void getNewReturnToSpashButton() {
        returnToSpashButton = new TextButton("Back to Home", new Skin(Gdx.files.internal("uiskin.json")));
        returnToSpashButton.getLabel().setFontScale(TEXTSCALE);
        returnToSpashButton.setSize(350,64);
        returnToSpashButton.setPosition(GameController.VIRTUAL_WIDTH/2 - returnToSpashButton.getWidth()/2,
                returnToSpashButton.getHeight()/2);

    }

    private void returnToSpashscreenSimple() {
        gameAdapter.returnToMainMenu();
    }

    private void returnToSpashscreenDisconnect() {
        networkClient.sendDisconnectRequest();
    }

    public void updateJoinedPlayers(java.util.List<String> joinedPlayers) {
        StringBuilder s = new StringBuilder("Joined Players:\n");
        joinedPlayers.forEach(player -> s.append(player).append("\n"));
        joinedPlayersLabel.setText(s.toString());
    }

    private void addPlayerList() {
        // Display the joined players
        connectionStage.addActor(joinedPlayersLabel);
    }
    
    private void joinGame(String ip, PlayerInfo info) {
        networkClient = NetworkUtils.initNetworkObjects(false, ip, 42069, gameAdapter, this, info);
        if (networkClient == null) {
            // Something was wrong in the input
            Gdx.app.debug("pjb3 - ConnectionScreen", "Ruh roh. Something is wrong, with the IP probably");
        } else {
            switchToWater();
            addPlayerList();
            Gdx.app.log("pjb3", "Finished making the water and the player liast");
            hostGameButton.remove();
            joinGameButton.remove();
            ipField.setDisabled(true);
            connectionStage.addActor(waitJoinMsg);
        }
    }

    private void hostGame() {
        hosting = true;
        switchToWater();
        hostGameButton.remove();
        joinGameButton.remove();
        ipField.remove();
        String name = nameField.getText();
        nameField.remove();
        
        Label label1 = new Label(name + ", your IP is " + NetworkUtils.getLocalHostAddr(), labelStyle);
        label1.setFontScale(TEXTSCALE);
        label1.setSize(200, 64);
        label1.setPosition(GameController.VIRTUAL_WIDTH/2 - label1.getWidth()/2, GameController.VIRTUAL_HEIGHT*2/3 - hostGameButton.getHeight() + 64 );
        label1.setAlignment(Align.center);

        // Shift the joinedplayerslabel down a bit since the host has an extra button
        joinedPlayersLabel.setPosition(
                    GameController.VIRTUAL_WIDTH / 2 - joinedPlayersLabel.getWidth() / 2,
                    GameController.VIRTUAL_HEIGHT * 2 / 3 - 240);

        connectionStage.addActor(label1);
        addPlayerList();

        networkClient = NetworkUtils.initNetworkObjects(true, null, 42069,
                gameAdapter, this, new PlayerInfo(name));

        connectionStage.addActor(launchGameButton);
        connectionStage.addActor(returnToSpashButton);
    }

    private void switchToWater() {
        Gdx.app.log("pjb3", "switching to water");
        Pixmap waterPix = new Pixmap(Gdx.files.internal("waterdark.png"));
        Pixmap waterPixScaled = new Pixmap((int)GameController.VIRTUAL_WIDTH, (int)GameController.VIRTUAL_HEIGHT, waterPix.getFormat());
        waterPixScaled.drawPixmap(waterPix,
                0, 0, waterPix.getWidth(), waterPix.getHeight(),
                0, 0, waterPixScaled.getWidth(), waterPixScaled.getHeight()
        );
        Texture tex = new Texture(waterPixScaled);
        waterPix.dispose();
        waterPixScaled.dispose();
        TextureRegionDrawable reg = new TextureRegionDrawable(tex);
        returnToSpashButton.remove();
        connectionStage.addActor(new Image(reg));
        getNewReturnToSpashButton();
        returnToSpashButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                returnToSpashscreenDisconnect();
            }
        });
        connectionStage.addActor(colorSelectBox);
        connectionStage.addActor(returnToSpashButton);
    }

    private ClickListener progressToLoadoutsListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!hosting) {
                    Gdx.app.log("pjb3 - ConnectionScreen", "No. You must be host to move the game onward. How did you even click this");
                    return;
                }
                networkClient.sendGoToLoadout(); // Send the request for everyone to move to the loadout screen.
            }
        };

    /*
     * This is when a player makes a change in their faction, it will send this along to the host to be processed.
     */
    private void submitColor(String factionName) {
        if (factionName.equals(selectFaction)) {
            // Do not display the faction as "Select new faction" in the joined players area
            networkClient.sendFactionSelection("Factionless");
        } else {
            networkClient.sendFactionSelection(factionName);
        }
    }

    /*
     * This toggles the progress to loadouts button on the host. Set when players have chosen a unique faction.
     */
    public void setReadyToGoToLoadouts(boolean isReady) {
        if (hosting) {
            if (isReady) {
                launchGameButton.setText("Create game with connected players!");
                launchGameButton.setTouchable(Touchable.enabled);
                launchGameButton.setStyle(blueStyle);
                launchGameButton.addListener(progressToLoadoutsListener);
            } else {
                launchGameButton.setText("Waiting for all players to choose unique factions...");
                launchGameButton.setTouchable(Touchable.disabled);
                launchGameButton.setStyle(disabledStyle);
                launchGameButton.removeListener(progressToLoadoutsListener);
            }
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        connectionStage.act();
        connectionStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        connectionStage.getViewport().update(width, height);
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
        connectionStage.dispose();
    }
}
