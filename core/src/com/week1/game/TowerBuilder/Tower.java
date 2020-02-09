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
    private Integer price = 0;
    private Array<ModelInstance> model = new Array<>();
    private ArrayList<BlockSpec> layout;
    private ArrayList<ArrayList<Integer>> footprint = new ArrayList<>();

    public Integer getPrice() {
        return price;
    }

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
            Integer code = block.getBlockCode();
            Integer x = block.getX();
            Integer y = block.getY();
            Integer z = block.getZ();

            ModelInstance blockInstance = new ModelInstance(TowerMaterials.modelMap.get(code));
            blockInstance.transform.setToTranslation(x * 5f, y * 5f, z * 5f);
            this.model.add(blockInstance);


            Integer curFootPrint = footprint.get(x + 2).get(z + 2);
            if (curFootPrint == -1) {
                footprint.get(x + 2).set(z + 2, y);
            } else {
                footprint.get(x + 2).set(z + 2, min(curFootPrint, y));
            }

            //Generate the tower stats
            rawHp += TowerMaterials.blockHp.get(code);
            rawAtk += TowerMaterials.blockAtk.get(code);
            range = max(range, y + 1);
            price += TowerMaterials.blockPrice.get(code);
        }

        //We aren't calculating armour multipliers, etc
        atk = rawAtk;
        hp = rawHp * armour;

    }

    public ArrayList<ArrayList<Integer>> getFootprint() {
        return this.footprint;
    }
}
