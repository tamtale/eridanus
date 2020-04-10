package com.week1.game.MenuScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.GameController;
import com.week1.game.GameControllerSetScreenAdapter;
import com.week1.game.TowerBuilder.TowerBuilderScreen;

/**
 * Spashscreen of the game
 */
public class MainMenuScreen implements Screen {
    GameController game;
    public Stage stage;

    //Widgets
    TextButton playButton, buildTowersButton;
    Texture tex;

    public MainMenuScreen(GameController game) {
        this.game = game;
        stage = new Stage(new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT));
        setWidgets();
        configureWidgets();
        setListeners();

        Gdx.input.setInputProcessor(stage);

    }


    private void setWidgets() {
        buildTowersButton = new TextButton("Build Towers", new Skin(Gdx.files.internal("uiskin.json")));
        playButton = new TextButton( "Play Game!", new Skin(Gdx.files.internal("uiskin.json")));

    }

    private void configureWidgets() {

        Pixmap backroundPix = new Pixmap(Gdx.files.internal("menu2.png"));
        Pixmap backgroundPixScaled = new Pixmap((int)GameController.VIRTUAL_WIDTH, (int)GameController.VIRTUAL_HEIGHT, backroundPix.getFormat());
        backgroundPixScaled.drawPixmap(backroundPix,
                0, 0, backroundPix.getWidth(), backroundPix.getHeight(),
                0, 0, backgroundPixScaled.getWidth(), backgroundPixScaled.getHeight()
        );
        tex = new Texture(backgroundPixScaled);
        backroundPix.dispose();
        backgroundPixScaled.dispose();

        TextureRegionDrawable reg = new TextureRegionDrawable(tex);
        stage.addActor(new Image(reg));

        buildTowersButton.setSize(200,64);
        buildTowersButton.getLabel().setFontScale(1.5f);
        buildTowersButton.setPosition(GameController.VIRTUAL_WIDTH/2 - buildTowersButton.getWidth() - 50, 24);
        stage.addActor(buildTowersButton);

        playButton.setSize(200,64);
        playButton.getLabel().setFontScale(1.5f);
        playButton.setPosition(GameController.VIRTUAL_WIDTH/2 + 50, 24);
        stage.addActor(playButton);
    }

    private void setListeners() {
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ConnectionScreen(new GameControllerSetScreenAdapter() {
                    @Override
                    public void setScreen(Screen newScreen) {
                        game.setScreen(newScreen);
                    }

                    @Override
                    public void returnToMainMenu() {
                        game.returnToMainMenu();
                    }

                    @Override
                    public Screen getScreen() {
                        return game.getScreen();
                    }
                }));
            }
        });

        buildTowersButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new TowerBuilderScreen(game));
            }
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
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
    public void dispose() {
        stage.dispose();
    }
}
