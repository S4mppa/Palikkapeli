package com.entity.game.client.display;


import com.entity.game.client.user.settings.Settings;
import com.entity.game.client.user.settings.WindowSettings;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.glfw.GLFW.*;


public class Window {
    private static long window;
    private final String TITLE;
    private static int WIDTH,HEIGHT;

    public Window(){
        this.TITLE = "VoxelRealms";
    }

    public void createWindow(){
        WindowSettings wSettings = Settings.windowSettings;
        GLFWErrorCallback.createPrint(System.err).set();
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_SAMPLES, 8);
        WIDTH = wSettings.getWidth();
        HEIGHT = wSettings.getHeight();
        window = glfwCreateWindow(wSettings.getWidth(), wSettings.getHeight(), TITLE, wSettings.isFullscreen() ? glfwGetPrimaryMonitor() : 0, 0);
        if(window == 0){
            throw new RuntimeException("Failed to create the GLFW window");
        }

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
                window,
                (vidmode.width() - wSettings.getWidth())/2,
                (vidmode.height() - wSettings.getHeight())/2
        );
        glfwMakeContextCurrent(window);
        glfwShowWindow(window);
        glfwSetCursorPos(window, wSettings.getWidth()/2, wSettings.getHeight()/2);
        GL.createCapabilities();
        GL30.glEnable(GL11.GL_DEPTH_TEST);
        GL30.glEnable(GL30.GL_CULL_FACE);
        //glfwSwapInterval(1);
        GL30.glEnable(GL30.GL_CLIP_DISTANCE0);
        GL30.glCullFace(GL11.GL_BACK);
        //GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        //GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK,GL30.GL_LINE);
    }

    public boolean shouldClose(){
        return glfwWindowShouldClose(window);
    }

    public void swapBuffers(){
        glfwSwapBuffers(window);
    }

    public void update(){
        glfwPollEvents();
    }

    public static void hideCursor(boolean hide){
        int code = hide ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL;
        glfwSetInputMode(window, GLFW_CURSOR, code);
    }

    public static boolean isKeyPressed(int keyCode) {
        return glfwGetKey(window, keyCode) == GLFW_PRESS;
    }
    public static int getWidth() {
        return WIDTH;
    }

    public static int getHeight() {
        return HEIGHT;
    }

    public String getTitle() {
        return TITLE;
    }

    public long getWindow(){
        return window;
    }
}

