package com.entity.game.client.bloom;

import com.entity.game.client.postProcessing.ImageRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;


public class CombineFilter {
	
	private ImageRenderer renderer;
	private CombineShader shader;
	
	public CombineFilter(){
		try {
			shader = new CombineShader();
			shader.start();
			shader.connectTextureUnits();
			shader.unbind();
			renderer = new ImageRenderer();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void render(int colourTexture, int highlightTexture){
		shader.start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, highlightTexture);
		renderer.renderQuad();
		shader.unbind();
	}
	
	public void cleanUp(){
		renderer.cleanUp();
		shader.cleanUp();
	}

}
