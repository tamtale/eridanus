package com.week1.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
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
import java.util.UUID;


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

	
	public GameScreen(String[] args) {
		this.args = args;


		networkClient = NetworkUtils.initNetworkObjects(args, new INetworkClientToEngineAdapter() {
			@Override
			public void deliverUpdate(List<? extends GameMessage> messages) {
				engine.receiveMessages(messages);
			}

			// The client should Not! make calls to the engine that perform drawing operations
//			@Override
//			public void notifyNumPlayers(int numPlayers) {
//				engine.setNumPlayers(numPlayers);
//			}
		});
		engine = new GameEngine(new IEngineToRendererAdapter() {
			@Override
			public void batchDrawOperations(Runnable drawRunnable) {
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

			@Override
			public TiledMap getMap() {
				return engine.getGameState().getWorld().toTiledMap();
			}

			public float getPlayerMana(int playerId) {
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
						renderer.changeAlpha(0.5f);
						renderer.startBatch();
						clickOracle.render(renderer::draw);
						renderer.endBatch();
						renderer.changeAlpha(1);
					}
				});
		clickOracle = new ClickOracle(
				new IClickOracleToRendererAdapter() {
					@Override
					public void unproject(Vector3 projected) {
						renderer.getCamera().unproject(projected);
					}
				},
				new IClickOracleToEngineAdapter() {
					@Override
					public Unit selectUnit(Vector3 position) {
						return engine.getGameState().findUnit(position);
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
		renderer.render();
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
	public void dispose () {

	}

}

