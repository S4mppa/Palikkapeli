package com.entity.game.client.gaussianBlur;


import com.entity.game.client.shaders.ShaderProgram;

public class VerticalBlurShader extends ShaderProgram {

	private static final String VERTEX_FILE = "blur/verticalBlurVertex.txt";
	private static final String FRAGMENT_FILE = "blur/blurFragment.txt";
	
	private int location_targetHeight;
	
	protected VerticalBlurShader() throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE);
		createUniform("targetHeight");
	}
	
	protected void loadTargetHeight(float height){ setUniform("targetHeight", height);
	}
}
