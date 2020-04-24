package com.week1.game.Model;

/**
 * This class is passed over the network so the host gets the info about the
 * name and what color the person wants to be.
 */
public class PlayerInfo {
    private String playerName;
    private String faction = "Choosing Color";
    //    private UnitModel faction = UnitLoader.EMPTY_FACTIONLESS; //this needs to be set by selectbox

    public PlayerInfo(String playerName) {
        this.playerName = playerName;
    }

    public String getFaction() {
        return faction;
    }

//    public String getFactionName() {
//        return faction.toString();
//    }
    public String getPlayerName() {
        return playerName;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }
}
