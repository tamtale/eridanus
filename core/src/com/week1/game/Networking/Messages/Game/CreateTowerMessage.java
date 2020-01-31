package com.week1.game.Networking.Messages.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.week1.game.Model.GameState;
import com.week1.game.Model.Tower;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Model.TowerType;

import static com.week1.game.Model.StatsConfig.*;


public class CreateTowerMessage extends GameMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.CREATETOWER;

    private float x, y;
    private TowerType towerType;

    public CreateTowerMessage(float x, float y, TowerType towerType, int playerID){
        super(playerID, MESSAGE_TYPE);
        this.x = x;
        this.y = y;
        this.towerType = towerType; // TODO use this
    }

    @Override
    public boolean process(GameState inputState){
        // First, check if it is able to be built.
        // TODO do lookup of the cost based on towerType, do not use hardcoded number [tempTower1Cost]
        if (tempTower1Cost > inputState.getPlayerStats(playerID).getMana()) {
            // Do not have enough mana!
            Gdx.app.log("pjb3 - CreateTowerMessage", "Not enough mana to create tower.");
            return false; // indicate it was NOT placed
        }

        Gdx.app.log("pjb3 - CreateTowerMessage", "Used " + tempTower1Cost + " mana to create tower.");
        inputState.getPlayerStats(playerID).useMana(tempTower1Cost);

        Gdx.app.log("lji1 - CreateTowerMessage", "Creating tower!");
        Tower tower = new Tower((int) x, (int) y, towerType, tempTowerHealth, playerID);

        inputState.addTower(tower);
        return true;
    }

    @Override
    public String toString() {
        return "CreateTowerMessage: " + x + ", " + y + ", " + towerType + ", " + playerID;
    }
}
