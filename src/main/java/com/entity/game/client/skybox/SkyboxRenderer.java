package com.entity.game.client.skybox;

import com.entity.game.client.misc.WorldConstants;
import com.entity.game.client.world.Mesh;
import com.entity.game.client.world.World;
import com.entity.game.client.renderEngine.*;
import org.lwjgl.opengl.GL30;


public class SkyboxRenderer extends Renderer {
    private static final float SIZE = 500f;
    private static final float[] VERTICES = {
            -SIZE,  SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            -SIZE,  SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE,  SIZE};

    private static String[] TEXTURE_FILES = {"right1", "left1", "top1", "bottom1", "front1", "back1"};
    private Mesh cube;
    private int textureID;
    private SkyboxShader shader;

    public SkyboxRenderer(Loader loader) throws Exception{
        cube = loader.loadToVAO(VERTICES, 3);
        textureID = loader.loadCubeMap(TEXTURE_FILES);
        this.shader = new SkyboxShader();
        shader.start();
        shader.loadProjectionMatrix(Transformation.getProjectionMatrix(WorldConstants.FOV, WorldConstants.WINDOW_WIDTH,WorldConstants.WINDOW_HEIGHT, WorldConstants.Z_NEAR, WorldConstants.Z_FAR));
        shader.unbind();
    }



    public void render(World world){
        shader.start();
        shader.setUniform("skyColour", WorldConstants.SKYCOLOUR);
        shader.setUniform("sunDir", world.getSunDir());
        shader.setUniform("cameraPos", CameraManager.getActiveCamera().getPosition());
        shader.loadViewMatrix(CameraManager.getActiveCamera());
        shader.loadProjectionMatrix(Transformation.getProjectionMatrix(WorldConstants.FOV, WorldConstants.WINDOW_WIDTH,WorldConstants.WINDOW_HEIGHT, WorldConstants.Z_NEAR, WorldConstants.Z_FAR));
        GL30.glBindVertexArray(cube.getVaoID());
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, textureID);
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0,cube.getVertexCount());
        shader.unbind();
        GL30.glBindVertexArray(0);
    }


}
