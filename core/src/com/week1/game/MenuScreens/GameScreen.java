package com.week1.game.MenuScreens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.Model.*;
import com.week1.game.GameController;
import com.week1.game.InfoUtil;
import com.week1.game.Model.Entities.Clickable;
import com.week1.game.Model.Entities.Unit;
import com.week1.game.Networking.INetworkClientToEngineAdapter;
import com.week1.game.Networking.Messages.AMessage;
import com.week1.game.Networking.Messages.Game.GameMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.NetworkObjects.Client;
import com.week1.game.Renderer.*;
import com.week1.game.TowerBuilder.TowerPresets;
import org.apache.commons.cli.CommandLine;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This is the Screen that holds the actual game that is being played.
 */
public class GameScreen implements Screen {
	public static float THRESHOLD = .2f;
	private Client networkClient;
	private GameEngine engine;
	private Renderer renderer;
	private ClickOracle clickOracle;
	private InfoUtil util;
	//This is a temporary stage that is displayed before connection of clients
	private Stage gameStage;
	private boolean pressedStartbtn;
	private boolean createdTextures;

	public GameScreen(Client givenNetworkClient) {
		// Set the logging level
		Gdx.app.setLogLevel(Application.LOG_INFO);
		Initializer.init();
		gameStage = new Stage(new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));
		util = new InfoUtil(true);
		// Finish setting up the client.
		this.networkClient = givenNetworkClient;

		//TODO actually pass the towers.
		networkClient.addAdapter( new INetworkClientToEngineAdapter() {
			@Override
			public void deliverUpdate(List<? extends GameMessage> messages) {
				engine.receiveMessages(messages);
			}
		});
		engine = new GameEngine(new IEngineAdapter() {
			@Override
			public void setDefaultLocation(Vector3 location) {
				renderer.setDefaultPosition(location);
				renderer.setCameraToDefaultPosition();
			}

			@Override
			public void endGame(int winOrLoss) {
				renderer.endGame(winOrLoss);
			}

			@Override
			public void gameOver() {
				renderer.showGameOver();
			}

			@Override
			public void sendMessage(AMessage msg) {
				networkClient.sendStringMessage(MessageFormatter.packageMessage(msg));
			}
		}, networkClient.getPlayerId(), new ConcurrentLinkedQueue<>(), util);

		renderer = new Renderer(new IRendererAdapter() {
			@Override
			public void renderSystem(RenderConfig renderConfig) {
				engine.render(renderConfig);
				clickOracle.render();
			}

			public double getPlayerMana(int playerId) {
				return engine.getGameState().getPlayerStats(playerId).getMana();
			}

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

			@Override
			public void setSelectedSpawnState(SpawnInfo type) {
				clickOracle.setSpawnType(type);
			}

			@Override
			public void restartGame() {
				Gdx.app.log("pjb3 - GameScreen", "TODO restart not implemented");
			}
		}, util);
		clickOracle = new ClickOracle(
				new IClickOracleAdapter() {

					@Override
					public void setTranslationDirection(Direction direction) {
						renderer.setPanning(direction);
					}

					public Camera getCamera() {
						return renderer.getCamera();
					}

					@Override
					public Clickable selectClickable(float screenX, float screenY, Vector3 intersection) {
                      return engine.getGameState().getClickableOnRay(renderer.getCam().getPickRay(screenX, screenY), intersection);
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
					public int getGameStateHash() {
						return engine.getGameStateHash();
					}

					@Override
					public String getGameStateString() {
						return engine.getGameStateString();
					}

					@Override
					public void sendMessage(AMessage msg) {
						networkClient.sendStringMessage(MessageFormatter.packageMessage(msg));
					}
					@Override
					public int getPlayerId() {
						return networkClient.getPlayerId();
					}
				});

		renderer.create();
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		if (!engine.started()) {
			gameStage.draw();
			return;
		}

		if (!pressedStartbtn) {
			InputMultiplexer multiplexer = new InputMultiplexer();
			multiplexer.addProcessor(renderer.getButtonStage());
			multiplexer.addProcessor(clickOracle);
			multiplexer.addProcessor(new GameCameraController(renderer.getCamera()));
			Gdx.input.setInputProcessor(multiplexer);

			gameStage.dispose();
			pressedStartbtn = true;
		}
		float time = Gdx.graphics.getDeltaTime();
		renderer.render(time); // Only move the units from their state position
	}

	@Override
	public void resize(int width, int height) {
		renderer.resize(width, height);
		if (!engine.started()) {
			gameStage.getViewport().update(width, height);
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

