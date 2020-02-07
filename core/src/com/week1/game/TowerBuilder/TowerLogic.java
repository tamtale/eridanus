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

import static java.lang.Integer.max;


public class TowerLogic {

    ModelBuilder modelBuilder = new ModelBuilder();

    public static final Map<Integer, Model> modelMap = new HashMap<Integer, Model>();

    //Just for sanity -- so we can see what codes correspond to what blocks
    public static final Map<String, Integer> blocknamekey = new HashMap<>();

    public static Map<Integer, Integer> blockHp = new HashMap<>();
    public static Map<Integer, Integer> blockAtk = new HashMap<>();
    public static Map<Integer, Integer> blockPrice = new HashMap<>();


    public TowerLogic() {
        //Make the block types
        addBlockType("core_block.png", "coreBlock", 1, 0, 0, 0);

        //Materials
        addBlockType("space_obsidian.png", "obsidian", 2, 50,5,10);
        addBlockType("moonstone.png", "moonstone", 3, 25, 15,20);
        addBlockType("gold_wip_6.png", "space gold", 4, 15, 40,40);

        //Guns
        addBlockType("fire_wip2.png", "fire", 5, 15, 40, 35);
        addBlockType("water_wip.png", "water", 6, 15,40, 35);
        addBlockType("earth_wip4.png", "earth", 7, 15,40, 35);

        //Easter egg
        addBlockType("cat_boi.png", "easter egg", 69, 10, 10,0);

    }

    private void addBlockType(String filename, String blockname, Integer code, Integer hp, Integer atk, Integer price) {
        blocknamekey.put(blockname, code);

        modelMap.put(code, modelBuilder.createBox(5f, 5f, 5f,
                new Material(TextureAttribute.createDiffuse(new Texture(filename))),
                VertexAttributes.Usage.Position |VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal));

        blockHp.put(code, hp);
        blockAtk.put(code, atk);
        blockPrice.put(code, price);

    }



    public Tower getTower(ArrayList<BlockSpec> layout) {
        //TODO - the edge length is hardcoded as 5f right now

        Array<ModelInstance> towerLayout = new Array<>();

        Tower tower = new Tower(layout);

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

