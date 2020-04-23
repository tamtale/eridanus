package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.GameController;
import com.week1.game.Model.TowerFootprint;
import com.week1.game.Model.TowerLite;

import java.io.*;
import java.util.*;

public class TowerDetails {
    private Array<ModelInstance> model = new Array<>();
    private List<BlockSpec> layout;
    private TowerFootprint footprint;
    private Vector3 averageLocationOfHighestBlock = new Vector3();

    //these stats are based off the raw blocks
    private double rawHeight = 0;
    private double rawHp = 0;
    private double rawAtk = 0;
    private double rawPrice = 0;
    private int numGuns = 0;

    //These stats are based off raw stats and multipliers
    private double hp = 0;
    private double atk = 0;
    private double range = 0;
    private int armour = 1;
    private int price = 0;

    //For penalizing small bases
    private int baseSize = 0;
    private String name = "init";
    private float BLOCKLENGTH = TowerMaterials.BLOCKLENGTH;
    
    // Maps the spawner block type to the count of that spawner block in the tower
    // -> the tower will spawn a number of minions of each type proportional to
    //    the number of the corresponding spawner blocks in the tower
    private Map<Integer, Integer> spawnerBlockCounts = new HashMap<>();

    public static TowerDetails copy(TowerDetails tower) {
        List<BlockSpec> layoutCopy = new ArrayList<>();
        for (int i = 0; i < tower.layout.size(); i++) {
            layoutCopy.add(tower.layout.get(i));
        }
        return new TowerDetails(layoutCopy, tower.name);
    }

    public List<BlockSpec> getLayout() {
        return layout;
    }

