package com.entity.game.client.misc;

import com.entity.game.client.renderEngine.Camera;
import com.entity.game.client.renderEngine.CameraManager;
import com.entity.game.client.world.BlockPosition;
import com.entity.game.client.world.BlockUpdate;
import com.entity.game.client.world.ChunkPosition;
import com.entity.game.client.world.Player;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;

public class Utils {
    public static Vector3f floorVector(Vector3f vector){
        return new Vector3f((float)Math.floor(vector.x), (float)Math.floor(vector.y), (float)Math.floor(vector.z));
    }

    public static Vector3i floorVectorInt(Vector3f vector){
        return new Vector3i((int) Math.floor(vector.x), (int)Math.floor(vector.y), (int)Math.floor(vector.z));
    }

    public static Vector3f blockPosToVector(BlockPosition blockPosition){
        return new Vector3f(blockPosition.x,blockPosition.y,blockPosition.z);
    }
    public static Vector3f chunkPosToVector(ChunkPosition chunkPosition){
        return new Vector3f(chunkPosition.x,chunkPosition.y,chunkPosition.z);
    }
    public static double distBetweenBlocks(BlockPosition b1, BlockPosition b2){
        return Math.sqrt((b1.x - b2.x)*(b1.x - b2.x) + (b1.y - b2.y)*(b1.y - b2.y) + (b1.z - b2.z)*(b1.z - b2.z));
    }
    public static String vectorToString(Vector3fc vector3fc){
        return vector3fc.x() + ","+vector3fc.y()+","+vector3fc.z();
    }
    public static void terraform(Player player, boolean build){
        Camera camera = CameraManager.getActiveCamera();
        BlockPosition[] bpos = Ray.testPoint(player.getWorld().getChunkManager(), Ray.getSightVector(camera.getRotation()), camera.getPosition());
        if(bpos == null) return;
        BlockPosition bloc = bpos[0];
        Vector3f v = Utils.blockPosToVector(bloc);
        for(int x = -5; x < 5; x++){
            for(int y = -5; y < 5; y++){
                for(int z = -5; z < 5; z++){
                    if(v.distance(new Vector3f(bloc.x+x,bloc.y+y,bloc.z+z)) <= 5){
                        player.getWorld().getChunkManager().addBlockUpdate(new BlockUpdate(bloc.x+x,bloc.y+y,bloc.z+z, build ? 1 : 0));
                    }
                }
            }
        }
    }
}
