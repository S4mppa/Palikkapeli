package com.entity.game.client.voxelhandling;

public class Block {
    private int type;
    private int lightLevel = 15;
    public Block(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setLightLevel(int lightLevel) {
        this.lightLevel = lightLevel;
    }

    public int getLightLevel() {
        return lightLevel;
    }
}
