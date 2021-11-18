package com.entity.game.client.misc;

public enum BlockData {
    //the numbers in array represent the layer in the texture array
    AIR(0, new int[]{}, true, false, false),
    GRASS(1, new int[]{1,1,3}, false, true, false),
    STONE(2, new int[]{6,6,6}, false, true, false),
    DIRT(3, new int[]{3,3,3}, false, true, false),
    SAND(4, new int[]{7,7,7}, false, true, false),
    SNOW_GRASS(5, new int[]{8,4,3}, false, true, false),
    WATER(6, new int[]{2,2,2}, true, false, false),
    COBBLE(7, new int[]{9,9,9}, false, true, false),
    LOG(8, new int[]{10, 11, 11}, false, true, false),
    LEAVES(9, new int[]{12,12,12}, true, true, false),
    PLANKS(10, new int[]{13,13,13}, false, true, false),
    GLASS(11, new int[]{14,14,14}, true, true, false),
    CACTUS(12, new int[]{16,15,17}, false, true, false),
    WEED(13, new int[]{18,0,0}, true, false, true),
    ROSE(14, new int[]{19,0,0}, true, false, true),
    SNOW(15, new int[]{4, 4, 4}, false, true, false),
    DARK_LEAVES(16, new int[]{20, 20, 20}, false, true, false),
    SNOWY_LEAVES(17, new int[]{21, 22, 21}, false, true, false),
    ICE(18, new int[]{23, 23, 23}, true, true, false);

    int[] faceTextures;
    int blockID;
    boolean isTransparent;
    boolean isCollidable;
    boolean crossFace;

    BlockData(int blockID, int[] faceTextures, boolean isTransparent, boolean isCollidable, boolean crossFace){
        this.isTransparent = isTransparent;
        this.faceTextures = faceTextures;
        this.isCollidable = isCollidable;
        this.blockID = blockID;
        this.crossFace = crossFace;
    }

    public int getSideTexture(){
        return faceTextures[0];
    }
    public int getTopTexture(){
        return faceTextures[1];
    }
    public int getBotTexture(){
        return faceTextures[2];
    }


    public int[] getFaceTextures() {
        return faceTextures;
    }

    public int getBlockID() {
        return blockID;
    }

    public boolean isCrossFace(){
        return crossFace;
    }

    public boolean isCollidable() {
        return isCollidable;
    }

    public boolean transparent(){
        return isTransparent;
    }

    public static int getHighestID(){
        int r = 0;
        for(BlockData b : BlockData.values()){
            if(b.getBlockID() > r) r= b.getBlockID();
        }
        return r;
    }

    public static BlockData getBlockByID(int id){
        for(BlockData b : BlockData.values()){
            if(b.getBlockID() == id) return b;
        }
        return null;
    }
}
