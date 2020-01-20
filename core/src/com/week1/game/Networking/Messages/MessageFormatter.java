package com.week1.game.Networking.Messages;

import com.week1.game.Model.GameState;

public class MessageFormatter {

    /**
     * Given a json formatted message, parses the message into a known IMessage type.
     * 
     * @param msg - json formatted message
     * @return - the parsed message
     */
    public static IMessage parseMessage(String msg) {
        
        return new IMessage() {
            @Override
            public boolean process(GameState gameState) {
                return false;
            }
        }; // TODO: Implement me please
    }

    /**
     * Given a known IMessage type, packages the message into a json formmated String.
     * 
     * @param msg - known IMessage type
     * @return - the json formatted message
     */
    public String packageMessage(IMessage msg) {
        return "Unimplemented :("; // TODO: Implement me please
    }
    
    
}
