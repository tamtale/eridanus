package com.week1.game.ConnectionPage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.GameController;
import com.week1.game.Networking.NetworkObjects.Tcp.TcpClient;
import com.week1.game.Networking.NetworkObjects.Tcp.TcpNetworkUtils;

public class ConnectionScreen implements Screen {
    private Stage connectionStage;
    private TcpClient networkClient;
    private GameController game;
    private boolean hosting;
    TextButton hostGameButton, joinGameButton, launchGameButton;
    Label waitJoinMsg;
    TextField ipField;
    Label.LabelStyle labelStyle;



    public ConnectionScreen(GameController game) {
        this.game = game;
        Gdx.app.log("pjb3 - LoadoutScreen.java", "creating Loadout Screen. In contructor");
        connectionStage = new Stage(new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));

        hostGameButton= new TextButton("Begin Hosting", new Skin(Gdx.files.internal("uiskin.json")));
        hostGameButton.setSize(200,64);
        hostGameButton.setPosition(GameController.VIRTUAL_WIDTH/2 - 20 - hostGameButton.getWidth(), GameController.VIRTUAL_HEIGHT/2 - hostGameButton.getHeight());
        connectionStage.addActor(hostGameButton);
        hostGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                /*
                TODO MUST block here until all people have selected their towers.
                    Ideas: Since we know how many players there are, the host can send out a 'good to go' message when all towers are in.
                */
                hostGame();
            }
        });


        // Make the joinGameButton
        joinGameButton = new TextButton("JoinGame", new Skin(Gdx.files.internal("uiskin.json")));
        joinGameButton.setSize(200,64);
        joinGameButton.setPosition(GameController.VIRTUAL_WIDTH / 2 + 20 ,GameController.VIRTUAL_HEIGHT / 2 + joinGameButton.getHeight());
        connectionStage.addActor(joinGameButton);
        joinGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                joinGame(ipField.getMessageText());
            }
        });

        launchGameButton = new TextButton("Press when done waiting for players", new Skin(Gdx.files.internal("uiskin.json")));
        launchGameButton.setSize(200,64);
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
        ipField.setPosition(GameController.VIRTUAL_WIDTH / 2 + ipField.getWidth(), GameController.VIRTUAL_HEIGHT * 3 / 4 - 80);
        connectionStage.addActor(ipField);


        Label label1 = new Label("Connection Stage. Choose Host OR Join", labelStyle);
        label1.setSize(200, 64);
        label1.setPosition(GameController.VIRTUAL_WIDTH / 2 - 60,GameController.VIRTUAL_HEIGHT * 3 / 4 );
        label1.setAlignment(Align.center);
        connectionStage.addActor(label1);

        Gdx.input.setInputProcessor(connectionStage);
    }

    private void joinGame(String ip) {
        networkClient = TcpNetworkUtils.initNetworkObjects(false, ip, 42069, newScreen -> game.setScreen(newScreen));
        if (networkClient == null) {
            // Something was wrong in the input
            Gdx.app.log("pjb3 - ConnectionScreen", "Ruh roh. Something is wrong, with the IP probably");
        } else {
            hostGameButton.remove();
            joinGameButton.remove();
            connectionStage.addActor(waitJoinMsg);
        }
    }

    private void hostGame() {
        hosting = true;
        hostGameButton.remove();
        joinGameButton.remove();
        ipField.remove();
        Label label1 = new Label("Your Ip is " + TcpNetworkUtils.getLocalHostAddr(), labelStyle);
        label1.setSize(200, 64);
        label1.setPosition(GameController.VIRTUAL_WIDTH/2 - 20 - hostGameButton.getWidth(), GameController.VIRTUAL_HEIGHT/2 - hostGameButton.getHeight() + 64 );
        label1.setAlignment(Align.center);
        connectionStage.addActor(label1);
//        10.122.178.55
        networkClient = TcpNetworkUtils.initNetworkObjects(true, null, 42069, newScreen -> game.setScreen(newScreen));
        Gdx.app.log("pjb3 - ConnectionScreen", "Created the Host network object");
        connectionStage.addActor(launchGameButton);

    }

    private void progressToLoadouts() {
        if (!hosting) {
            Gdx.app.log("pjb3 - ConnectionScreen", "No. You must be host to move the game onward. How did you even click this");
            return;
        }
        Gdx.app.log("pjb3 - ConnectionScreen", "Trying to send the GoToLoadout command");
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

    }
}
