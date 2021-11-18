package com.entity.game.client.postProcessing;


import com.entity.game.client.display.Window;
import org.lwjgl.opengl.GL11;

public class ImageRenderer {

	private Fbo fbo;

	public ImageRenderer(int width, int height, Window window) {
		this.fbo = new Fbo(width, height, Fbo.NONE, window);
	}

	public ImageRenderer() {}

	public void renderQuad() {
		if (fbo != null) {
			fbo.bindFrameBuffer();
		}
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		if (fbo != null) {
			fbo.unbindFrameBuffer();
		}
	}

	public int getOutputTexture() {
		return fbo.getColourTexture();
	}

	public void cleanUp() {
		if (fbo != null) {
			fbo.cleanUp();
		}
	}

}
