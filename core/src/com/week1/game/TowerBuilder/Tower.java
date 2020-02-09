package com.week1.game.TowerBuilder;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

import static java.lang.Integer.max;
import static java.lang.Math.min;

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
    private ArrayList<ArrayList<Integer>> footprint = new ArrayList<>();

    //want to also generate dimensions, footprint, other multipliers
    //prog generated view

    public Tower(ArrayList<BlockSpec> layout) {
        this.layout = layout;

        //generate model and stats
        populateFields();

    }

    private void populateFields() {
        int base_blocks = 0;

        //Max footprint for tower if 5 x 5. The origin is at 3 x 3
        //row represents x coord and column represents y coord
        for (int i = 0; i < 5; i++) {
            footprint.add(new ArrayList<>());
            for (int j = 0; j < 5; j++) {
                footprint.get(i).add(-1);
            }
        }

        for (BlockSpec block : layout) {
            ModelInstance blockInstance = new ModelInstance(TowerMaterials.modelMap.get(block.getBlockCode()));
            blockInstance.transform.setToTranslation(block.getX() * 5f, block.getY() * 5f, block.getZ() * 5f);
            this.model.add(blockInstance);


            Integer curFootPrint = footprint.get(block.getX() + 2).get(block.getZ() + 2);
            if (curFootPrint == -1) {
                footprint.get(block.getX() + 2).set(block.getZ() + 2, block.getY());
            } else {
                footprint.get(block.getX() + 2).set(block.getZ() + 2, min(curFootPrint, block.getY()));
            }

            //Generate the tower stats
            rawHp += TowerMaterials.blockHp.get(block.getBlockCode());
            rawAtk += TowerMaterials.blockAtk.get(block.getBlockCode());
            range = max(range, block.getY() + 1);
        }

        //We aren't calculating armour multipliers, etc
        atk = rawAtk;
        hp = rawHp * armour;

    }

    public ArrayList<ArrayList<Integer>> getFootprint() {
        return this.footprint;
    }
}
