package com.week1.game.Networking.Messages;

public enum MessageType {
    // Game Messages
    TEST,
    CREATEMINION, 
    MOVE,
    CREATETOWER,
    INIT,
    CHECKSYNC,
    SYNCERR,
    TOWERDETAILS,
    
    // Client Control Messages
    PLAYERID,
    GOTOLOADOUT,
    GOTOGAME,
    
    // Host Control Messages
    TCPJOIN,
    UDPJOIN,
    START,
    SENDLOADOUT,
    REQUESTGOTOLOADOUT,
}
