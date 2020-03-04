package com.week1.game.TowerBuilder;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
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

    static float BLOCKLENGTH = 5f;

    static ModelBuilder modelBuilder = new ModelBuilder();

    public static final Map<BlockType, Model> modelMap = new HashMap<>();

    //Just for sanity -- so we can see what codes correspond to what blocks
    public static final Map<String, BlockType> materialCodes = new HashMap<>();
    public static final Map<String, Material> materialNames = new HashMap<>();
    public static final Map<String, BlockType> blocknamekey = new HashMap<>();

    public static Map<BlockType, Integer> blockHp = new HashMap<>();
    public static Map<BlockType, Integer> blockAtk = new HashMap<>();
    public static Map<BlockType, Integer> blockPrice = new HashMap<>();


    static  {

    //Materials
    addBlockType("space_obsidian2.png", "obsidian", BlockType.OBSIDIAN, 50,5,10);
    addBlockType("moonstone3.png", "moonstone", BlockType.MOONSTONE, 25, 15,20);
    addBlockType("gold2.png", "space gold", BlockType.SPACEGOLD, 15, 40,40);

    //Guns
    addBlockType("water2.png", "water", BlockType.WATER, 15,40, 35);
    addBlockType("fire2.png", "fire", BlockType.FIRE, 15, 40, 35);
    addBlockType("earth3.png", "earth", BlockType.EARTH, 15,40, 35);


    //Easter egg
    addBlockType("cat_boi.png", "easter egg", BlockType.EASTEREGG, 10, 10,0);

    //Highlighted block type
    Material mat = new Material();
    mat.set(new ColorAttribute(ColorAttribute.Diffuse, 84, 68, 79, 1f));
    mat.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.8f));

    Model highlighted = modelBuilder.createBox(5f, 5f ,5f,
            mat,
            VertexAttributes.Usage.Position |VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal);
    modelMap.put(BlockType.HIGHLIGHT, highlighted);

    }


    private static void addBlockType(String filename, String blockname, BlockType code, Integer hp, Integer atk, Integer price) {
        blocknamekey.put(blockname, code);

        Material mat = new Material(TextureAttribute.createDiffuse(new Texture(filename)));
        modelMap.put(code, modelBuilder.createBox(5f, 5f, 5f, mat,
                VertexAttributes.Usage.Position |VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal));

        if (code != BlockType.EASTEREGG) {
            materialNames.put(blockname, mat);
            materialCodes.put(blockname, code);
        }

        blockHp.put(code, hp);
        blockAtk.put(code, atk);
        blockPrice.put(code, price);

    }


}

