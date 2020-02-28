package com.week1.game.MenuScreens;

import com.badlogic.gdx.Screen;
import com.week1.game.GameControllerSetScreenAdapter;
import com.week1.game.GameScreen;

/**
 * This class is passed around and helps unify the changing of screens during menu progressions.
 */
public class ScreenManager {
    private GameControllerSetScreenAdapter gameControllerSetScreenAdapter;
    private boolean isHostScreenManager = false;
    private GameScreen actualGame;

    public ScreenManager(GameControllerSetScreenAdapter gameControllerSetScreenAdapter) {
        this.gameControllerSetScreenAdapter = gameControllerSetScreenAdapter;
    }

    public void setScreen(Screen screen) {
        gameControllerSetScreenAdapter.setScreen(screen);
    }

    public void setActualGame(GameScreen actualGame) {
        this.actualGame = actualGame;
    }

    public void setIsHost(boolean isHost) {
        isHostScreenManager = isHost;
    }

    public void createNewLoadoutScreen() {

    }

    public void sendGoToLoadout() {

    }
}
