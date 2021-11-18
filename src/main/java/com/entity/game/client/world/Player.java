package com.entity.game.client.world;


import com.entity.game.client.gui.GuiRenderer;
import com.entity.game.client.misc.PlayerGui;
import com.entity.game.client.misc.Ray;
import com.entity.game.client.misc.Utils;
import com.entity.game.client.misc.WorldConstants;
import com.entity.game.client.physics.CollisionResponse;
import com.entity.game.client.physics.Contact;
import com.entity.game.client.physics.Hitbox;
import com.entity.game.client.display.Window;
import com.entity.game.client.renderEngine.Camera;
import com.entity.game.client.user.MouseInput;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;


import static org.lwjgl.glfw.GLFW.*;

public class Player {
    private static final float FLYING_SPEED = 0.024f;
    private static final float WALKING_SPEED = 0.1f;

    private static final float FLYING_DRAG = 0.015f;
    private static final float WALKING_DRAG = 0.07f;

    private static float MAX_FLYING_SPEED = 1.4f;
    private static float MAX_WALKING_SPEED = 1.1f;

    private final Camera camera;
    private final Vector3f vel;

    private final Window window;
    private PlayerGui playerGui;
    private static float GRAVITY = 0.04f;
    private static final float CAMERA_POS_STEP = 0.05f;
    private static final float MOUSE_SENSITIVITY = 0.2f;


    private float jumpForce = 1.5f;
    private boolean inAir = true;
    private boolean isInWater = false;
    private boolean lastFrameInWater = false;
    private boolean jumped = false;
    private float jumpRadians = jumpForce;
    private boolean flightEnabled = true;
    private int currentBlock = 0;

    private World world;

