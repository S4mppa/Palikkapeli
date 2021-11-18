package com.entity.game.client.skybox;


import com.entity.game.client.display.Window;
import com.entity.game.client.postProcessing.ImageRenderer;
import com.entity.game.client.renderEngine.Camera;
import com.entity.game.client.renderEngine.Transformation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;


public class AtmosFilter {
    private ImageRenderer renderer;
    private AtmosShader2 shader;
    private Camera camera;

    public AtmosFilter(int width, int height, Window window, Camera camera) throws Exception{
        shader = new AtmosShader2();
        this.camera = camera;
        renderer = new ImageRenderer(width, height, window);
    }

    public void render(int texture, int depthTexture){
        shader.start();
        shader.setUniform("colourTexture", 0);
        shader.setUniform("depthTexture", 1);
        shader.setUniform("worldSpaceCameraPos", camera.getPosition());
        shader.setUniform("inverseView", Transformation.getViewMatrix(camera).invert());
        shader.setUniform("inverseProjection", Transformation.getProjectionMatrix().invert());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
        renderer.renderQuad();
        shader.unbind();
    }

    public int getOutputTexture(){
        return renderer.getOutputTexture();
    }

    public void cleanUp(){
        renderer.cleanUp();
        shader.cleanUp();
    }
}
