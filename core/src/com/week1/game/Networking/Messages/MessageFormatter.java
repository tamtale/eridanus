package com.week1.game.Networking.Messages;

import com.badlogic.gdx.Gdx;
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
        Gdx.app.log(TAG, "jsonString: " + jsonString);
        List<AMessage> msgList = new ArrayList<>();
        Update update = g.fromJson(jsonString, Update.class);
        update.messages.forEach((msg) -> {
            AMessage parsedMsg = g.fromJson(msg, PrototypeMessage.class);
//            Gdx.app.log(TAG, "Parsed Message: " + parsedMsg);
            if (parsedMsg.messageTypeID == MessageTypes.TEST.ordinal()) { //TODO: does this have to be an int?
                parsedMsg = g.fromJson(msg, TestMessage.class);
            } else if (parsedMsg.messageTypeID == MessageTypes.CREATE.ordinal()){
                parsedMsg = g.fromJson(msg, CreateMinionMessage.class);
            } else {
                parsedMsg = g.fromJson(msg, MoveMinionMessage.class);
            }
            msgList.add(parsedMsg);
        });
        return msgList;
    }

    /**
     * Given a known AMessage type, packages the message into a json formmated String.
     * 
     * @param msg - known AMessage type
     * @return - the json formatted message
     */
    public static String packageMessage(Object msg) {
        String jsonString = g.toJson(msg);
//        Gdx.app.log(TAG, "Packaged message: " + jsonString);
        return jsonString;
    }
    
    
}
