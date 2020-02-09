package com.week1.game.Networking.Messages;

import com.badlogic.gdx.Gdx;
import com.google.gson.*;
import com.week1.game.Networking.Messages.Control.*;
import com.week1.game.Networking.Messages.Game.*;

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
    public static List<GameMessage> parseMessage(String jsonString) {
        List<GameMessage> msgList = new ArrayList<>();
        Update update = g.fromJson(jsonString, Update.class);
        update.messages.forEach((msg) -> {
            AMessage prototypeMessage = g.fromJson(msg, PrototypeMessage.class);
//            Gdx.app.log(TAG, "Parsed Message: " + parsedMsg);
            GameMessage parsedMsg = null;
            if (prototypeMessage.messageTypeID == MessageType.TEST) { 
                parsedMsg = g.fromJson(msg, TestMessage.class);
            } else if (prototypeMessage.messageTypeID == MessageType.CREATEMINION){
                parsedMsg = g.fromJson(msg, CreateMinionMessage.class);
            } else if (prototypeMessage.messageTypeID == MessageType.MOVE){
                parsedMsg = g.fromJson(msg, MoveMinionMessage.class);
            } else if (prototypeMessage.messageTypeID == MessageType.CREATETOWER){
                parsedMsg = g.fromJson(msg, CreateTowerMessage.class);
            } else if (prototypeMessage.messageTypeID == MessageType.INIT){
                parsedMsg = g.fromJson(msg, InitMessage.class);
            } else if (prototypeMessage.messageTypeID == MessageType.TOWERDETAILS){
                parsedMsg = g.fromJson(msg, InitMessage.class);
            }
            if (parsedMsg == null) {
                Gdx.app.error(TAG, "The following message had an unrecognized MessageType: " + msg);
            } else {
                msgList.add(parsedMsg);
            }
        });
        return msgList;
    }
    
    public static HostControlMessage parseHostControlMessage(String jsonString) {
        AMessage parsedMsg = g.fromJson(jsonString, PrototypeMessage.class);
        if (parsedMsg != null) {
            if (parsedMsg.messageTypeID == MessageType.JOIN) {
                return g.fromJson(jsonString, JoinMessage.class);
            } else if (parsedMsg.messageTypeID == MessageType.START) {
                return g.fromJson(jsonString, StartMessage.class);
            }
        }

        Gdx.app.debug(TAG, "Failed to parse as control message.");
        return null;
    }

    public static ClientControlMessage parseClientControlMessage(String jsonString) {
        AMessage parsedMsg = g.fromJson(jsonString, PrototypeMessage.class);
        if (parsedMsg != null) {
            if (parsedMsg.messageTypeID == MessageType.PLAYERID) {
                return g.fromJson(jsonString, PlayerIdMessage.class);
            }
        }
        
        Gdx.app.debug(TAG, "Failed to parse as control message.");
        return null;
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
