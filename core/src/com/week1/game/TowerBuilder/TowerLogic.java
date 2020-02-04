package com.week1.game.TowerBuilder;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TowerLogic {

    ModelBuilder modelBuilder = new ModelBuilder();

    private Map<Integer, Model> modelMap = new HashMap<Integer, Model>();

    //Just for sanity -- so we can see what codes correspond to what blocks
    private Map<String, Integer> blocknamekey = new HashMap<>();

    public TowerLogic() {
        //Make the textures
        addTexture("core_block.png", "coreBlock", 1);
        addTexture("space_obsidian.png", "obsidian", 2);
        addTexture("moonstone.png", "moonstone", 3);
        addTexture("gold_wip_6.png", "space gold", 4);
        addTexture("fire_wip2.png", "fire", 5);
        addTexture("water_wip.png", "water", 6);
        addTexture("earth_wip4.png", "earth", 7);
        addTexture("cat_boi.png", "easter egg", 8);

    }

    private void addTexture(String filename, String blockname, Integer code) {
        blocknamekey.put(blockname, code);

        modelMap.put(code, modelBuilder.createBox(5f, 5f, 5f,
                new Material(TextureAttribute.createDiffuse(new Texture(filename))),
                VertexAttributes.Usage.Position |VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal));
    }



    public Array<ModelInstance> getTower(ArrayList<BlockSpec> layout) {
        //TODO - the edge length is hardcoded as 5f right now

        Array<ModelInstance> tower = new Array<>();

        for (BlockSpec block : layout) {
            ModelInstance blockInstance = new ModelInstance(modelMap.get(block.getBlockCode()));
            blockInstance.transform.setToTranslation(block.getX() * 5f, block.getY() * 5f, block.getZ() * 5f);

            tower.add(blockInstance);
        }

        return tower;
    }

//    public Array<ModelInstance> readTowerFromTxt(String filename) {
//        //build the layout list
//        JSONParser jsonParser = new JSONParser();
//        System.out.println("hello");
//        try (FileReader reader = new FileReader(filename)) {
//            System.out.println(reader.read());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //call previous function
//
//        return null;
//    }



}

