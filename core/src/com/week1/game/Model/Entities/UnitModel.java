package com.week1.game.Model.Entities;

import com.badlogic.gdx.graphics.g3d.Model;

public class UnitModel {
    private Model model;
    private String factionName;

    public UnitModel(Model model, String factionName) {
        this.model = model;
        this.factionName = factionName;
    }

    public Model getModel() {
        return model;
    }

    public String toString() {
        return factionName;
    }

}
