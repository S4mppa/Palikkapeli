package com.entity.game.client.world;

import com.entity.game.client.events.EventBus;
import com.entity.game.client.voxelhandling.*;
import com.entity.game.client.worldsaving.WorldPacker;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class World {
    private static final int TICK_SPEED = 20;

    private String name;
    private int seed;
    private long time = 12000;

    private TerrainGenerator terrainGeneration;
    private ChunkManager chunkManager;

    public World(EventBus eventBus, String name){
        this.name = name;
        this.seed = getSeed();
        this.chunkManager = new ChunkManager(this, eventBus);
        this.terrainGeneration = getMainTerrainGenerator();
        try {
            loadWorld();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        worldEventTask();
    }

    public TerrainGenerator getMainTerrainGenerator(){
        return new TerrainGeneration(this);
    }

    public long getTime() {
        return time;
    }

    //Used in shader calculations
    public float getNormalizedTime(){
        //System.out.println(-(1/(145000000f))*time*(time - 2) + ":"+time);
        return -(1/(145000000f))*time*(time - 24000);
    }

    public Vector3f getSunDir(){
        double angle = (time / 24000f) * 360;
        double rotX = angle/180*Math.PI;

        double rotY = 0;

        double dx = Math.sin(rotY);
        double dz = -Math.cos(rotY);
        double dy = -Math.sin(rotX);
        double m = Math.cos(rotX);

        return new Vector3f((float) (dx*m), (float) dy, (float) (dz*m)).normalize();
    }

    public void worldEventTask(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(time >= 23999) time = 0;
                time++;
            }
        },0,TICK_SPEED);
    }

    public String getName() {
        return name;
    }

    public int getSeed(){
        int s = 0;
        for(char c : name.toCharArray()){
            s += c;
        }
        return (int) Math.round(s * 28.6518);
    }

    public void loadWorld() throws IOException {
        WorldPacker.readWorldFile(chunkManager, name+".world");
    }

    public TerrainGenerator getTerrainGeneration() {
        return terrainGeneration;
    }

    public ChunkManager getChunkManager() {
        return chunkManager;
    }
}
