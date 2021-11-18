package com.entity.game.client.renderEngine;


import com.entity.game.client.misc.Utils;
import com.entity.game.client.display.Window;
import com.entity.game.client.world.BlockPosition;
import org.joml.Vector3f;
import org.joml.Vector3i;


import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    private final Vector3f position;

    private final Vector3f rotation;

    private boolean locked = false;

    private final Vector3f velocity;

    Window window;

    public Camera(Window window){
        this.window = window;
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        velocity = new Vector3f();
    }
    public Camera(Window window, Vector3f position, Vector3f rotation) {
        this.window = window;
        this.position = position;
        this.rotation = rotation;
        velocity = new Vector3f();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void setPosition(Vector3f vector3f){
        this.position.set(vector3f);
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void resetVelocity(){
        velocity.mul(0);
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if ( offsetZ != 0 ) {
            float x = (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            float z = (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ;
            position.x += x;
            position.z += z;
        }
        if ( offsetX != 0) {
            float x = (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            float z = (float)Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
            position.x += x;
            position.z += z;
        }
        position.y += offsetY;
    }

    public BlockPosition getBlockPos(){
        Vector3i pPos = Utils.floorVectorInt(getPosition());
        return new BlockPosition(pPos.x,pPos.y,pPos.z);
    }



    public Vector3f getRotation() {
        return rotation;
    }

    public void lock(){
        if(locked){
            locked = false;
            glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }
        else{
            locked = true;
            glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
    }

    public boolean getLocked(){
        return locked;
    }

    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void invertPitch(){
        rotation.x = -rotation.x;
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }

}
