package com.entity.game.client.misc;


import com.entity.game.client.world.BlockPosition;
import com.entity.game.client.voxelhandling.ChunkManager;
import org.joml.*;

import java.lang.Math;


public class Ray {
    private static final int LENGTH = 500;

    public static Vector3f getSightVector(Vector3f rotation){
        double rotX = rotation.x/180*Math.PI;
        double rotY = rotation.y/180*Math.PI;

        double dx = Math.sin(rotY);
        double dz = -Math.cos(rotY);
        double dy = -Math.sin(rotX);
        double m = Math.cos(rotX);

        return new Vector3f((float) (dx*m), (float) dy, (float) (dz*m)).normalize();
    }

    public static BlockPosition[] testPoint(ChunkManager chunkManager, Vector3f ray, Vector3f camPos){
        BlockPosition prev = new BlockPosition(0,0,0);
        for(float i = 0; i < LENGTH; i += 0.1){
            Vector3i point = Utils.floorVectorInt(getPointOnRay(camPos, ray, i));
            BlockPosition blockPosition = new BlockPosition(point.x,point.y,point.z);
            if(chunkManager.getBlockAt(blockPosition) > 0){
                return new BlockPosition[]{blockPosition, prev};
            }
            else{
                prev = new BlockPosition(point.x,point.y,point.z);
            }
        }
        return null;
    }

    private static Vector3f getPointOnRay(Vector3f camPos, Vector3f ray, float distance) {
        Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
        Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
        return start.add(scaledRay);
    }

}
