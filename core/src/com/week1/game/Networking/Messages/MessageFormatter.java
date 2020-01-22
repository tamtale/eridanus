package com.week1.game.Networking.Messages;

import com.badlogic.gdx.Gdx;
import com.week1.game.Model.GameState;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;

public class MessageFormatter {
    private static final String TAG = "MessageFormatter - lji1";
    private static final Gson g = new Gson();

    /**
     * Given a json formatted message, parses the message into a known IMessage type.
     * 
     * @param jsonString - json formatted message
     * @return - the parsed message
     */
    public static List<AMessage> parseMessage(String jsonString) {
        AMessage msg = g.fromJson(jsonString, AMessage.class);
        List<AMessage> msgList = new ArrayList<>();
        msgList.add(msg);
        return msgList;
    }

    /**
     * Given a known AMessage type, packages the message into a json formmated String.
     * 
     * @param msg - known AMessage type
     * @return - the json formatted message
     */
    public static String packageMessage(AMessage msg) {
        String jsonString = g.toJson(msg);
        Gdx.app.log(TAG, "Packaged message: " + jsonString);
        return jsonString;
    }
    
    
}
