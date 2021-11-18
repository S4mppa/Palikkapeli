package com.entity.game.client.shaders;

import com.entity.game.client.world.Light;
import com.entity.game.client.user.Config;
import org.apache.commons.io.IOUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public abstract class ShaderProgram {

    private final int programId;

    private int vertexShaderId;

    private int fragmentShaderId;

    private final Map<String, Integer> uniforms;

    public ShaderProgram(String vertex, String fragment) throws Exception {
        programId = glCreateProgram();
        uniforms = new HashMap<>();
        if (programId == 0) {
            throw new Exception("Could not create Shader");
        }
        createVertexShader(loadShaderFromStream(vertex));
        createFragmentShader(loadShaderFromStream(fragment));
        link();
    }

    private static String loadShaderFromStream(String path) throws IOException {
        InputStream in = Config.class.getResourceAsStream("/shaders/" + path);
        return IOUtils.toString(in, StandardCharsets.UTF_8);
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(programId,
                uniformName);
        if (uniformLocation < 0) {
            System.out.println("Could not find uniform:" +
                    uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }


    public void setUniform(String uniformName, Matrix4f value) {
        // Dump the matrix into a float buffer
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }

    public void setUniform(String uniformName, Vector3f vector){
        glUniform3f(uniforms.get(uniformName), vector.x, vector.y, vector.z);
    }
    public void setUniform(String uniformName, Vector2f vector){
        glUniform2f(uniforms.get(uniformName), vector.x, vector.y);
    }

    public void setUniform(String uniformName, boolean value){
        glUniform1i(uniforms.get(uniformName), value ? 1 : 0);
    }
    public void setUniform(String uniformName, float f){
        glUniform1f(uniforms.get(uniformName), f);
    }
    public void setUniform(String uniformName, Vector4f vector4f){
        glUniform4f(uniforms.get(uniformName), vector4f.x, vector4f.y,vector4f.z,vector4f.w);
    }

    public void setUniform(Light light){
        setUniform("lightPosition", light.getPosition());
        setUniform("lightColour", light.getColour());

    }


    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

    }

    public void start() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanUp() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}