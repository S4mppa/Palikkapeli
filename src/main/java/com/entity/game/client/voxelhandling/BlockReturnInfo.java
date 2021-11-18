package com.entity.game.client.voxelhandling;

public class BlockReturnInfo{
    private int arrayIndex;
    private byte[] blockArray;

    public BlockReturnInfo(int arrayIndex, byte[] blockArray){
        this.arrayIndex = arrayIndex;
        this.blockArray = blockArray;
    }

    public byte[] getBlockArray() {
        return blockArray;
    }

    public int getArrayIndex() {
        return arrayIndex;
    }
}
