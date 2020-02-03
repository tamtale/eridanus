package com.week1.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;

public class InfoUtil {

    private boolean messagesOnScreen;
    private Array<DisplayMessage> messages = new Array<>();
    private Array<DisplayMessage> toDelete = new Array<>();

    public InfoUtil(boolean messagesOnScreen) {
        this.messagesOnScreen = messagesOnScreen;
    }

    public void log(String tag, String message) {
        Gdx.app.log(tag, message);
        if (messagesOnScreen) {
            messages.add(new DisplayMessage(message));
        }
    }

    public void drawMessages(Batch batch, BitmapFont font) {
        batch.begin();
        font.setColor(Color.ORANGE);
        font.getData().setScale(.25f);
        for (int i = 0; i < messages.size; i++) {
            DisplayMessage message = messages.get(i);
            font.draw(batch, message.text, 5f, 100 - (float) i * 3.0f);
            message.decay();
            if (message.finished()) {
                toDelete.add(message);
            }
        }
        batch.end();
        for (DisplayMessage message: toDelete) {
            messages.removeValue(message, true);
        }
        toDelete.clear();
    }

}

class DisplayMessage {
    final String text;
    private int time = 30; // TODO maybe use the delta time to decrement
    void decay() {
        time--;
    }
    boolean finished() {
        return time <= 0;
    }
    DisplayMessage(String text) {
        this.text = text;
    }
}
