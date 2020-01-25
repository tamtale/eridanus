package com.week1.game.Networking.Messages;

import java.util.List;

public class Update {
    public List<String> messages;
    
    public Update(List<String> messages) {
        this.messages = messages;
    }
}
