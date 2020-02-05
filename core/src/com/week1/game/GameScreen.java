package com.week1.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.AIMovement.AI;
import com.week1.game.Model.*;
import com.week1.game.Networking.Client;
import com.week1.game.Networking.INetworkClientToEngineAdapter;
import com.week1.game.Networking.Messages.AMessage;
import com.week1.game.Networking.Messages.Game.GameMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.NetworkUtils;
import com.week1.game.Renderer.IRendererToEngineAdapter;
import com.week1.game.Renderer.IRendererToNetworkAdapter;
import com.week1.game.Renderer.Renderer;

import java.util.List;


public class GameScreen implements Screen {
	private static float THRESHOLD = .2f;
	public static int PIXELS_PER_UNIT = 64;
	private String[] args;
	private float curTime = 0f;
	private Client networkClient;
	private GameEngine engine;
	private Renderer renderer;
	private ClickOracle clickOracle;
	private AI ai;
	private InfoUtil util;

	
	public GameScreen(String[] args) {
		this.args = args;


		util = new InfoUtil(true);
		networkClient = NetworkUtils.initNetworkObjects(args, new INetworkClientToEngineAdapter() {
			@Override
			public void deliverUpdate(List<? extends GameMessage> messages) {
				engine.receiveMessages(messages);
			}

			@Override
			public void setPlayerId(int playerId) {
				engine.setEnginePlayerId(playerId);
			}
		});

		engine = new GameEngine(new IEngineToRendererAdapter() {
			@Override
			public void endGame(int winOrLoss) {
				renderer.endGame(winOrLoss);
			}
		}, util);

		renderer = new Renderer(new IRendererToEngineAdapter() {
			@Override
			public void render() {
				engine.render();
			}

			@Override
			public TiledMap getMap() {
				return engine.getGameState().getWorld().toTiledMap();
			}

			public double getPlayerMana(int playerId) {
				return engine.getGameState().getPlayerStats(playerId).getMana();
			}
		}, new IRendererToNetworkAdapter() {
			@Override
			public String getHostAddr() {
				return networkClient.getHostAddr();
			}

			@Override
			public int getPlayerId() {
				return networkClient.getPlayerId();
			}

			@Override
			public String getClientAddr() {
				return null;
			}
		},
		new IRendererToClickOracleAdapter() {
			@Override
			public void render() {
				clickOracle.render();
			}
		}, util);
		clickOracle = new ClickOracle(
				new IClickOracleToRendererAdapter() {
					@Override
					public void unproject(Vector3 projected) {
						renderer.getCamera().unproject(projected);
					}

					@Override
					public Camera getCamera() {
						return renderer.getCamera();
					}
				},
				new IClickOracleToEngineAdapter() {
					@Override
					public Unit selectUnit(Vector3 position) {
						return engine.getGameState().findUnit(position);
					}

					@Override
					public boolean isPlayerAlive() {
						return engine.isPlayerAlive();
					}
					
					@Override
					public Array<Unit> getUnitsInBox(Vector3 cornerA, Vector3 cornerB) {
						return engine.getGameState().findUnitsInBox(cornerA, cornerB);
					}

				},
				new IClickOracleToNetworkAdapter() {
					@Override
					public void sendMessage(AMessage msg) {
						networkClient.sendStringMessage(MessageFormatter.packageMessage(msg));
					}
					@Override
					public int getPlayerId() {
						return networkClient.getPlayerId();
					}
				});

		ai = new AI();
		Gdx.input.setInputProcessor(clickOracle);
		renderer.create();
		
		// Set the logging level
		Gdx.app.setLogLevel(Application.LOG_INFO);
	}



	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		if (!engine.started()) {
			renderer.renderInfo();
			return;
		}
		float time = Gdx.graphics.getDeltaTime();
		curTime += time;
		if (curTime > THRESHOLD) {
			curTime = 0;
			engine.processMessages();
		}
		engine.updateState(time);
		engine.getBatch().setProjectionMatrix(renderer.getCamera().combined); // necessary to use tilemap coordinate system
//		clickOracle.getBatch().setProjectionMatrix(renderer.getCamera().combined);
		renderer.render();
	}

	@Override
	public void resize(int width, int height) {
		renderer.resize();
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
	public void dispose () {

	}

}

