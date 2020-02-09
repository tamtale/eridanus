package com.week1.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.AIMovement.AI;
import com.week1.game.Model.*;
import com.week1.game.Model.Entities.Building;
import com.week1.game.Model.Entities.Unit;
import com.week1.game.Networking.Client;
import com.week1.game.Networking.INetworkClientToEngineAdapter;
import com.week1.game.Networking.Messages.AMessage;
import com.week1.game.Networking.Messages.Game.GameMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.NetworkUtils;
import com.week1.game.Renderer.*;

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
	//This is a temporary stage that is displayed before connection of clients
	private Stage connectionStage;
	private boolean pressedStartbtn = false;

	private void makeTempStage() {
		connectionStage = new Stage(new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));

		TextButton startbtn = new TextButton("Send Start Message", new Skin(Gdx.files.internal("uiskin.json")));
		startbtn.setSize(200,64);
		startbtn.setPosition(GameController.VIRTUAL_WIDTH/2 - startbtn.getWidth(), GameController.VIRTUAL_HEIGHT/2 - startbtn.getHeight());
		connectionStage.addActor(startbtn);

		startbtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				networkClient.sendStartMessage();
//				Gdx.input.setInputProcessor(clickOracle);
//				connectionStage.dispose();
			}
		});
	}

	public GameScreen(String[] args) {
		this.args = args;
		// Set the logging level
		Gdx.app.setLogLevel(Application.LOG_INFO);



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

			@Override
			public void setSelectedSpawnState(SpawnInfo type) {
				clickOracle.setSpawnType(type);
			}
		}, util);
		clickOracle = new ClickOracle(
				new IClickOracleToRendererAdapter() {
					@Override
					public void unproject(Vector3 projected) {
						renderer.getCamera().unproject(projected);
					}

					@Override
					public void zoom(int amount) {
					    renderer.zoom(amount);
					}

                    @Override
                    public void setTranslationDirection(Direction direction) {
					    renderer.setPanning(direction);
                    }

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

					@Override
					public Array<Building> getBuildings() {
						return engine.getBuildings();
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
		makeTempStage();
		Gdx.input.setInputProcessor(connectionStage);

		renderer.create();
	}



	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		if (!engine.started()) {
			connectionStage.draw();
//			renderer.renderInfo();
			return;
		}

		if (!pressedStartbtn) {
			InputMultiplexer multiplexer = new InputMultiplexer();
			multiplexer.addProcessor(renderer.getButtonStage());
			multiplexer.addProcessor(clickOracle);
			Gdx.input.setInputProcessor(multiplexer);

			connectionStage.dispose();
			pressedStartbtn = true;
		}


		float time = Gdx.graphics.getDeltaTime();
		curTime += time;
		if (curTime > THRESHOLD) {
			curTime = 0;
			engine.processMessages();
		}
		engine.updateState(time);
		engine.getBatch().setProjectionMatrix(renderer.getCamera().combined); // necessary to use tilemap coordinate system
		renderer.render();
	}

	@Override
	public void resize(int width, int height) {
		renderer.resize(width, height);
		if (!engine.started()) {
			connectionStage.getViewport().update(width, height);
		}
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

