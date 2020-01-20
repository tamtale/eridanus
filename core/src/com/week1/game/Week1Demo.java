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
import com.week1.game.Model.GameState;
import com.week1.game.Model.Unit;
import com.week1.game.Networking.Client;
import com.week1.game.Networking.Host;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.week1.game.Networking.NetworkUtils.getLocalHostAddr;

public class Week1Demo extends ApplicationAdapter {
	public static int SCALE = 8; // 8 pixels per unit.
	public static int SPEED = 8;
	private Batch batch;
	private OrthographicCamera camera;
	private Array<Unit> units;
	private Pixmap unitPixmap;
	private Pixmap unitPixmap2;
	private Texture unitTexture;
	private Texture unitTexture2;
	private Vector3 touchPos = new Vector3();
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private Unit selected;
	private Array<SteeringAgent> agents;
	private GameState state;
	
	public Week1Demo (String[] args) {
	}

	@Override
	public void create () {
	    map = new TmxMapLoader().load("testmap.tmx");
	    camera = new OrthographicCamera();
	    renderer = new OrthogonalTiledMapRenderer(map, 1f / SCALE);
		camera.setToOrtho(false, 256, 256);
		camera.update();
		units = new Array<>();
		agents = new Array<>();
	    batch = renderer.getBatch();

		initUnits();
		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {

				touchPos.set(screenX, screenY, 0);
				camera.unproject(touchPos);

				if (button == Input.Buttons.LEFT) {
					for (Unit unit: units) {
						if (unit.contains(touchPos.x, touchPos.y)) {
						    select(unit);
							return true;
						}
					}
					select(spawn(touchPos.x, touchPos.y));
					System.out.println("spawned: " + touchPos);
					return true;
				}
				// Right click
                if (selected != null) {
                	final Vector2 vec = new Vector2(touchPos.x,touchPos.y);
					selected.agent.steeringBehavior = new Arrive<>(selected.agent, new Location<Vector2>() {
						@Override
						public Vector2 getPosition() {
							return vec;
						}

						@Override
						public float getOrientation() {
							return 0;
						}

						@Override
						public void setOrientation(float orientation) {

						}

						@Override
						public float vectorToAngle(Vector2 vector) {
							return (float)Math.atan2(-vector.x, -vector.y);
						}

						@Override
						public Vector2 angleToVector(Vector2 outVector, float angle) {
							outVector.x = -(float)Math.sin(angle);
							outVector.y = -(float)Math.cos(angle);
							return outVector;
						}

						@Override
						public Location<Vector2> newLocation() {
							return this;
						}
					}).setArrivalTolerance(0).setDecelerationRadius(50).setTimeToTarget(10);
//						int xSign = (selected.x < touchPos.x) ? 1 : -1;
//                		int ySign = (selected.y < touchPos.y) ? 1 : -1;
//						double angle = Math.atan((selected.y - touchPos.y) / (selected.x - touchPos.x));
//						float deltax = (float) SPEED * (float) Math.cos(angle);
//						float deltay = (float) SPEED * (float) Math.sin(angle);
//                		selected.agent.setSteeringOutputLinear(new Vector2(xSign * deltax, ySign * deltay));

					return true;

				} else {
                	return false;
				}
			}
		});

	}

	private void initUnits() {
		unitPixmap = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888);
		unitPixmap.setColor(Color.BLUE);
		unitPixmap.fill();

		unitPixmap2 = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888);
		unitPixmap2.setColor(Color.RED);
		unitPixmap2.fill();

		unitTexture = new Texture(unitPixmap);
		unitTexture2 = new Texture(unitPixmap2);
	}
	private void select(Unit unit) {
	    unselect();
		selected = unit;
		unit.clicked = true;
	}
	private void unselect() {
		if (selected != null) {
			selected.clicked = false;
		}
		selected = null;
	}

	private Unit spawn(float x, float y) {
		Unit unit = new Unit(x, y, 0, 0);
		units.add(unit);
		SteeringAgent agent = new SteeringAgent(unit, new Vector2(x, y), 0,
				new Vector2((float) .1, (float) .1), 0, 1, true, (float).5);
		this.agents.add(agent);
		unit.agent = agent;
		return unit;
	}

	public void step(float delta) {
		for(Unit unit: units) {
			//System.out.println("from step " + agent.getSteeringOutput().linear);
			unit.step(delta);
		}
	}

	@Override
	public void render () {
	    float diff = Gdx.graphics.getDeltaTime();
	    step(diff); //TODO in the new version make this gameState.step(diff)
		Gdx.gl.glClearColor(0, 1f, 1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		renderer.setView(camera);
		renderer.render();
		batch.begin();
		for (Unit unit: units) {
			if (unit.clicked) {
				batch.draw(unitTexture, unit.x, unit.y);
			} else {
				batch.draw(unitTexture2, unit.x, unit.y);
			}
		}
		batch.end();

	}

	@Override
	public void dispose () {
		batch.dispose();
	}

}

