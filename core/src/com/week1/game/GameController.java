package com.week1.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.AIMovement.AI;
import com.week1.game.Model.*;
import com.week1.game.Networking.Client;
import com.week1.game.Networking.INetworkClientToEngineAdapter;
import com.week1.game.Networking.Messages.AMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.NetworkUtils;
import com.week1.game.Renderer.IRendererToEngineAdapter;
import com.week1.game.Renderer.IRendererToNetworkAdapter;
import com.week1.game.Renderer.Renderer;

import java.util.List;
import java.util.UUID;


public class GameController extends ApplicationAdapter {
	private static float THRESHOLD = .2f;
	public static int SCALE = 8; // 8 pixels per unit.
	private String[] args;
	private float curTime = 0f;
	private Client networkClient;
	private GameEngine engine;
	private Renderer renderer;
	private ClickOracle clickOracle;
	private AI ai;

	
	public GameController(String[] args) {
		this.args = args;
	}

	@Override
	public void create () {
		networkClient = NetworkUtils.initNetworkObjects(args, new INetworkClientToEngineAdapter() {
			@Override
			public void deliverUpdate(List<? extends AMessage> messages) {
				engine.receiveMessages(messages);
			}
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
		}, new IEngineToAIAdapter() {
			@Override
			public void spawn(Unit unit) {
				ai.spawn(unit);
			}

			@Override
			public void spawnTower() {
				ai.spawnTower();
			}

			@Override
			public void updateTarget(Unit unit, Vector3 newTarget) {
				ai.updateTarget(unit, newTarget);
			}

			@Override
			public void buildMap() {
				ai.buildMap();
			}

			@Override
			public void update(float delta) {
				ai.update(delta);
			}
		});
		renderer = new Renderer(new IRendererToEngineAdapter() {
			@Override
			public void render() {
				engine.render();
			}
		}, new IRendererToNetworkAdapter() {
			@Override
			public String getHostAddr() {
				return networkClient.getHostAddr();
			}

			@Override
			public String getClientAddr() {
				return null;
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

				},
				new IClickOracleToNetworkAdapter() {
					@Override
					public void sendMessage(AMessage msg) {
						networkClient.sendStringMessage(MessageFormatter.packageMessage(msg));
					}
				});

		ai = new AI();
		Gdx.input.setInputProcessor(clickOracle);
		renderer.create();
	}


	@Override
	public void render () {
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
	public void dispose () {

	}

}

