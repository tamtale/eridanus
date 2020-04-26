package com.week1.game.Networking.Messages.Game;

import com.badlogic.gdx.Gdx;
import com.week1.game.InfoUtil;
import com.week1.game.Model.GameEngine;
import com.week1.game.Model.GameState;
import com.week1.game.Networking.Messages.MessageType;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;

import static com.week1.game.GameController.PREFS;

public class SyncIssueMessage extends GameMessage {
    List<Integer> allHashes;
    static BufferedWriter writer;

    // Create the writer to write to the log file. Only need one persistent writer.
    static {
        if (PREFS.getBoolean("doLog")) {
            try {
                File errorFile = new File("logs/STATE-ERROR-LOG.txt");
                FileChannel outChan = new FileOutputStream(errorFile, true).getChannel();
                outChan.truncate(0);

                writer = new BufferedWriter(new FileWriter(errorFile, true));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public SyncIssueMessage(int playerID, MessageType messageTypeID, int intHash, List<Integer> allHashes) {
        super(playerID, messageTypeID, intHash);
        this.allHashes = allHashes;
    }

    @Override
    public boolean process(GameEngine engine, GameState gameState, InfoUtil util) {
        Gdx.app.log("pjb3 - SyncIssueMessage", "SYNCHRONIZATION ISSUE. CHECK STATE");
        util.log("pjb3 - SyncIssueMessage", "SYNCHRONIZATION ISSUE. CHECK STATE");

        if (PREFS.getBoolean("doLog")) {
            util.log("pjb3 - SyncIssueMessage", gameState.packState(engine.getTurn()).getGameString());

            // Create the message to append to the log file
            String fileContent = "Issue with turn " + (engine.getTurn() - 1) + " hashes were:";
            for (Integer i : allHashes) {
                fileContent += i.toString() + " ";
            }
            fileContent += ". Check log files in core/assets/logs\n";
            Gdx.app.debug("pjb3 - SyncIssueMessage", fileContent);

            // Add the message to the log file
            try {
                writer.append(fileContent);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
