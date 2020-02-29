package com.week1.game.Networking.Messages;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.week1.game.Networking.Messages.Control.ClientControl.ClientControlMessage;
import com.week1.game.Networking.Messages.Control.ClientControl.GoToGameMessage;
import com.week1.game.Networking.Messages.Control.ClientControl.GoToLoadoutMessage;
import com.week1.game.Networking.Messages.Control.ClientControl.PlayerIdMessage;
import com.week1.game.Networking.Messages.Control.HostControl.*;
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
    public static List<GameMessage> parseMessages(String jsonString) {
        List<GameMessage> msgList = new ArrayList<>();
        Update update = g.fromJson(jsonString, Update.class);
        Gdx.app.log("pjb3 MessageFormatter", "parseMessages(). Here are the messages to send out " + update.messages);
        update.messages.forEach((msg) -> {
            GameMessage parsedMsg = parseMessage(msg);
            if (parsedMsg == null) {
                Gdx.app.error(TAG, "The following message had an unrecognized MessageType: " + msg);
            } else {
                msgList.add(parsedMsg);
            }
        });
        return msgList;
    }

    public static GameMessage parseMessage(String msg) {
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
        } else if (prototypeMessage.messageTypeID == MessageType.SYNCERR){
            parsedMsg = g.fromJson(msg, SyncIssueMessage.class);
        } else if (prototypeMessage.messageTypeID == MessageType.CHECKSYNC){
            parsedMsg = g.fromJson(msg, CheckSyncMessage.class);
        } else if (prototypeMessage.messageTypeID == MessageType.TOWERDETAILS){
            parsedMsg = g.fromJson(msg, TowerDetailsMessage.class);
        }
        return parsedMsg;
    }
    
    public static HostControlMessage parseHostControlMessage(String jsonString) {
        AMessage parsedMsg = g.fromJson(jsonString, PrototypeMessage.class);
        
        System.out.println("About to parse as host control message: " + jsonString);
        
        if (parsedMsg != null) {
            if (parsedMsg.messageTypeID == MessageType.START) {
                return g.fromJson(jsonString, StartMessage.class);
            } else if (parsedMsg.messageTypeID == MessageType.REQUESTGOTOLOADOUT) {
                return g.fromJson(jsonString, RequestGoToLoadoutMessage.class);
            } else if (parsedMsg.messageTypeID == MessageType.SENDLOADOUT) {
                return g.fromJson(jsonString, SendLoadoutMessage.class);
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
            } else if (parsedMsg.messageTypeID == MessageType.GOTOLOADOUT) {
                return g.fromJson(jsonString, GoToLoadoutMessage.class);
            } else if (parsedMsg.messageTypeID == MessageType.GOTOGAME) {
                return g.fromJson(jsonString, GoToGameMessage.class);
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
