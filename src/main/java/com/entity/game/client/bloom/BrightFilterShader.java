package com.entity.game.client.bloom;


import com.entity.game.client.shaders.ShaderProgram;

public class BrightFilterShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "bloom/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "bloom/brightFilterFragment.txt";
	
	public BrightFilterShader() throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
}
