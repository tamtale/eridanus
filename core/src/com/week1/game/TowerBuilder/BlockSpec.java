package com.week1.game.TowerBuilder;

public class BlockSpec {
    private BlockType blockCode;
    private int x;
    private int y;
    private int z;

    public BlockSpec(BlockType blockCode, int x, int y, int z) {
        this.blockCode = blockCode;
        this.x = x;
        this.y = y;
        this.z = z;

    }

    public BlockType getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(BlockType blockCode) {
        this.blockCode = blockCode;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof  BlockSpec) {
            if (((BlockSpec) o).getX() == x & ((BlockSpec) o).getY() == y & ((BlockSpec) o).getZ() == z & ((BlockSpec) o).getBlockCode() == blockCode) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        //This one is easier to read
        return "(" + x + ", " + y + ", " + z + ", " + blockCode +")";
    }

    public String toFileStr() {
        //This one is for writing to file
        return "(" + x + ", " + y + ", " + z + ", " + blockCode.ordinal() +")";
    }
}
