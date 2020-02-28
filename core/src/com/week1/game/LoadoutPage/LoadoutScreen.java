package com.week1.game.LoadoutPage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.GameController;
import com.week1.game.GameControllerSetScreenAdapter;
import com.week1.game.GameScreen;
import com.week1.game.Networking.NetworkObjects.Tcp.TcpClient;
import com.week1.game.TowerBuilder.BlockSpec;
import com.week1.game.TowerBuilder.TowerPresets;

import java.util.Arrays;
import java.util.List;

public class LoadoutScreen implements Screen {
    private Stage loadoutStage;
    private TcpClient networkClient;
    private boolean sentTowers = false;
    private boolean isHostingClient;
    private GameControllerSetScreenAdapter gameAdapter;

    public LoadoutScreen(GameControllerSetScreenAdapter gameAdapter, TcpClient client, boolean isHostingClient) {
        this.gameAdapter = gameAdapter;
        this.networkClient = client;
        this.isHostingClient = isHostingClient;

        Gdx.app.log("pjb3 - LoadoutScreen.java", "creating Loadout Screen. In contructor");
        loadoutStage = new Stage(new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));

        if (isHostingClient) {
            TextButton startbtn = new TextButton("Launch Game! [ONLY FOR HOST]", new Skin(Gdx.files.internal("uiskin.json")));
            startbtn.setSize(200, 64);
            startbtn.setPosition(GameController.VIRTUAL_WIDTH / 2 - startbtn.getWidth(), GameController.VIRTUAL_HEIGHT / 2 - startbtn.getHeight());
            loadoutStage.addActor(startbtn);

            startbtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                /*
                TODO MUST block here until all people have selected their towers.
                    Ideas: Since we know how many players there are, the host can send out a 'good to go' message when all towers are in.
                */

                    Gdx.app.log("pjb3 LoadoutScreen", "About to send start message.");
                    networkClient.sendStartMessage();
                }
            });
        }


        TextButton loadoutSelector = new TextButton("Confirm Your Loadout!", new Skin(Gdx.files.internal("uiskin.json")));
        loadoutSelector.setSize(200,64);
        loadoutSelector.setPosition(
                GameController.VIRTUAL_WIDTH / 2 - loadoutSelector.getWidth(),
                GameController.VIRTUAL_HEIGHT / 2 - 80 - loadoutSelector.getHeight());
        loadoutStage.addActor(loadoutSelector);

        loadoutSelector.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sendTowerChoices(Arrays.asList(
                        TowerPresets.getTower(1).getLayout(),
                        TowerPresets.getTower(3).getLayout(),
                        TowerPresets.getTower(5).getLayout()));
            }
        });

        // Make the font for the title
        Label.LabelStyle label1Style = new Label.LabelStyle();
        BitmapFont myFont = new BitmapFont();
        label1Style.font = myFont;
        label1Style.fontColor = Color.WHITE;

        Label label1 = new Label("LOADOUT Stage. Currently nonfunctional",label1Style);
        label1.setSize(200, 64);
        label1.setPosition(GameController.VIRTUAL_WIDTH / 2 - 60,GameController.VIRTUAL_HEIGHT * 3 / 4 );
        label1.setAlignment(Align.center);
        loadoutStage.addActor(label1);

        createNewGame(); // MAKE the game but dont start it yet.
        Gdx.input.setInputProcessor(loadoutStage);
    }


    public void createNewGame() {
        Screen futureGame = new GameScreen(networkClient, gameAdapter);
        networkClient.setGameScreen(futureGame);
        Gdx.app.log("pjb3 - LoutoutScreen", "the GameScreen is being created NOW. It has been added to the client");
    }

    public void sendTowerChoices(List<List<BlockSpec>> details) {
        if (!sentTowers) {
            networkClient.sendTowersMessage( details);
            sentTowers = false;
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
//        Gdx.app.log("pjb3 - LoadoutScreen", "rendering");
        loadoutStage.draw();
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
