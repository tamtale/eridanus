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
    TARGET,
    
    // Client Control Messages
    PLAYERID,
    GOTOLOADOUT,
    GOTOGAME,
    GOTOSPLASHSCREEN,
    READYTOSTART,
    UNDOREADYTOSTART,
    RESTART,
    JOINEDPLAYERS,
    
    // Host Control Messages
    SUBMITLOADOUT,
    SUBMITCOLOR,
    RETRACTLOADOUT,
    SUBMITPLAYERINFO,
    REQUESTGOTOGAME,
    REQUESTGOTOLOADOUT,
    REQUESTGOTOSPLASHSCREEN,
    REQUESTRESTART,
    ;
}

