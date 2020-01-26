package com.week1.game.Networking.Messages.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.week1.game.Model.GameState;
import com.week1.game.Model.Unit;
import com.week1.game.Networking.Messages.Game.GameMessage;
import com.week1.game.Networking.Messages.MessageType;

import java.util.HashMap;
import java.util.Map;

import static com.week1.game.GameController.SCALE;

public class CreateMinionMessage extends GameMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.CREATEMINION;
    private final static String TAG = "CreateMinionMessage";
    
    private final static Map<Integer, Texture> colorMap = new HashMap<Integer, Texture>() {{

        Pixmap blueMap = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888){{
            setColor(Color.BLUE);
            fill();
        }};
        Texture blueTexture = new Texture(blueMap){{ blueMap.dispose(); }};
        this.put(0, blueTexture);

        Pixmap redMap = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888){{
            setColor(Color.RED);
            fill();
        }};
        Texture redTexture = new Texture(redMap){{ redMap.dispose(); }};
        this.put(1, redTexture);

        Pixmap yellowMap = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888){{
            setColor(Color.YELLOW);
            fill();
        }};
        Texture yellowTexture = new Texture(yellowMap){{ yellowMap.dispose(); }};
        this.put(2, yellowTexture);

        
    }};

    private float x, y;
    private int unitType;

    public CreateMinionMessage(float x, float y, int unitType, int playerID){
        super(playerID, MESSAGE_TYPE);
        this.x = x;
        this.y = y;
        this.unitType = unitType; // TODO use this
    }

    @Override
    public boolean process(GameState inputState){
        Unit unit = new Unit(x, y, colorMap.get(playerID), playerID);
        inputState.addUnit(unit);
//        SteeringAgent agent = new SteeringAgent(unit, new Vector2(x, y), 0,
//                new Vector2((float) .1, (float) .1), 0, 1, true, (float).5);
//        inputState.addAgent(agent);
//        unit.agent = agent;
        return true;
    }

    @Override
    public String toString() {
        return "CreateMinionMessage: " + x + ", " + y + ", " + unitType + ", " + playerID;
    }
}
