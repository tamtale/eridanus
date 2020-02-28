package com.week1.game.ConnectionPage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.GameController;
import com.week1.game.LoadoutPage.LoadoutScreen;
import com.week1.game.Networking.NetworkObjects.Tcp.TcpClient;
import com.week1.game.Networking.NetworkObjects.Tcp.TcpNetworkUtils;

public class ConnectionScreen implements Screen {
    private Stage connectionStage;
    private TcpClient networkClient;
//        private boolean sentTowers = false;
    private GameController game;
    private boolean hosting;
    TextButton hostGameButton, joinGameButton;
    TextField ipField;


    public ConnectionScreen(GameController game) {
        this.game = game;
        Gdx.app.log("pjb3 - LoadoutScreen.java", "creating Loadout Screen. In contructor");
        connectionStage = new Stage(new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));

        hostGameButton= new TextButton("Begin Hosting", new Skin(Gdx.files.internal("uiskin.json")));
        hostGameButton.setSize(200,64);
        hostGameButton.setPosition(GameController.VIRTUAL_WIDTH/2 - hostGameButton.getWidth(), GameController.VIRTUAL_HEIGHT/2 - hostGameButton.getHeight());
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


        joinGameButton = new TextButton("JoinGame", new Skin(Gdx.files.internal("uiskin.json")));
        joinGameButton.setSize(200,64);
        joinGameButton.setPosition(
                GameController.VIRTUAL_WIDTH / 2 - joinGameButton.getWidth(),
                GameController.VIRTUAL_HEIGHT / 2 - joinGameButton.getHeight());
        connectionStage.addActor(joinGameButton);

        joinGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                joinGame(ipField.getMessageText());
            }
        });
    }

    private void joinGame(String ip) {
        hosting = false;

        networkClient = TcpNetworkUtils.initNetworkObjects(false, ip, 42069);
        if (networkClient == null) {
            // Something was wrong in the input
            Gdx.app.log("pjb3 - ConnectionScreen", "Ruh roh. Something is wrong, with the IP probably");
        } else {
            hostGameButton.remove();
            joinGameButton.remove();
        }
    }

    private void hostGame() {
        hosting = true;
        hostGameButton.remove();
        joinGameButton.remove();
        networkClient = TcpNetworkUtils.initNetworkObjects(true, null, 42069);
    }

    private void progressToLoadouts() {
        if (!hosting) {
            Gdx.app.log("pjb3 - ConnectionScreen", "no. You must be host to move the game onward.");
            return;
        }
        game.setScreen(new LoadoutScreen(game, networkClient));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

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
