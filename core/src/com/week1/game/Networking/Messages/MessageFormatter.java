package com.week1.game.Networking.Messages;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.week1.game.Networking.Messages.Control.ClientControl.*;
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
        if (msg == null) {
            return null;
        } else  if (prototypeMessage.messageTypeID == MessageType.TEST) {
            return g.fromJson(msg, TestMessage.class);
        } else if (prototypeMessage.messageTypeID == MessageType.CREATEMINION){
            return g.fromJson(msg, CreateMinionMessage.class);
        } else if (prototypeMessage.messageTypeID == MessageType.MOVE){
            return g.fromJson(msg, MoveMinionMessage.class);
        } else if (prototypeMessage.messageTypeID == MessageType.CREATETOWER){
            return g.fromJson(msg, CreateTowerMessage.class);
        } else if (prototypeMessage.messageTypeID == MessageType.INIT){
            return g.fromJson(msg, InitMessage.class);
        } else if (prototypeMessage.messageTypeID == MessageType.SYNCERR){
            return g.fromJson(msg, SyncIssueMessage.class);
        } else if (prototypeMessage.messageTypeID == MessageType.CHECKSYNC){
            return g.fromJson(msg, CheckSyncMessage.class);
        } else if (prototypeMessage.messageTypeID == MessageType.TOWERDETAILS){
            return g.fromJson(msg, TowerDetailsMessage.class);
        }
        Gdx.app.debug(TAG, "Failed to parse as game message.");
        return null;
    }
    
    public static HostControlMessage parseHostControlMessage(String jsonString) {
        AMessage parsedMsg = g.fromJson(jsonString, PrototypeMessage.class);
        
        Gdx.app.debug(TAG, "About to parse as host control message: " + jsonString);
        
        if (parsedMsg != null) {
            if (parsedMsg.messageTypeID == MessageType.REQUESTGOTOGAME) {
                return g.fromJson(jsonString, RequestGoToGameMessage.class);
            } else if (parsedMsg.messageTypeID == MessageType.REQUESTGOTOLOADOUT) {
                return g.fromJson(jsonString, RequestGoToLoadoutMessage.class);
            } else if (parsedMsg.messageTypeID == MessageType.REQUESTRESTART) {
                return g.fromJson(jsonString, RequestRestartMessage.class);
            } else if (parsedMsg.messageTypeID == MessageType.SUBMITLOADOUT) {
                return g.fromJson(jsonString, SubmitLoadoutMessage.class);
            } else if (parsedMsg.messageTypeID == MessageType.RETRACTLOADOUT) {
                return g.fromJson(jsonString, RetractLoadoutMessage.class);
            } else if (parsedMsg.messageTypeID == MessageType.SUBMITPLAYERINFO) {
                return g.fromJson(jsonString, SubmitPlayerInfo.class);
            }
        }

        Gdx.app.debug(TAG, "Failed to parse as host control message.");
        return null;
    }

    public static ClientControlMessage parseClientControlMessage(String jsonString) {
        
        Gdx.app.log(TAG, "About to parse as ClientControlMessage: " + jsonString);
        
        AMessage parsedMsg = g.fromJson(jsonString, PrototypeMessage.class);
        if (parsedMsg != null) {
            if (parsedMsg.messageTypeID == MessageType.PLAYERID) {
                return g.fromJson(jsonString, PlayerIdMessage.class);
            } else if (parsedMsg.messageTypeID == MessageType.GOTOLOADOUT) {
                return g.fromJson(jsonString, GoToLoadoutMessage.class);
            } else if (parsedMsg.messageTypeID == MessageType.GOTOGAME) {
                return g.fromJson(jsonString, GoToGameMessage.class);
            } else if (parsedMsg.messageTypeID == MessageType.READYTOSTART) {
                return g.fromJson(jsonString, ReadyToStartMessage.class);
            } else if (parsedMsg.messageTypeID == MessageType.UNDOREADYTOSTART) {
                return g.fromJson(jsonString, UndoReadyToStart.class);
            } else if (parsedMsg.messageTypeID == MessageType.RESTART) {
                return g.fromJson(jsonString, RestartMessage.class);
            } else if (parsedMsg.messageTypeID == MessageType.JOINEDPLAYERS) {
                return g.fromJson(jsonString, JoinedPlayersMessage.class);
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
        return jsonString;
    }
    
    
}