    public int getPrice() {
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
    
    public Vector3 getHighestBlock() {
        return averageLocationOfHighestBlock;
    }
    
    public Map<Integer, Integer> getSpawnerCounts() {
        return spawnerBlockCounts;
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

            int i = 0;
            int x = 0,y = 0,z = 0,code = 0;
            for (String coord: block.split(", ")) {
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

            blocks.add(new BlockSpec(BlockType.values()[code], x, y, z));
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

            Gdx.app.log("towerdetails", filename);
            System.out.println(GameController.PREFS.getString("saveDir"));
            String twrName = filename.substring(("/eridanus/customTowers/").length() + GameController.PREFS.getString("saveDir").length(), filename.length() - 11);
            layout = blocks;
            name = twrName;
            calcRawStats(true);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public TowerDetails(List<BlockSpec> layout, String name) {
        this.layout = layout;
        this.name = name;
        
        //generate model and stats
        calcRawStats(true);
    }

    private void resetStats() {
        //these stats are based off the raw blocks
         rawHeight = 0;
          rawHp = 0;
          rawAtk = 0;
          rawPrice = 0;
          numGuns = 0;

        //These stats are based off raw stats and multipliers
          hp = 0;
          atk = 0;
          range = 0;
          armour = 1;
          price = 0;

        //For penalizing small bases
          baseSize = 0;
    }

    private void calcRawStats(boolean first_run) {
        resetStats();

        this.footprint = new TowerFootprint();

        int maxHeight = Integer.MIN_VALUE;
        int numBlocksAtMaxHeight = 0;
        for (int i = 0; i < layout.size(); i++) {
            BlockSpec block = layout.get(i);

            BlockType code = block.getBlockCode();
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();

            if (first_run) {
                ModelInstance blockInstance = new ModelInstance(TowerMaterials.modelMap.get(code));
                blockInstance.transform.setToTranslation(x * BLOCKLENGTH, y * BLOCKLENGTH, z * BLOCKLENGTH);
                this.model.add(blockInstance);
            }

            this.footprint.setFootPrint(x + 2, z + 2, true);

            //Generate the tower stats
            rawHp += TowerMaterials.blockHp.get(code);
            rawAtk += TowerMaterials.blockAtk.get(code);
            rawHeight = Math.max(rawHeight, y + 1);
            rawPrice += TowerMaterials.blockPrice.get(code);


            //Getting the base size
            if (y == 0) {
                baseSize += 1;
            }
            
            
            if (block.getY() > maxHeight) {
                averageLocationOfHighestBlock.set(block.getX(), block.getY(), block.getZ());
                maxHeight = block.getY();
                numBlocksAtMaxHeight = 1;
            } else if (block.getY() == maxHeight) {
                averageLocationOfHighestBlock.add(block.getX(), block.getY(), block.getZ());
                numBlocksAtMaxHeight++;
            }

            if (code == BlockType.WATER || code == BlockType.EARTH || code == BlockType.FIRE) {
                numGuns+= 1;
            }

            
            if (code == BlockType.SPAWNER) {
                // TODO: When there are multiple minion types, different spawner blocks will increase different counts
                if (spawnerBlockCounts.get(0) == null) {
                    spawnerBlockCounts.put(0, 1);
                } else {
                    spawnerBlockCounts.put(0, spawnerBlockCounts.get(0) + 1);
                }
            }
            
        }
        
        // Divide to find the average location of the highest blocks (this is where to put the health bar)
        averageLocationOfHighestBlock.scl(1f / ((float)numBlocksAtMaxHeight));

        calcFinalStats();
    }

    private void calcFinalStats() {
        //Tower base cost
        price = (int) (rawPrice + 100);

        atk = rawAtk * 0.3;
        if (rawHeight <= 5) {
            range = rawHeight + 5;
        } else {
            range = rawHeight * 2;
        }

        //Multipliers --- Fine tuning the stats

        //penalties up to basesize of 5
        hp = rawHp * Math.min(1, baseSize/5.0);

//        atk is inversely prop to range
        if (rawHeight > 4) {
//            Note: this if statement is required because the height of the ground in the tower editor is 0
            if (rawHeight == 5){
                atk = Math.round(atk * 4/5.0 * 10)/10.0;
            } else if (rawHeight == 6) {
                atk = Math.round(atk * 4/6.0 * 10)/10.0;
            } else if (rawHeight == 7) {
                atk = Math.round(atk * 5.0/8 * 10)/10.0;
            } else if (rawHeight == 8) {
                atk = Math.round(atk * 5.0/8 * 10)/10.0;
            }
        }

        //Negate the atk if there is no gun block
        if (numGuns == 0) {
            atk = 0;
        }

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
        BlockType code = bs.getBlockCode();
        int x = bs.getX();
        int y = bs.getY();
        int z = bs.getZ();


        //add to the layout
        this.layout.add(bs);

        //add to the model
        ModelInstance blockInstance = new ModelInstance(TowerMaterials.modelMap.get(code));
        blockInstance.transform.setToTranslation(x * BLOCKLENGTH, y * BLOCKLENGTH, z * BLOCKLENGTH);
        this.model.add(blockInstance);
        this.footprint.setFootPrint(x + 2, z + 2, true);

        if (y == 0) {
            baseSize += 1;
        }

        if (code == BlockType.FIRE || code == BlockType.EARTH || code == BlockType.WATER) {
            numGuns += 1;
        }

        //populate the fields
        calcRawStats(false);

    }

    private boolean checkRemovalSafety(int blockIdx) {

        boolean blockAtGroundLevel = false;

        List<BlockSpec> updatedBlocks = new ArrayList<>();
        for (int i = 0; i < layout.size(); i++) {
            if (i != blockIdx) {
                updatedBlocks.add(layout.get(i));
                if (layout.get(i).getY() == 0) {
                    blockAtGroundLevel = true;
                }
            }
        }

        if (!blockAtGroundLevel) {
            //disconnected from ground
            return false;
        }

        List<BlockSpec> q = new ArrayList<>();
        q.add(updatedBlocks.get(0));

        List<BlockSpec> seen = new ArrayList<>();
        while (q.size() != 0) {
            BlockSpec cur = q.get(0);
            q.remove(0);
            seen.add(cur);

            List<BlockSpec> nbrs = getNbrs(cur, updatedBlocks);
            for (int i = 0; i < nbrs.size(); i++) {
                if (!seen.contains(nbrs.get(i)) & !q.contains(nbrs.get(i))) {
                    q.add(nbrs.get(i));
                }
            }

        }

        if (seen.size() != updatedBlocks.size()) {
            Gdx.app.log("Unable to remove: TowerDetails expected " + updatedBlocks.size() + " blocks but only saw " + seen.size() + " blocks", "skv2");
            return false;
        }


        return true;
    }

    private List<BlockSpec> getNbrs(BlockSpec b, List<BlockSpec> blocks) {
        List<BlockSpec> nbrs = new ArrayList<>();
        int x = b.getX();
        int y = b.getY();
        int z = b.getZ();

        for (int i = 0; i < blocks.size(); i++) {
            BlockSpec blk = blocks.get(i);
            int curX = blk.getX();
            int curY = blk.getY();
            int curZ = blk.getZ();

            if (curX == x) {
                if (y == curY) {
                    if (Math.abs(z - curZ) == 1) {
                        nbrs.add(blk);
                    }
                } else if (z == curZ) {
                    if (Math.abs((y - curY)) == 1) {
                        nbrs.add(blk);
                    }
                }
            } else if (y == curY && z == curZ) {
                if (Math.abs(x - curX) == 1) {
                    nbrs.add(blk);
                }
            }

        }

        return nbrs;
    }

    protected boolean removeBlock(int x, int y, int z) {
        BlockType code = null;

        int modelIdx = -1;
        int blockIdx = -1;
        int newHt = 0;

        for (int i = 0; i < layout.size(); i++) {
            if (code == null) {
                BlockSpec b = layout.get(i);
                if (x == b.getX() & y == b.getY() & z == b.getZ()) {
                    code = b.getBlockCode();
                    blockIdx = i;
                }
            }

            ModelInstance m = model.get(i);
            Vector3 translation = new Vector3();
            m.transform.getTranslation(translation);


            if (translation.x == BLOCKLENGTH * x & translation.y == BLOCKLENGTH * y & translation.z == BLOCKLENGTH * z) {
                modelIdx = i;
            } else {
                newHt = Integer.max(newHt, ((int)translation.y/5) + 1);

            }

        }

        if (!checkRemovalSafety(blockIdx)) {
            return false;
        }
        //remove from model and layout
        layout.remove(blockIdx);
        this.model.removeIndex(modelIdx);

        calcRawStats(false);

        return true;

    }

    private String getLayoutStr() {
        String towerStr = "[";
        for (int i = 0; i < layout.size(); i++) {
            BlockSpec b = layout.get(i);
            towerStr += b.toFileStr();
            towerStr += ", ";
        }
        towerStr = towerStr.substring(0, towerStr.length() - 2);
        towerStr += "]";

        return  towerStr;

    }

    public boolean saveTower() {
        //write tower layout to a file
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(GameController.PREFS.getString("saveDir") + "/eridanus/customTowers/" +name +"_layout.txt"), "utf-8"))) {
            writer.write(getLayoutStr());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public String getName() {
        return name;
    }

    public void setRange(double range) {
        this.range = range;
    }


    public TowerLite getTowerLite() {
        return new TowerLite(name, layout);
    }
}
