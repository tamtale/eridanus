package com.week1.game.ConnectionPage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.GameController;
import com.week1.game.GameScreen;
import com.week1.game.Networking.NetworkObjects.Tcp.TcpClient;
import com.week1.game.TowerBuilder.TowerPresets;

import java.util.Arrays;

public class ConnectionScreen implements Screen {
        private Stage connectionStage;
//        private TcpClient networkClient;
//        private boolean sentTowers = false;
        private GameController game;

        public ConnectionScreen(GameController game) {
            this.game = game;
            Gdx.app.log("pjb3 - LoadoutScreen.java", "creating Loadout Screen. In contructor");
            connectionStage = new Stage(new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));

            TextButton hostGameButton = new TextButton("Begin Hosting", new Skin(Gdx.files.internal("uiskin.json")));
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
                    startNewGame();
                    networkClient.sendStartMessage();
                }
            });


            TextButton loadoutSelector = new TextButton("Confirm Your Loadout!", new Skin(Gdx.files.internal("uiskin.json")));
            loadoutSelector.setSize(200,64);
            loadoutSelector.setPosition(
                    GameController.VIRTUAL_WIDTH / 2 - loadoutSelector.getWidth(),
                    GameController.VIRTUAL_HEIGHT / 2 - loadoutSelector.getHeight());
            connectionStage.addActor(loadoutSelector);

            loadoutSelector.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    sendTowerChoices(Arrays.asList(
                            TowerPresets.getTower(1).getLayout(),
                            TowerPresets.getTower(3).getLayout(),
                            TowerPresets.getTower(5).getLayout()));
                }
            });
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
