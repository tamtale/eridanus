package com.week1.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Model.*;
import com.week1.game.Networking.Client;
import com.week1.game.Networking.INetworkClientToEngineAdapter;
import com.week1.game.Networking.Messages.AMessage;
import com.week1.game.Networking.NetworkUtils;
import com.week1.game.Renderer.IRendererToEngineAdapter;
import com.week1.game.Renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    private static final String TAG = "Controller";
    private GameEngine engine;
    private ClickOracle clickOracle;
    private Client networkClient;
    private Renderer renderer;
    public Controller(String[] args) {
        networkClient = NetworkUtils.initNetworkObjects(args, new INetworkClientToEngineAdapter() {
            @Override
            public void deliverUpdate(List<? extends AMessage> messages) {
                Gdx.app.error(TAG, "deliverUpdate is unimplemented!");
                engine.deliverMessage(new ArrayList<>());
            } // TODO: implement this!
        });
        engine = new GameEngine(new IEngineToRendererAdapter() {
            @Override
            public void batchGame(Runnable drawRunnable) {
                renderer.startBatch();
                drawRunnable.run();
                renderer.endBatch();
            }

            @Override
            public void draw(Texture texture, float x, float y) {
                renderer.draw(texture, x, y);
            }
        });
        renderer = new Renderer(new IRendererToEngineAdapter() {
            @Override
            public void render() {
                engine.render();
            }
        });
        clickOracle = new ClickOracle(
                new IClickOracleToRendererAdapter() {
                    @Override
                    public void unproject(Vector3 projected) {

                    }
                },
                new IClickOracleToEngineAdapter() {
                    @Override
                    public Unit selectUnit(Vector3 position) {
                        return null;
                    }

                    @Override
                    public Unit spawn(Vector3 position) {
                        return null;
                    }
                });
    }
}