    public Player(World world, Camera camera, Window window) {
        this.camera = camera;
        this.world = world;
        vel = new Vector3f();

        this.window = window;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public int getBlockBelowPlayer(){
        BlockPosition bPos = camera.getBlockPos();
        bPos.y -= 2;
        return world.getChunkManager().getAnyBlockAt(bPos);
    }

    public void initGui(GuiRenderer guiRenderer){
        playerGui = new PlayerGui(guiRenderer);
    }

    public void addForce(Vector3f force){
        float maxCameraSpeed = flightEnabled ? MAX_FLYING_SPEED : MAX_WALKING_SPEED;
        if(Math.abs(vel.x+force.x) > maxCameraSpeed) return;
        if(Math.abs(vel.y+force.y) > (flightEnabled ? MAX_FLYING_SPEED : 3)) return;
        if(Math.abs(vel.z+force.z) > maxCameraSpeed) return;
        vel.add(force);
    }

    public void input() {
        MouseInput.input(window);

        float cameraSpeedInc = flightEnabled ? FLYING_SPEED : WALKING_SPEED;

        if(WorldConstants.I_MOVE){
            addForce(new Vector3f(0,0,-cameraSpeedInc / 3));
        }

        if (Window.isKeyPressed(GLFW_KEY_W)) {
            addForce(new Vector3f(0, 0,-cameraSpeedInc));
        } else if (Window.isKeyPressed(GLFW_KEY_S)) {
            addForce(new Vector3f(0, 0,cameraSpeedInc));
        }

        if (Window.isKeyPressed(GLFW_KEY_A)) {
            addForce(new Vector3f(-cameraSpeedInc, 0,0));

        } else if (Window.isKeyPressed(GLFW_KEY_D)) {
            addForce(new Vector3f(cameraSpeedInc, 0,0));
        }

        if (Window.isKeyPressed(GLFW_KEY_F)) {
            if(flightEnabled) MAX_FLYING_SPEED *= 1.01;
            else MAX_WALKING_SPEED *= 1.01;

        }
        if (Window.isKeyPressed(GLFW_KEY_C)) {
            if(flightEnabled) MAX_FLYING_SPEED *= 0.99;
            else MAX_WALKING_SPEED *= 0.99;
        }

        if(Window.isKeyPressed(GLFW_KEY_N)){
            flightEnabled = !flightEnabled;
        }

        if (Window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            addForce(new Vector3f(0,-cameraSpeedInc, 0));
        } else if (Window.isKeyPressed(GLFW_KEY_SPACE) && !inAir) {
            if(!flightEnabled) {
                addForce(new Vector3f(0, jumpForce,0));
                inAir = true;
            }
            else {
                addForce(new Vector3f(0, cameraSpeedInc, 0));
            }

        }

        if(Window.isKeyPressed(GLFW_KEY_SPACE) && flightEnabled){
            addForce(new Vector3f(0, cameraSpeedInc, 0));
        }

        if(Window.isKeyPressed(GLFW_KEY_Z)){
            WorldConstants.FOV = (float) Math.toRadians(20.0f);
        }
        else {
            WorldConstants.FOV = (float) Math.toRadians(60.0f);
        }
        if(Window.isKeyPressed(GLFW_KEY_P)){
            WorldConstants.NO_GUI = !WorldConstants.NO_GUI;
        }
        if(Window.isKeyPressed(GLFW.GLFW_KEY_J)){
            Utils.terraform(this, false);
        }
        else if(Window.isKeyPressed(GLFW.GLFW_KEY_G)){
            Utils.terraform(this, true);
        }

        //collision(new Vector3f(cameraInc.x,cameraInc.y,cameraInc.z));
        if (Window.isKeyPressed(GLFW_KEY_ESCAPE) && MouseInput.isLeftButtonClicked()) {
            camera.lock();
        }
        if (MouseInput.isLeftButtonClicked()) {
            Vector3f ray = Ray.getSightVector(camera.getRotation());
            BlockPosition[] points = Ray.testPoint(world.getChunkManager(), ray, camera.getPosition());
            if (points != null) {
                BlockUpdate blockUpdate = new BlockUpdate(points[0].x, points[0].y, points[0].z, 0);
                world.getChunkManager().addBlockUpdate(blockUpdate);
            }

        }
        if (MouseInput.isRightButtonClicked()) {
            Vector3f ray = Ray.getSightVector(camera.getRotation());
            BlockPosition[] points = Ray.testPoint(world.getChunkManager(), ray, camera.getPosition());
            if (points != null) {
                BlockUpdate blockUpdate = new BlockUpdate(points[1].x, points[1].y, points[1].z, playerGui.blockIDs[currentBlock]);
                world.getChunkManager().addBlockUpdate(blockUpdate);
            }
        }
        if(MouseInput.scrollY == 1){
            changeBlock(-(36f/window.getWidth())*2, -1);
        }
        else if(MouseInput.scrollY == -1){
            changeBlock((36f/window.getWidth())*2, 1);
        }
        MouseInput.resetScroll();
    }

    public void changeBlock(float selectorOffsetX, int blockOffset){
        playerGui.moveSelector(selectorOffsetX);
        if(currentBlock+blockOffset >= 0 && currentBlock+blockOffset <= playerGui.hotbarSize-1){
            currentBlock += blockOffset;
        }
    }

    public void velocityDirection(Vector3f velocityDirection, BlockPosition blockPosition){
        Vector3f vel2 = new Vector3f(velocityDirection).mul(-1);
        Vector3f[] normalsArray = new Vector3f[]{
                new Vector3f(0.0f,1.0f,0.0f),  // Up
                new Vector3f(1.0f,0.0f,0.0f),//Right
                new Vector3f(0.0f,-1.0f,0.0f), // Down
                new Vector3f(-1.0f,0.0f,0.0f), //Left
                new Vector3f(0.0f,0.0f,1.0f), //Front
                new Vector3f(0.0f,0.0f,-1.0f) //Back
        };
        Vector3f bestMatch = new Vector3f();
        float max = 0;
        for (Vector3f normal : normalsArray) {
            float dot = vel2.dot(normal);
            if (dot > max) {
                max = dot;
                bestMatch = normal;
            }
        }
        System.out.println(bestMatch);

        if(bestMatch.length() == 0) return;

        vel2.add(bestMatch.mul(velocityDirection));
    }



    public void rawPosMove(Vector3f move){
        camera.getPosition().add(move);
    }


    Vector3f lastCamPos = new Vector3f();
    double lastTime = 0;
    public boolean collision() {
        Vector3f camPos = Utils.floorVector(camera.getPosition());
        for(int x = -2; x <= 2; x++){
            for(int z = -2; z <= 2; z++){
                for(int y = -2; y <= 2; y++){
                    BlockPosition blockPosition = new BlockPosition((int)camPos.x+x,(int)camPos.y+y,(int)camPos.z+z);
                    if(world.getChunkManager().getBlockDataAt(blockPosition).isCollidable()){
                        Hitbox hb1 = new Hitbox(Utils.blockPosToVector(blockPosition), new Vector3f(blockPosition.x+1, blockPosition.y+1, blockPosition.z+1));
                        Hitbox hb2 = new Hitbox(new Vector3f(camera.getPosition().x-0.3f, camera.getPosition().y-1.8f, camera.getPosition().z-0.3f), new Vector3f(camera.getPosition().x+0.3f, camera.getPosition().y, camera.getPosition().z+0.3f));

                        CollisionResponse collisionResponse = new CollisionResponse();
                        Contact contact = new Contact();
                        if(collisionResponse.AABBAABB(hb1,hb2, contact)){
                            if(contact.nEnter.y == -1 && vel.y < 0){
                                inAir = false;
                                vel.y = 0;
                                camera.getPosition().y += (hb1.m_vecMax.y - (camera.getPosition().y-1.8f));
                            }
                            else {
                                camera.getPosition().sub(contact.nEnter.mul(contact.penetration));
                            }

                        }

                    }
                }

            }
        }
        lastTime = System.currentTimeMillis();
        lastCamPos = new Vector3f(camera.getPosition());
        return false;
    }


    public boolean isInWater() {
        return isInWater;
    }

    public void updateCamera() {
        //+ (camera.getRotation().x * cameraInc.z/1000
        if (Math.abs(vel.x) < 0.02) vel.x = 0;
        if (Math.abs(vel.y) < 0.02) vel.y = 0;
        if (Math.abs(vel.z) < 0.02) vel.z = 0;


        if(world.getChunkManager().getAnyBlockAt(camera.getBlockPos()) == 6
                && camera.getPosition().y < WorldConstants.WATER_LEVEL){ //if in water
            jumpForce = 0.75f;
            GRAVITY = 0.022f;
            isInWater = true;
        }
        else {
            isInWater = false;
        }
        if(lastFrameInWater && !isInWater){
            jumpForce = 1.5f;
            GRAVITY = 0.04f;
        }

        lastFrameInWater = isInWater;

        if(new Vector3f(vel).mul(1,0,1).length() > 0 && !flightEnabled){
            Vector3f drag = new Vector3f(vel.x,0, vel.z);
            drag.normalize();
            float c = -WALKING_DRAG;
            drag.mul(c);
            addForce(drag);
        }
        else if(vel.length() > 0 && flightEnabled){
            Vector3f drag = new Vector3f(vel);
            drag.normalize();
            float c = -FLYING_DRAG;
            drag.mul(c);
            addForce(drag);
        }

        camera.movePosition(vel.x * CAMERA_POS_STEP,
                vel.y * CAMERA_POS_STEP,
                vel.z * CAMERA_POS_STEP);

        // Update camera based on mouse
        if (!camera.getLocked()) {
            Vector2f rotVec = MouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

    }

    public void update(){
        if(!flightEnabled) addForce(new Vector3f(0, -GRAVITY, 0));
        updateCamera();
        input();
        collision();
    }

}
