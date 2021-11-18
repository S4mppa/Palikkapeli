package com.entity.game.client.voxelhandling;


import com.entity.game.client.misc.Coordinates;
import com.entity.game.client.world.ChunkPosition;

import java.util.Random;

enum TreeType{
    OAK,
    SPRUCE,
    SNOWY_SPRUCE;
}

abstract class TreeBuilder{
    private static int[] oakTreeShape = {2,3,3,3,2};
    public static void oakTree(int x, int y, int z, ChunkManager chunkManager, Random random, ChunkPosition c){
        int treeHeight = random.nextInt(5) + 7;
        for(int ty = 1; ty < treeHeight; ty++){
            chunkManager.addGlobalBlock(Coordinates.toGlobalBlockPosition(x,y+ty,z, c), 8, c);
        }

        for(int i = 0; i < oakTreeShape.length; i++){
            int tty = oakTreeShape[i];
            for (float px = 0; px < Math.PI * 2; px += 0.1){
                int tx = (int) (tty  * Math.cos(px));
                int tz = (int) (tty * Math.sin(px));
                chunkManager.addGlobalBlock(Coordinates.toGlobalBlockPosition(x+tx,treeHeight-i+y,z+tz, c), 16, c);
            }
        }
    }
    public static void spruceTree(int x, int y, int z, ChunkManager chunkManager, Random random, ChunkPosition c, int leaveType){
        int treeHeight = random.nextInt(5) + 14;

        for(int ty = 1; ty < treeHeight; ty++){
            chunkManager.addGlobalBlock(Coordinates.toGlobalBlockPosition(x,y+ty,z, c), 8, c);
        }


        for(int ty = 8; ty < treeHeight; ty++){
            for (float px = 0; px < Math.PI * 2; px += 1){
                int tx = (int) ((treeHeight-ty-3) * Math.cos(px));
                int tz = (int) ((treeHeight-ty-3) * Math.sin(px));
                chunkManager.addGlobalBlock(Coordinates.toGlobalBlockPosition(x+tx,y+ty,z+tz, c), leaveType, c);
            }
        }

    }
}


public abstract class Decorations {
    public static void tree(TreeType treeType, int x, int y, int z, ChunkManager chunkManager, Random random, ChunkPosition c){
        switch (treeType){
            case SPRUCE:
                TreeBuilder.spruceTree(x,y,z,chunkManager,random,c, 16);
                break;
            case SNOWY_SPRUCE:
                TreeBuilder.spruceTree(x,y,z,chunkManager,random,c, 17);
                break;
            case OAK:
                TreeBuilder.oakTree(x,y,z,chunkManager,random,c);
                break;
        }
    }
}
