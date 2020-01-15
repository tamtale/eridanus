package com.week1.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Week1Demo extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	private Viewport viewport;
	private OrthographicCamera camera;
	private Rectangle base1;
	private Rectangle base2;
	private Array<Rectangle> units;
	private Pixmap unitPixmap;
	private Texture unitTexture;
	private Sprite unitSprite;
	private Pixmap basePixmap;
	private Sprite baseSprite;
	Vector3 touchPos = new Vector3();

	@Override
	public void create () {
		units = new Array<>();
	    batch = new SpriteBatch();

	    basePixmap = new Pixmap(64, 64, Pixmap.Format.RGB888);
	    basePixmap.setColor(Color.RED);
	    basePixmap.fill();
	    img = new Texture(basePixmap);
	    basePixmap.dispose();
	    baseSprite = new Sprite(img);
	    base1 = new Rectangle();
	    base2 = new Rectangle();
	    base1.x = 0;
	    base1.y = Gdx.graphics.getHeight() - 64;
		base2.x = Gdx.graphics.getWidth() - 64;
		base2.y = 0;

		unitPixmap = new Pixmap(32, 32, Pixmap.Format.RGB888);
		unitPixmap.setColor(Color.BLUE);
		unitPixmap.fill();
		unitTexture = new Texture(unitPixmap);
		unitSprite = new Sprite(unitTexture);

		units.add(new Rectangle(100, 100, 32, 32));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 1f, 1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		baseSprite.setPosition(base1.x, base1.y);
		baseSprite.draw(batch);
		baseSprite.setPosition(base2.x, base2.y);
		baseSprite.draw(batch);
		for (Rectangle unit: units) {
			unitSprite.setPosition(unit.x, unit.y);
			unitSprite.draw(batch);
		}
		batch.end();

		if (Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}

class Unit {
	private int dx;
	private int dy;
}

