package com.week1.game.Networking.Messages;

public enum MessageType {
    // Game Messages
    TEST,
    CREATEMINION, 
    MOVE,
    CREATETOWER,
    INIT,
    TOWERDETAILS,
    
    // Client Control Messages
    PLAYERID,
    
    // Host Control Messages
    JOIN,
    START
}
