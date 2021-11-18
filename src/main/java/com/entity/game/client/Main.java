package com.entity.game.client;

import com.entity.game.client.gui.GuiListener;
import com.entity.game.client.gui.GuiRenderer;
import com.entity.game.client.gui.startmenu.PauseMenuManager;
import com.entity.game.client.gui.startmenu.StartMenu;
import com.entity.game.client.misc.DisplayTime;
import com.entity.game.client.misc.Ray;
import com.entity.game.client.misc.Utils;
import com.entity.game.client.misc.WorldConstants;
import com.entity.game.client.skybox.SkyboxRenderer;
import com.entity.game.client.textMesh.FontType;
import com.entity.game.client.textMesh.GUIText;
import com.entity.game.client.textRendering.TextMaster;
import com.entity.game.client.textures.Texture;
import com.entity.game.client.display.Window;
import com.entity.game.client.events.*;
import com.entity.game.client.world.*;
import com.entity.game.client.postProcessing.Fbo;
import com.entity.game.client.postProcessing.PostProcessing;
import com.entity.game.client.renderEngine.*;
import com.entity.game.client.user.ConfigManager;
import com.entity.game.client.user.MouseInput;
import com.entity.game.client.user.settings.Settings;
import com.entity.game.client.voxelhandling.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.UUID;

public class Main extends EventBus {
    private ConfigManager configManager;
    private Window window;

    private Loader loader;
    public static Player player;
    private GuiRenderer guiRenderer;
    private Camera camera;

    private Fbo postProcessedScreen;
    private WorldGenThread genThread;
    private HighlightRenderer highlightRenderer;
    private SkyboxRenderer skyboxRenderer;
    private MainRenderer mainRenderer;
    private GuiListener guiListener;
    private EventBus eventBus;

    public Main(){
        this.configManager = new ConfigManager();
        this.eventBus = new EventBus();
        subscribe(SettingsLoadEvent.class, this::onSettingsLoad);
        this.window = new Window();
        Settings.init();
    }

    public void onSettingsLoad(SettingsLoadEvent event){
        System.out.println("Settings loaded..");
        WorldConstants.WINDOW_HEIGHT = Settings.windowSettings.getHeight();
        WorldConstants.WINDOW_WIDTH = Settings.windowSettings.getWidth();
        Settings.loaded = true;
    }


    public void onWindowCreate() throws Exception {
        window.createWindow();
        loader = new Loader();
        MouseInput.init(window);
        PostProcessing.init(loader, window, camera);
        guiListener = new GuiListener();
        guiRenderer = new GuiRenderer(loader);
        postProcessedScreen = new Fbo(window.getWidth(), window.getHeight(), Fbo.DEPTH_RENDER_BUFFER, window);
        camera = new Camera(window);
        TextMaster.init(loader);
    }

    public void initGame() throws Exception{
        CameraManager.setActiveCamera(camera);
        player = new Player(new World(eventBus, Settings.userSettings.getLastWorld()), camera, window);
        new MainListener(eventBus);
        genThread = new WorldGenThread(player, window);

        //new ChunkGenThread(window, chunkManager).start();
        camera.setPosition(Settings.userSettings.getLastPos());
        camera.setRotation(0,0,0);
        highlightRenderer = new HighlightRenderer(loader);
        skyboxRenderer = new SkyboxRenderer(loader);
        mainRenderer = new MainRenderer(player, window, skyboxRenderer, highlightRenderer, guiRenderer, loader);
    }

    public void run() throws Exception {

        onWindowCreate();
        initGame();
        FontType font = new FontType(new Texture("fonts/voxelfont1.png").getID(), new File("fonts/voxelfont.fnt"));
        GUIText fps = new GUIText("", 1, font, new Vector2f(0.01f,0.02f), 1f, false);
        GUIText position = new GUIText("", 1, font, new Vector2f(0.01f,0.06f), 1f, false);
        GUIText velocity = new GUIText("", 1, font, new Vector2f(0.01f,0.1f), 1f, false);
        GUIText chunkUpdates = new GUIText("", 1, font, new Vector2f(0.01f,0.14f), 1f, false);
        GUIText biome = new GUIText("", 1, font, new Vector2f(0.01f,0.18f), 1f, false);
        GUIText time = new GUIText("", 1, font, new Vector2f(0.01f,0.22f), 1f, false);
        GUIText worldT = new GUIText("", 1, font, new Vector2f(0.01f,0.26f), 1f, false);
        Vector3f lastCamPos = camera.getPosition();


        long lastTime = System.nanoTime();
        double amountOfTicks = 144.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;

        boolean initialized = false;
        PauseMenuManager.setCurrentMenu(new StartMenu(guiRenderer));
        while(!window.shouldClose())
        {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            double frameTime = (now - lastTime) / 1000000000f;
            lastTime = now;
            while(delta >=1)
            {

                window.update();
                if(Window.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)){
                    PauseMenuManager.setCurrentMenu(new StartMenu(guiRenderer));
                    eventBus.send(new GameCloseEvent());
                }
                if(Window.isKeyPressed(GLFW.GLFW_KEY_0)){
                    World world = new World(eventBus, UUID.randomUUID().toString().substring(0,5));
                    player.setWorld(world);
                }
                guiListener.listen();
                if(PauseMenuManager.canContinue()){
                    update(frameTime);
                }
                delta--;

            }
            if(!window.shouldClose()){
                //renderEntities()
                if(PauseMenuManager.canContinue()){
                    if(!initialized){
                        genThread.start();
                        player.initGui(guiRenderer);
                        initialized = true;
                    }
                    player.getWorld().getChunkManager().update();
                    mainRenderer.update();
                    mainRenderer.render();
                }
                else {
                    mainRenderer.renderMenuScreen();
                }
            }
            frames++;

            if(System.currentTimeMillis() - timer > 1000)
            {

                timer += 1000;
                if(!PauseMenuManager.canContinue()) continue;
                fps.resetString("FPS: " + frames);
                position.resetString(String.format("Position: %d, %d, %d", (int) camera.getPosition().x,(int)camera.getPosition().y,(int)camera.getPosition().z));
                velocity.resetString(String.format("Velocity: %d b/s", (int) lastCamPos.distance(camera.getPosition())));
                chunkUpdates.resetString(String.format("Chunk updates: %d", player.getWorld().getChunkManager().getChunkUpdateList().size()));
                biome.resetString(String.format("Biome: %s", player.getWorld().getTerrainGeneration().getBiome((int)camera.getPosition().x, (int)camera.getPosition().z)));
                worldT.resetString("World: " + player.getWorld().getName());

                long t = player.getWorld().getTime();
                int h = (int) (t / 1000);
                int m = (int) (((t % 1000) / 1000f) * 60);

                time.resetString("Time: " + h+":"+m);
                frames = 0;
                lastCamPos = new Vector3f(camera.getPosition());

            }
        }

        TextMaster.cleanUp();
        for(Chunk c : player.getWorld().getChunkManager().getAllChunks()){
            c.getFluidChunkMesh().freeMemory();
            c.getChunkMesh().freeMemory();
        }
    }

    public void update(double delta){

        player.update();
        //skyboxRenderer.updateRotation();
        highlightRenderer.updateRay(player.getWorld().getChunkManager());
        DisplayTime.refreshTime(delta);
        for(Chunk chunk : player.getWorld().getChunkManager().getRenderArray()){
            chunk.animTime += DisplayTime.getTime();
            if(chunk.animTime > 150){
                chunk.animTime = 150;
            }
        }
    }

    public static void main(String[] args){
        try {
            new Main().run();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}


