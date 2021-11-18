package com.entity.game.client.skybox;


import com.entity.game.client.world.Mesh;
import com.entity.game.client.renderEngine.*;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;


public class AtmosRenderer extends Renderer {
    private static final float SIZE = 500000f;
    private final AtmosShader shader;
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
    private Mesh cube;
    public AtmosRenderer(Loader loader) throws Exception{
        this.shader = new AtmosShader();
        cube = loader.loadToVAO(VERTICES, 3);
    }


    public void render(){
        shader.start();
        Camera camera = CameraManager.getActiveCamera();
        Matrix4f viewMatrix = Transformation.getViewMatrix(camera);

        viewMatrix.setColumn(3, new Vector4f(0,0,0,1));

        shader.setUniform("proj", Transformation.getProjectionMatrix());
        shader.setUniform("view", viewMatrix);
        GL30.glBindVertexArray(cube.getVaoID());
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0,cube.getVertexCount());
        shader.unbind();
    }
}
