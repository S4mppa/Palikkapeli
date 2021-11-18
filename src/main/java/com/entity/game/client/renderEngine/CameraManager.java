package com.entity.game.client.renderEngine;

import com.entity.game.client.world.ChunkPosition;
import org.joml.Vector3f;

public class CameraManager {
    private static Camera activeCamera;

    public static void setActiveCamera(Camera activeCamera) {
        CameraManager.activeCamera = activeCamera;
    }

    public static Camera getActiveCamera() {
        return activeCamera;
    }

    public static double distFromCam(ChunkPosition cpos){
        Vector3f cam = new Vector3f(activeCamera.getPosition().x,0,activeCamera.getPosition().z);
        return new Vector3f(cpos.x,0,cpos.z).distance(cam);
    }
}
