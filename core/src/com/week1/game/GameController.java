package com.week1.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sun.org.apache.bcel.internal.classfile.Unknown;
import com.week1.game.Model.*;
import com.week1.game.Networking.Client;
import com.week1.game.Networking.Host;
import com.week1.game.Networking.INetworkClientToEngineAdapter;
import com.week1.game.Networking.Messages.AMessage;
import com.week1.game.Networking.NetworkUtils;
import com.week1.game.Renderer.IRendererToEngineAdapter;
import com.week1.game.Renderer.Renderer;

import java.util.List;


public class GameController extends ApplicationAdapter {
	public static int SCALE = 8; // 8 pixels per unit.
	private String[] args;
	private float curTime = 0f;
	private Client networkClient;
	private GameEngine engine;
	private Renderer renderer;
	private ClickOracle clickOracle;

	
	public GameController(String[] args) {
		this.args = args;
	}

	@Override
	public void create () {
		networkClient = NetworkUtils.initNetworkObjects(args, new INetworkClientToEngineAdapter() {
			@Override
			public void deliverUpdate(List<AMessage> messages) {
				engine.deliverMessage(messages);
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
						renderer.getCamera().unproject(projected);
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

		renderer.create();
	}


	@Override
	public void render () {
	    curTime += Gdx.graphics.getDeltaTime();
	    if (curTime > .2) {
	    	System.out.println("update: " + curTime * 1000 + "ms");
	    	curTime = 0;
		}
	    engine.render();
	}

	@Override
	public void dispose () {

	}

}

