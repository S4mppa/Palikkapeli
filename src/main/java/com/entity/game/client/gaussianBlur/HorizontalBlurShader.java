package com.entity.game.client.gaussianBlur;


import com.entity.game.client.shaders.ShaderProgram;

public class HorizontalBlurShader extends ShaderProgram {

	private static final String VERTEX_FILE = "blur/horizontalBlurVertex.txt";
	private static final String FRAGMENT_FILE = "blur/blurFragment.txt";
	
	private int location_targetWidth;
	
	protected HorizontalBlurShader() throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE);
		createUniform("targetWidth");
	}

	protected void loadTargetWidth(float width){
		setUniform("targetWidth", width);
	}

	
}
