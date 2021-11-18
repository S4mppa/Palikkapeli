package com.entity.game.client.voxelhandling;


import com.entity.game.client.display.Window;

public class ChunkGenThread extends Thread{
    private ChunkManager chunkManager;
    private Window window;
    public ChunkGenThread(Window window, ChunkManager chunkManager){
        this.chunkManager = chunkManager;
        this.window = window;
    }
    @Override
    public synchronized void run() {
        while (!window.shouldClose()){
            chunkManager.update();
            try {
                wait(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
