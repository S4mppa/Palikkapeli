package com.entity.game.client.skybox;


import com.entity.game.client.renderEngine.Camera;
import com.entity.game.client.renderEngine.Transformation;
import com.entity.game.client.shaders.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class SkyboxShader extends ShaderProgram {

    private static final String VERTEX_FILE = "skybox/skyboxVertex.txt";
    private static final String FRAGMENT_FILE = "skybox/skyboxFragment.txt";
    private float rotation = 0;
    private float time = 0;
    private final float ROTATION_SPEED = 0.0001f;

    public SkyboxShader() throws Exception {
        super(VERTEX_FILE, FRAGMENT_FILE);
        super.createUniform("projectionMatrix");
        super.createUniform("viewMatrix");
        super.createUniform("skyColour");
        super.createUniform("sunDir");
        super.createUniform("cameraPos");
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.setUniform("projectionMatrix", matrix);
    }

    public void updateRotation(){
        rotation += ROTATION_SPEED;
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f matrix = Transformation.getViewMatrix(camera);
        matrix.setColumn(3, new Vector4f(0,0,0,1));
        matrix.rotateY(rotation);

        super.setUniform("viewMatrix", matrix);
    }

}
