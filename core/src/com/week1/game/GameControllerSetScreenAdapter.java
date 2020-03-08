package com.week1.game;

import com.badlogic.gdx.Screen;

/**
 * This class is an adapter to setting the screen of the overall GameController. This way,
 * we do not have to pass around the GameScreen everywhere we can just pass around an adapter
 * that things can call to switch the screen to a new screen.
 */
public interface GameControllerSetScreenAdapter {
    void setScreen(Screen newScreen);
    Screen getScreen();
}
