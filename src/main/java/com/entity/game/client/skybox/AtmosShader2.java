package com.entity.game.client.skybox;


import com.entity.game.client.shaders.ShaderProgram;

public class AtmosShader2 extends ShaderProgram {
    public AtmosShader2() throws Exception {
        super("bloom/simpleVertex.txt", "skybox/atmosFragment2.glsl");
        createUniform("depthTexture");
        createUniform("colourTexture");
        createUniform("worldSpaceCameraPos");
        createUniform("inverseProjection");
        createUniform("inverseView");
    }
}
