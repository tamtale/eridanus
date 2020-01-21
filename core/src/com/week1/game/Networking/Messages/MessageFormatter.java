package com.week1.game.Networking.Messages;

import com.badlogic.gdx.Gdx;
import com.week1.game.Model.GameState;

import java.util.ArrayList;
import java.util.List;

public class MessageFormatter {
    private static final String TAG = "MessageFormatter - lji1";

    /**
     * Given a json formatted message, parses the message into a known IMessage type.
     * 
     * @param msg - json formatted message
     * @return - the parsed message
     */
    public static List<AMessage> parseMessage(String msg) {
        Gdx.app.log(TAG, "Parsing message (UNIMPLEMENTED)");
        return new ArrayList(); // TODO: actually parse the message
    }

    /**
     * Given a known AMessage type, packages the message into a json formmated String.
     * 
     * @param msg - known AMessage type
     * @return - the json formatted message
     */
    public static String packageMessage(AMessage msg) {
        return "Unimplemented :("; // TODO: Implement me please
    }
    
    
}
