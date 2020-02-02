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

//import org.json.JSON

public class TowerGenerator {

    ModelBuilder modelBuilder = new ModelBuilder();

    Texture stoneTex = new Texture("space_obsidian.png");

    Texture woodTex = new Texture("wood2.png");

    Texture catTex = new Texture("cat_boi.png");

    Texture gunTex = new Texture("gun_basic.png");

    Texture splashTex = new Texture("splash_gun.png");

    Texture aoeTex = new Texture("aoe_gun.png");

    Texture goldTex = new Texture("gold.png");

    Texture coreTex = new Texture("core_block.png");

    private Model easterCat = modelBuilder.createBox(5f, 5f, 5f,
            new Material(TextureAttribute.createDiffuse(catTex)),
            VertexAttributes.Usage.Position |
                    VertexAttributes.Usage.TextureCoordinates |
                    VertexAttributes.Usage.Normal);
    private Model stoneBlock = modelBuilder.createBox(5f, 5f, 5f,
                new Material(TextureAttribute.createDiffuse(stoneTex)),
                VertexAttributes.Usage.Position |
                        VertexAttributes.Usage.TextureCoordinates |
                        VertexAttributes.Usage.Normal);
    private Model woodBlock = modelBuilder.createBox(5f, 5f, 5f,
            new Material(TextureAttribute.createDiffuse(woodTex)),
    VertexAttributes.Usage.Position |VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal);
    private Model gunBlock = modelBuilder.createBox(5f, 5f, 5f,
            new Material(TextureAttribute.createDiffuse(gunTex)),
    VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal);
    private Model splashBlock = modelBuilder.createBox(5f, 5f, 5f,
            new Material(TextureAttribute.createDiffuse(splashTex)),
    VertexAttributes.Usage.Position |VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal);
    private Model amberBlock = modelBuilder.createBox(5f, 5f, 5f,
            new Material(TextureAttribute.createDiffuse(goldTex)),
    VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal);
    private Model aoeBlock = modelBuilder.createBox(5f, 5f, 5f,
            new Material(TextureAttribute.createDiffuse(aoeTex)),
    VertexAttributes.Usage.Position |VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal);
    private Model coreBlock = modelBuilder.createBox(5f, 5f, 5f,
            new Material(TextureAttribute.createDiffuse(coreTex)),
    VertexAttributes.Usage.Position |VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal);

    private Map<Integer, Model> blockMap = new HashMap<Integer, Model>();

    public TowerGenerator() {
        blockMap.put(1, coreBlock);
        blockMap.put(2, stoneBlock);
        blockMap.put(3, woodBlock);
        blockMap.put(4, amberBlock);
        blockMap.put(5, gunBlock);
        blockMap.put(6, splashBlock);
        blockMap.put(7, aoeBlock);
        blockMap.put(69, easterCat);
    }



    public Array<ModelInstance> getTower(ArrayList<BlockSpec> layout) {
        //TODO - the edge length is hardcoded as 5f right now

        Array<ModelInstance> tower = new Array<>();

        for (BlockSpec block : layout) {
            ModelInstance blockInstance = new ModelInstance(blockMap.get(block.getBlockCode()));
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

