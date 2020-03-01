package com.week1.game.TowerBuilder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TowerMaterials {

    static ModelBuilder modelBuilder = new ModelBuilder();

    public static final Map<Integer, Model> modelMap = new HashMap<Integer, Model>();

    //Just for sanity -- so we can see what codes correspond to what blocks
    public static final Map<String, Integer> materialCodes = new HashMap<>();
    public static final Map<String, Material> materialNames = new HashMap<>();

    public static Map<Integer, Integer> blockHp = new HashMap<>();
    public static Map<Integer, Integer> blockAtk = new HashMap<>();
    public static Map<Integer, Integer> blockPrice = new HashMap<>();


    static  {

    //Make the block types
//    addBlockType("core_block.png", "coreBlock", 1, 0, 0, 0);

        //Materials
        addBlockType("space_obsidian2.png", "obsidian", 2, 50,5,10);
        addBlockType("moonstone3.png", "moonstone", 3, 25, 15,20);
        addBlockType("gold2.png", "space gold", 4, 15, 40,40);

        //Guns
        addBlockType("water2.png", "water", 6, 15,40, 35);
        addBlockType("fire2.png", "fire", 5, 15, 40, 35);
        addBlockType("earth3.png", "earth", 7, 15,40, 35);


    //Easter egg
    addBlockType("cat_boi.png", "easter egg", 69, 10, 10,0);

    //Highlighted block type
    Material mat = new Material();
    mat.set(new ColorAttribute(ColorAttribute.Diffuse, 84, 68, 79, 1f));
    mat.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.8f));

    Model highlighted = modelBuilder.createBox(5f, 5f ,5f,
            mat,
            VertexAttributes.Usage.Position |VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal);
    modelMap.put(0, highlighted);

    }

    private static void addBlockType(String filename, String blockname, Integer code, Integer hp, Integer atk, Integer price) {

        Material mat = new Material(TextureAttribute.createDiffuse(new Texture(filename)));
        modelMap.put(code, modelBuilder.createBox(5f, 5f, 5f, mat,
                VertexAttributes.Usage.Position |VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal));

        if (code != 69) {
            materialNames.put(blockname, mat);
            materialCodes.put(blockname, code);
        }

        blockHp.put(code, hp);
        blockAtk.put(code, atk);
        blockPrice.put(code, price);

    }



    public static TowerDetails getTower(List<BlockSpec> layout) {
        //TODO - the edge length is hardcoded as 5f right now

        Array<ModelInstance> towerLayout = new Array<>();

        TowerDetails towerDetails = new TowerDetails(layout, "");

        return towerDetails;
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

