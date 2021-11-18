package com.entity.game.client.voxelhandling;


import com.entity.game.client.display.Window;
import com.entity.game.client.world.Player;

public class WorldGenThread extends Thread {
    private TerrainGeneration terrainGeneration;
    private Window window;
    private Player player;


    public WorldGenThread(Player player, Window window){
        this.window = window;
        this.player = player;
    }

    @Override
    public synchronized void run() {
        while (!window.shouldClose()){
            player.getWorld().getTerrainGeneration().tick();
        }
    }
}
