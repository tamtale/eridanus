package com.week1.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

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

	@Override
	public void create () {
	    map = new TmxMapLoader().load("testmap.tmx");
	    camera = new OrthographicCamera();
	    renderer = new OrthogonalTiledMapRenderer(map, 1f / SCALE);
	    // float w = Gdx.graphics.getWidth();
		// float h = Gdx.graphics.getHeight();
		camera.setToOrtho(false, 256, 256);
		camera.update();
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
					float deltaX = touchPos.x - selected.x;
					float deltaY = touchPos.y - selected.y;
					double angle = Math.atan(deltaY / deltaX);
					if (deltaX < 0) {
						angle += Math.PI;
					} else if (deltaY < 0) {
						angle += 2 * Math.PI;
					}
                    selected.vx = (float) SPEED * (float) Math.cos(angle);
					selected.vy = (float) SPEED * (float) Math.sin(angle);
					selected.goal = new Vector3(touchPos.x, touchPos.y, 0);
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
		units = new Array<>();
		units.add(new Unit(0, 0, 0, 0));
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
		return unit;
	}

	public void step(float delta) {
		for (Unit unit: units) {
		    unit.step(delta);
		}
	}

	@Override
	public void render () {
	    float diff = Gdx.graphics.getDeltaTime();
	    step(diff);
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

class Unit extends Rectangle {
	public float vx;
	public float vy;
	public boolean clicked;
	public Vector3 goal;
	public Unit(float x, float y, float vx, float dy) {
		super(x, y, Week1Demo.SCALE, Week1Demo.SCALE);
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = dy;
		this.clicked = false;
	}

	public void step(float delta) {
		if (goal != null) {
			if (Math.sqrt(Math.pow(x + width / 2 - goal.x, 2) + Math.pow(y + height / 2 - goal.y, 2)) < width / 2) {
			    goal = null;
			    return;
			}
			x += vx * delta;
			y += vy * delta;
		}
	}
}
