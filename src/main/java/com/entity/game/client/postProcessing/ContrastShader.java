package com.entity.game.client.postProcessing;


import com.entity.game.client.shaders.ShaderProgram;

public class ContrastShader extends ShaderProgram {

	private static final String VERTEX_FILE = "postProcessing/contrastVertex.txt";
	private static final String FRAGMENT_FILE = "postProcessing/contrastFragment.txt";
	
	public ContrastShader() throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
}
