package com.week1.game.TowerBuilder;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

import static java.lang.Integer.max;

public class Tower {
    private Integer hp = 0;
    private Integer atk = 0;
    private Integer range = 0;
    private Integer rawHp = 0;
    private Integer rawAtk = 0;
    private Integer armour = 1;

    public Integer getHp() {
        return hp;
    }

    public Integer getAtk() {
        return atk;
    }

    public Integer getRange() {
        return range;
    }

    public Array<ModelInstance> getModel() {
        return model;
    }

    private Array<ModelInstance> model = new Array<>();
    private ArrayList<BlockSpec> layout;

    //want to also generate dimensions, footprint, other multipliers
    //prog generated view

    public Tower(ArrayList<BlockSpec> layout) {
        this.layout = layout;

        //generate model and stats
        populateFields();

        //generate footprint -- what format?


    }

    private void populateFields() {
        int base_blocks = 0;

        for (BlockSpec block : layout) {
            ModelInstance blockInstance = new ModelInstance(TowerLogic.modelMap.get(block.getBlockCode()));
            blockInstance.transform.setToTranslation(block.getX() * 5f, block.getY() * 5f, block.getZ() * 5f);

            this.model.add(blockInstance);

//            //Generate the tower stats
            rawHp += TowerLogic.blockHp.get(block.getBlockCode());
            rawAtk += TowerLogic.blockAtk.get(block.getBlockCode());
            range = max(range, block.getY() + 1);
        }

        atk = rawAtk;
        hp = rawHp * armour;

    }

}
