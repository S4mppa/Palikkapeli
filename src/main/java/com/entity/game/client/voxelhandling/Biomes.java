package com.entity.game.client.voxelhandling;


import com.entity.game.client.misc.WorldConstants;
import org.joml.Vector3f;


enum GrassColor{
    NORMAL(160, 224, 92),
    DARKER(34,139,34),
    GREENER(41, 171, 134),
    TEAL(41, 171, 134),
    LIGHTER(160, 224, 92);

    private Vector3f color;
    GrassColor(float r, float g, float b){
        this.color = new Vector3f(r/255f,g/255f,b/255f);
    }

    public Vector3f getColor() {
        return color;
    }
}

public enum Biomes {
    FOREST(1, 0.0075, WorldConstants.CHUNKSIZE*5, 0.01f, GrassColor.DARKER),
    SWAMP(1, 0.1,WorldConstants.CHUNKSIZE*4,0.01f, GrassColor.LIGHTER),
    DESERT(4, 0.0025,WorldConstants.CHUNKSIZE*4,0.01f, GrassColor.NORMAL),
    TAIGA(5, 0.0125,WorldConstants.CHUNKSIZE*4,0.01f, GrassColor.DARKER),
    MOUNTAINS(1, 0.0075, WorldConstants.CHUNKSIZE*8,0.01f, GrassColor.NORMAL),
    OCEAN(4, 0, WorldConstants.CHUNKSIZE,0.1f, GrassColor.NORMAL);

    private int blockType;
    private double decorationRarity;
    private int maxHeight;
    private float waveStrength;
    private GrassColor grassColor;
    Biomes(int blockType, double decorationRarity, int maxHeight, float waveStrength, GrassColor grassColor){
        this.blockType = blockType;
        this.maxHeight = maxHeight;
        this.decorationRarity = decorationRarity;
        this.waveStrength = waveStrength;
        this.grassColor = grassColor;
    }

    public GrassColor getGrassColor() {
        //TODO add bilinear interpolation, for now this just returns NORMAL
        return GrassColor.NORMAL;
    }

    public float getWaveStrength() {
        return waveStrength;
    }

    public int getWaterLevel(){
        return 40;
    }

    public double getDecorationRarity() {
        return decorationRarity;
    }

    public int getBlockType() {
        return blockType;
    }

    public int getMaxHeight() {
        return maxHeight;
    }
}