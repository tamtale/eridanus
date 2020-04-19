package com.week1.game.TowerBuilder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import java.util.HashMap;
import java.util.Map;


public class TowerMaterials {

    public static float BLOCKLENGTH = 5f;

    static ModelBuilder modelBuilder = new ModelBuilder();

    public static final Map<BlockType, Model> modelMap = new HashMap<>();

    //Just for sanity -- so we can see what codes correspond to what blocks
    public static final Map<String, BlockType> materialCodes = new HashMap<>();
    public static final Map<String, BlockType> blocknamekey = new HashMap<>();

    public static Map<BlockType, Integer> blockHp = new HashMap<>();
    public static Map<BlockType, Integer> blockAtk = new HashMap<>();
    public static Map<BlockType, Integer> blockPrice = new HashMap<>();


    static  {

    //Materials
    addBlockType("space_obsidian2.png", "etherite", BlockType.ETHERITE, 50,5,10);
    addBlockType("moonstone3.png", "kuiperium", BlockType.KUIPERIUM, 25, 15,20);
    addBlockType("gold2.png", "novacore", BlockType.NOVACORE, 15, 40,40);

    //Guns
    addBlockType("water2.png", "water", BlockType.WATER, 15,40, 100);
    addBlockType("fire2.png", "fire", BlockType.FIRE, 15, 40, 100);
    addBlockType("earth3.png", "earth", BlockType.EARTH, 15,40, 100);

    // Spawners
     addBlockType("spawner2.png", "spawner", BlockType.SPAWNER, 5,0, 500);


    //Easter egg
    addBlockType("cat_boi.png", "easter egg", BlockType.EASTEREGG, 10, 10,0);

    //Highlighted block type
    Material mat = new Material();
    mat.set(new ColorAttribute(ColorAttribute.Diffuse, Color.WHITE));
    mat.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.8f));

    Model highlighted = modelBuilder.createBox(BLOCKLENGTH, BLOCKLENGTH ,BLOCKLENGTH,
            mat,
            VertexAttributes.Usage.Position |VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal);
    modelMap.put(BlockType.HIGHLIGHT, highlighted);


    //Ground in Tower editor block type
    Material mat2 = new Material(TextureAttribute.createDiffuse(new Texture("ground_highlight.png")));
    mat2.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.5f));

    Model highlighted2 = modelBuilder.createBox(BLOCKLENGTH, BLOCKLENGTH ,BLOCKLENGTH,
            mat2,
            VertexAttributes.Usage.Position |VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal);
    modelMap.put(BlockType.GROUND_HIGHLIGHT, highlighted2);

    //Hacky solution to be able to render the tower of highlighted blocks in the tower editor
    blockHp.put(BlockType.GROUND_HIGHLIGHT, 0);
    blockAtk.put(BlockType.GROUND_HIGHLIGHT, 0);
    blockPrice.put(BlockType.GROUND_HIGHLIGHT, 0);

    }


    private static void addBlockType(String filename, String blockname, BlockType code, Integer hp, Integer atk, Integer price) {
        blocknamekey.put(blockname, code);

        Material mat = new Material(TextureAttribute.createDiffuse(new Texture(filename)));
        modelMap.put(code, modelBuilder.createBox(BLOCKLENGTH, BLOCKLENGTH, BLOCKLENGTH, mat,
                VertexAttributes.Usage.Position |VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal));

        if (code != BlockType.EASTEREGG) {
            materialCodes.put(blockname, code);
        }

        blockHp.put(code, hp);
        blockAtk.put(code, atk);
        blockPrice.put(code, price);

    }


}

