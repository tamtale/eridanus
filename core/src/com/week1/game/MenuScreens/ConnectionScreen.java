package com.week1.game.MenuScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.GameController;
import com.week1.game.GameControllerSetScreenAdapter;
import com.week1.game.Networking.NetworkObjects.Client;
import com.week1.game.Networking.NetworkObjects.NetworkUtils;

/**
 * This is the Screen where people chose to host or to join someone who is already hosting.
 * It is preceded by the MainMenu and followed by the LoadOut Screen.
 */
public class ConnectionScreen implements Screen {
    private Stage connectionStage;
    private Client networkClient;
    private GameControllerSetScreenAdapter gameAdapter;
    private boolean hosting;
    TextButton hostGameButton, joinGameButton, launchGameButton;
    Label waitJoinMsg;
    TextField ipField;
    Label.LabelStyle labelStyle;

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


        hostGameButton= new TextButton("Begin Hosting", new Skin(Gdx.files.internal("uiskin.json")));
        hostGameButton.setSize(200,64);
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
        joinGameButton = new TextButton("JoinGame", new Skin(Gdx.files.internal("uiskin.json")));
        joinGameButton.setSize(200,64);
        joinGameButton.setPosition(GameController.VIRTUAL_WIDTH / 2 + 20 ,
                GameController.VIRTUAL_HEIGHT / 2 - joinGameButton.getHeight());
        connectionStage.addActor(joinGameButton);
        joinGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                joinGame(ipField.getText());
            }
        });

        launchGameButton = new TextButton("Press when done waiting for players", new Skin(Gdx.files.internal("uiskin.json")));
        launchGameButton.setSize(300,64);
        launchGameButton.setPosition(
                GameController.VIRTUAL_WIDTH / 2 - launchGameButton.getWidth(),
                GameController.VIRTUAL_HEIGHT / 2 - 80);
        launchGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                progressToLoadouts();
            }
        });


        // Make the font for the title
        labelStyle = new Label.LabelStyle();
        BitmapFont myFont = new BitmapFont();
        labelStyle.font = myFont;
        labelStyle.fontColor = Color.WHITE;

        waitJoinMsg = new Label("Waiting for all players to join and for the host to start...", labelStyle);
        waitJoinMsg.setSize(300,64);
        waitJoinMsg.setPosition(GameController.VIRTUAL_WIDTH / 2 - waitJoinMsg.getWidth(), GameController.VIRTUAL_HEIGHT / 2 - 80);

        ipField = new TextField("10.122.178.55", new Skin(Gdx.files.internal("uiskin.json")));
        ipField.setSize(200,64);
        ipField.setPosition(GameController.VIRTUAL_WIDTH / 2 + 20 ,GameController.VIRTUAL_HEIGHT / 2);
        connectionStage.addActor(ipField);


        Label label1 = new Label("Connection Stage. Choose Host OR Join", labelStyle);
        label1.setSize(200, 64);
        label1.setPosition(GameController.VIRTUAL_WIDTH / 2 - 60,GameController.VIRTUAL_HEIGHT * 3 / 4 );
        label1.setAlignment(Align.center);
        connectionStage.addActor(label1);

        Gdx.input.setInputProcessor(connectionStage);
    }

    private void joinGame(String ip) {
        networkClient = NetworkUtils.initNetworkObjects(false, ip, 42069, gameAdapter);
        if (networkClient == null) {
            // Something was wrong in the input
            Gdx.app.log("pjb3 - ConnectionScreen", "Ruh roh. Something is wrong, with the IP probably");
        } else {
            switchToWater();
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
        Label label1 = new Label("Your Ip is " + NetworkUtils.getLocalHostAddr(), labelStyle);
        label1.setSize(200, 64);
        label1.setPosition(GameController.VIRTUAL_WIDTH/2 - 20 - hostGameButton.getWidth(), GameController.VIRTUAL_HEIGHT/2 - hostGameButton.getHeight() + 64 );
        label1.setAlignment(Align.center);
        connectionStage.addActor(label1);
//        10.122.178.55
        networkClient = NetworkUtils.initNetworkObjects(true, null, 42069, gameAdapter);
//        Gdx.app.log("pjb3 - ConnectionScreen", "Created the Host network object");
        connectionStage.addActor(launchGameButton);

    }

    private void switchToWater() {
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
        connectionStage.addActor(new Image(reg));
    }

    private void progressToLoadouts() {
        if (!hosting) {
            Gdx.app.log("pjb3 - ConnectionScreen", "No. You must be host to move the game onward. How did you even click this");
            return;
        }
        networkClient.sendGoToLoadout(); // Send the request for everyone to move to the loadout screen.
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
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
