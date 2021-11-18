package com.entity.game.client.skybox;


import com.entity.game.client.shaders.ShaderProgram;

public class AtmosShader extends ShaderProgram {
    public AtmosShader() throws Exception {
        super("skybox/atmosVertex.txt", "skybox/atmosFragment.txt");
        createUniform("proj");
        createUniform("view");
    }
}
