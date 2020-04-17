package com.week1.game.Model.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;

public class UnitModel {
    private Model model;
    private String factionName;

    //Used for Hp bars
    private Color associatedColor;

    public UnitModel(Model model, String factionName, Color color) {
        this.model = model;
        this.factionName = factionName;
        this.associatedColor = color;
    }


    public Color getColor() {
        return associatedColor;
    }

    public Model getModel() {
        return model;
    }

    @Override
    public String toString() {
        return factionName;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (o.getClass() == UnitModel.class) {
//            if (o.toString().equals(this.toString())) {
////            if (((UnitModel) o).getModel() == this.model && o.toString().equals(this.toString())) {
//                return true;
//            }
//        }
//        return false;
//    }


}
