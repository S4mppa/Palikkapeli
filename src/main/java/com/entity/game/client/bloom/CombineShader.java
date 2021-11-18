package com.entity.game.client.bloom;


import com.entity.game.client.shaders.ShaderProgram;

public class CombineShader extends ShaderProgram {

	private static final String VERTEX_FILE = "bloom/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "bloom/combineFragment.txt";
	
	public CombineShader() throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE);
		createUniform("colourTexture");
		createUniform("highlightTexture");
	}

	public void connectTextureUnits(){
		setUniform("colourTexture", 0);
		setUniform("highlightTexture", 1);
	}

}
