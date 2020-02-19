package com.week1.game.Networking.Messages.Game;

import com.badlogic.gdx.Gdx;
import com.week1.game.InfoUtil;
import com.week1.game.Model.GameState;
import com.week1.game.Networking.Messages.MessageType;

public class SyncIssueMessage extends GameMessage {

    public SyncIssueMessage(int playerID, MessageType messageTypeID, int intHash) {
        super(playerID, messageTypeID, intHash);
    }

    @Override
    public boolean process(GameState gameState, InfoUtil util) {
        Gdx.app.log("pjb3 - SyncIssueMessage", "SYNCHRONIZATION ISSUE. CHECK STATE");
        util.log("pjb3 - SyncIssueMessage", "SYNCHRONIZATION ISSUE. CHECK STATE");
        util.log("pjb3 - SyncIssueMessage", gameState.packState().getGameString());
        return true;
    }
}
