package com.week1.game.MenuScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MenuStyles {
    public static TextButton.TextButtonStyle normalStyle = new TextButton.TextButtonStyle(
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round"),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round"),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round"), new BitmapFont());

    public static TextButton.TextButtonStyle blueStyle = new TextButton.TextButtonStyle(
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round", Color.valueOf("add8e6")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.valueOf("add8e6")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.valueOf("add8e6")), new BitmapFont());

    public static TextButton.TextButtonStyle redStyle = new TextButton.TextButtonStyle(
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round", Color.RED),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round", Color.RED),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round", Color.RED), new BitmapFont());

    public static TextButton.TextButtonStyle pressedStyle = new TextButton.TextButtonStyle(
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.DARK_GRAY),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.DARK_GRAY),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.DARK_GRAY), new BitmapFont());


    public static TextButton.TextButtonStyle disabledStyle = new TextButton.TextButtonStyle(
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.BLACK),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.BLACK),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-down", Color.BLACK), new BitmapFont());

    public static ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle(new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-rect", Color.valueOf("9e8196")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-scroll", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-large", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-scroll", Color.valueOf("8e7186")),
            new Skin(Gdx.files.internal("uiskin.json")).newDrawable("default-round-large", Color.valueOf("8e7186")));
}
