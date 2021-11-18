package com.entity.game.client.user;


import com.entity.game.client.display.Window;
import com.entity.game.client.user.settings.Settings;
import org.joml.Vector2d;
import org.joml.Vector2f;


import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private static final Vector2d previousPos = new Vector2d(-1, -1);

    private static final Vector2d currentPos =  new Vector2d(0, 0);

    private static final Vector2f displVec = new Vector2f();

    private static boolean inWindow = false;

    private static boolean leftButtonPressed = false;

    private static boolean rightButtonPressed = false;

    private static boolean rightButtonReleased = false;
    private static boolean leftButtonReleased = false;

    public static double scrollX = 0;
    public static double scrollY = 0;



    public static void resetScroll(){
        scrollY = 0;
        scrollX = 0;
    }

    public static void init(Window window) {
        glfwSetCursorPosCallback(window.getWindow(), (windowHandle, xpos, ypos) -> {
            currentPos.x = xpos;
            currentPos.y = ypos;
        });
        glfwSetCursorEnterCallback(window.getWindow(), (windowHandle, entered) -> {
            inWindow = entered;
        });
        glfwSetMouseButtonCallback(window.getWindow(), (windowHandle, button, action, mode) -> {
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });

        glfwSetScrollCallback(window.getWindow(), (windowHandle, xoffset, yoffset) -> {
            scrollX = xoffset;
            scrollY = yoffset;
        });

    }

    public static Vector2d getCurrentPos() {
        return currentPos;
    }

    public static Vector2f getDisplVec() {
        return displVec;
    }

    public static void input(Window window) {
        displVec.x = 0;
        displVec.y = 0;
        if (previousPos.x != 0 && previousPos.y != 0 && inWindow) {
            double deltax = currentPos.x - previousPos.x;
            double deltay = currentPos.y - previousPos.y;
            boolean rotateX = deltax != 0;
            boolean rotateY = deltay != 0;
            if (rotateX) {
                displVec.y = (float) deltax * Settings.inputSettings.getMouseSensitivity();
            }
            if (rotateY) {
                displVec.x = (float) deltay * Settings.inputSettings.getMouseSensitivity();
            }
        }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public static boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public static boolean isLeftButtonClicked(){
        if(isLeftButtonPressed()){
            leftButtonReleased = true;
            return false;
        }
        else{
            if(leftButtonReleased){
                leftButtonReleased = false;
                return true;
            }
            return false;
        }
    }

    public static boolean isRightButtonClicked(){
        if(isRightButtonPressed()){
            rightButtonReleased = true;
            return false;
        }
        else{
            if(rightButtonReleased){
                rightButtonReleased = false;
                return true;
            }
            return false;
        }
    }

    public static boolean isRightButtonPressed(){
        return rightButtonPressed;
    }
}