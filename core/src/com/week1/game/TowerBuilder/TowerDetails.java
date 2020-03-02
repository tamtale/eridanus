package com.week1.game.TowerBuilder;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.TowerFootprint;
import com.week1.game.Model.World.Block;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//Todos for towers -- ensure that they are connected when deleting blocks
//                  -- update stats and block names
//                  -- clean up load code
//                  -- make things static if possible
//                  -- make dialog bigger and show up to the side or over the camera

public class TowerDetails {
    private double hp = 0;
    private double atk = 0;
    private int height = 0;
    private double range = 0;
    private int rawHp = 0;
    private int rawAtk = 0;
    private int armour = 1;
    private double price = 0;
    private Array<ModelInstance> model = new Array<>();
    private List<BlockSpec> layout;
    private TowerFootprint footprint;
    private String name = "";

    public List<BlockSpec> getLayout() {
        return layout;
    }

    public double getPrice() {
        return price;
    }

    public double getHp() {
        return hp;
    }

    public double getAtk() {
        return atk;
    }

    public double getRange() {
        return range;
    }

    public Array<ModelInstance> getModel() {
        return model;
    }

    private List<BlockSpec> parseLayoutString(String layout) {
        List<BlockSpec> blocks = new ArrayList<>();

        String blox = layout.substring(1, layout.length() - 1);
        boolean isFirst = true;
        for (String block: blox.split("\\(")) {
            if (isFirst) {
                isFirst = false;
                continue;
            }
//            System.out.println("block is "+ block);
            int i = 0;
            int x = 0,y = 0,z = 0,code = 0;
            for (String coord: block.split(", ")) {
//                System.out.println("coord is: " + coord);
                if (i == 0) {
                    x = Integer.parseInt(coord);
                } else if (i == 1) {
                    y = Integer.parseInt(coord);
                } else if (i == 2) {
                    z = Integer.parseInt(coord);
                } else if (i == 3) {
                    code = Integer.parseInt(coord.substring(0, 1));
                }
                i += 1;
            }

            blocks.add(new BlockSpec(code, x, y, z));
        }


        return blocks;
    }


    public TowerDetails(String filename) {

        //parse the name and block layout to use the other constructor
        try {
            List<BlockSpec> blocks = new ArrayList<>();

            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                blocks = parseLayoutString(data);
            }
            myReader.close();

            String twrName = filename.substring(13, filename.length() - 11);
            System.out.println(twrName);
            layout = blocks;
            name = twrName;
            populateFields();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public TowerDetails(List<BlockSpec> layout, String name) {
        this.layout = layout;
        this.name = name;

        //generate model and stats
        populateFields();
    }

    
    /*
        Overloaded constructor to allow us to bypass automatic stat generation, if necessary for testing
     */
    public TowerDetails(TowerFootprint fp, double health, double price, double range, double damage) {
        this.footprint = fp;
        this.hp = health;
        this.price = price;
        this.range = range;
        this.atk = damage;
    }

    private void populateFields() {
        int base_blocks = 0;

        this.footprint = new TowerFootprint();

        for (BlockSpec block : layout) {
            int code = block.getBlockCode();
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();

            ModelInstance blockInstance = new ModelInstance(TowerMaterials.modelMap.get(code));
            blockInstance.transform.setToTranslation(x * 5f, y * 5f, z * 5f);
            this.model.add(blockInstance);
            this.footprint.setFootPrint(x + 2, z + 2, true);

            //Generate the tower stats
            rawHp += TowerMaterials.blockHp.get(code);
            rawAtk += TowerMaterials.blockAtk.get(code);
            height = Integer.max(height, y + 1);
            price += TowerMaterials.blockPrice.get(code);
        }

        //We aren't calculating armour multipliers, etc
        atk = rawAtk * 0.05;
        hp = rawHp * armour;
        range = height * 3;

    }

    public TowerFootprint getFootprint() {
        return this.footprint;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected void addBlock(BlockSpec bs) {
        //made it protected so that only the TowerBuilder classes are dealing with adding blocks to the
        //towers
        int code = bs.getBlockCode();
        int x = bs.getX();
        int y = bs.getY();
        int z = bs.getZ();


        //add to the layout
        this.layout.add(bs);

        //add to the model
        ModelInstance blockInstance = new ModelInstance(TowerMaterials.modelMap.get(code));
        blockInstance.transform.setToTranslation(x * 5f, y * 5f, z * 5f);
        this.model.add(blockInstance);
        this.footprint.setFootPrint(x + 2, z + 2, true);

        //populate the fields
        this.atk += TowerMaterials.blockAtk.get(code);
        this.hp += TowerMaterials.blockHp.get(code);
        height = Integer.max(height, y + 1);
        range = height * 3;
        this.price += TowerMaterials.blockPrice.get(code);

    }

    private boolean checkRemovalSafety(BlockSpec b, int blockIdx, int modelIdx) {
        return true;
    }

    protected boolean removeBlock(BlockSpec bs) {
        int x = bs.getX();
        int y = bs.getY();
        int z = bs.getZ();
        int code = -1;

        int modelIdx = -1;
        int blockIdx = -1;
        boolean shrinkFootprint = true;
        int newHt = 0;

        System.out.println("removal");
        for (int i = 0; i < layout.size(); i++) {
            if (code == -1) {
                BlockSpec b = layout.get(i);
                if (x == b.getX() & y == b.getY() & z == b.getZ()) {
                    code = b.getBlockCode();
                    blockIdx = i;
                }
            }

            ModelInstance m = model.get(i);
            Vector3 translation = new Vector3();
            m.transform.getTranslation(translation);


            if (translation.x == 5f * x & translation.y == 5f * y & translation.z == 5f * z) {
                modelIdx = i;
            } else {
                System.out.println((int)translation.y/5);
                newHt = Integer.max(newHt, ((int)translation.y/5) + 1);

            }

            if (translation.x == x & translation.z == z) {
                shrinkFootprint = false;
            }

        }

        if (!checkRemovalSafety(bs, blockIdx, modelIdx)) {
            return false;
        }
        //remove from model and layout
        layout.remove(blockIdx);
        this.model.removeIndex(modelIdx);


        //update the fields
        this.atk -= TowerMaterials.blockAtk.get(code);
        this.hp -= TowerMaterials.blockHp.get(code);
        this.price -= TowerMaterials.blockPrice.get(code);

        //updating range and footprint
        if (shrinkFootprint) {
            this.footprint.setFootPrint(x + 2, z + 2, false);
        }

        height = newHt;
        range = newHt * 3;


        return true;

    }

    private String getLayoutStr() {
        String towerStr = "[";
        for (BlockSpec b: layout) {
            towerStr += b.toString();
            towerStr += ", ";
        }
        towerStr = towerStr.substring(0, towerStr.length() - 2);
        towerStr += "]";

        return  towerStr;

    }

    public void saveTower() {
        //write tower layout to a file
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("customTowers/" +name +"_layout.txt"), "utf-8"))) {
            writer.write(getLayoutStr());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
