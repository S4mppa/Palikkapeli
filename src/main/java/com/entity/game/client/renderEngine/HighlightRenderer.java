package com.entity.game.client.renderEngine;

import com.entity.game.client.misc.Ray;
import com.entity.game.client.misc.WorldConstants;
import com.entity.game.client.world.BlockPosition;
import com.entity.game.client.world.Mesh;
import com.entity.game.client.shaders.ShaderProgram;
import com.entity.game.client.voxelhandling.ChunkManager;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;

public class HighlightRenderer extends ShaderProgram {

    private BlockPosition latestPoint;
    private Mesh mesh;
    private static final float SIZE = 1.005f;
    private static final float[] VERTICES = {
            -0.005f,  SIZE, -0.005f,
            -0.005f, -0.005f, -0.005f,
            SIZE, -0.005f, -0.005f,
            SIZE, -0.005f, -0.005f,
            SIZE,  SIZE, -0.005f,
            -0.005f,  SIZE, -0.005f,

            -0.005f, -0.005f,  SIZE,
            -0.005f, -0.005f, -0.005f,
            -0.005f,  SIZE, -0.005f,
            -0.005f,  SIZE, -0.005f,
            -0.005f,  SIZE,  SIZE,
            -0.005f, -0.005f,  SIZE,

            SIZE, -0.005f, -0.005f,
            SIZE, -0.005f,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE, -0.005f,
            SIZE, -0.005f, -0.005f,

            -0.005f, -0.005f,  SIZE,
            -0.005f,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE, -0.005f,  SIZE,
            -0.005f, -0.005f,  SIZE,

            -0.005f,  SIZE, -0.005f,
            SIZE,  SIZE, -0.005f,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            -0.005f,  SIZE,  SIZE,
            -0.005f,  SIZE, -0.005f,

            -0.005f, -0.005f, -0.005f,
            -0.005f, -0.005f,  SIZE,
            SIZE, -0.005f, -0.005f,
            SIZE, -0.005f, -0.005f,
            -0.005f, -0.005f,  SIZE,
            SIZE, -0.005f,  SIZE};
    public HighlightRenderer(Loader loader) throws Exception{
        super("terrain/selectVertex.txt", "terrain/selectFragment.txt");
        createUniform("blockPos");
        createUniform("viewMatrix");
        createUniform("projectionMatrix");

        this.mesh = loader.loadToVAO(VERTICES, 3);
    }

    public BlockPosition getLatestPoint(){
        return latestPoint;
    }

    public void updateRay(ChunkManager chunkManager){
        Vector3f ray = Ray.getSightVector(CameraManager.getActiveCamera().getRotation());
        BlockPosition[] points = Ray.testPoint(chunkManager, ray, CameraManager.getActiveCamera().getPosition());
        if(points != null){
            latestPoint = points[0];
        }
        else{
            latestPoint = null;
        }
    }



    public void render(Vector3f pos){
        // Draw the mesh
        if(pos == null) return;
        start();
        GL30.glBindVertexArray(mesh.getVaoID());
        setUniform("projectionMatrix", Transformation.getProjectionMatrix(WorldConstants.FOV, WorldConstants.WINDOW_WIDTH, WorldConstants.WINDOW_HEIGHT, WorldConstants.Z_NEAR, WorldConstants.Z_FAR));
        setUniform("viewMatrix", Transformation.getViewMatrix(CameraManager.getActiveCamera()));
        setUniform("blockPos", pos);
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, mesh.getVertexCount());
        GL30.glBindVertexArray(0);
        unbind();
    }
}
