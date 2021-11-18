package com.entity.game.client.misc;


import com.entity.game.client.gui.GuiRenderer;
import com.entity.game.client.textures.GuiTexture;
import com.entity.game.client.textures.Texture;
import org.joml.Vector2f;

public class PlayerGui {
    private GuiTexture hotbar;
    private GuiTexture selector;
    private GuiTexture crosshair;
    private GuiRenderer guiRenderer;
    private float iconSize = 32;
    private int cornerOffSet = 2;
    private float wHeight = WorldConstants.WINDOW_HEIGHT;
    private float wWidth = WorldConstants.WINDOW_WIDTH;
    public int hotbarSize = 9;
    public int[] blockIDs = {
            1,2,3,4,5,7,8,10,11
    };
    private String[] textures = {
            "grass_block_side.png",
            "stone.png",
            "dirt.png",
            "sand.png",
            "grass_block_snow.png",
            "cobblestone.png",
            "spruce_log.png",
            "oak_planks.png",
            "glass.png",
    };

    private void loadBlockTextures(){
        for(int i = 1; i <= blockIDs.length; i++) {
            Texture blockT = new Texture("blocks/x16/" + textures[i - 1]);
            GuiTexture block = new GuiTexture(blockT.getID(), new Vector2f(((-324f / wWidth + (36f / wWidth) * (i+i)-(36f / wWidth))), -0.8f - (cornerOffSet/wHeight)), new Vector2f(iconSize / wWidth, iconSize / wHeight));
            guiRenderer.addGui(block);
        }
    }

    public PlayerGui(GuiRenderer guiRenderer){
        Texture selectorT = new Texture("gui/selector.png");
        Texture hotbarT = new Texture("gui/hotbar.png");
        Texture crossT = new Texture("gui/crosshair.png");
        selector = new GuiTexture(selectorT.getID(), new Vector2f(-324f / wWidth + (36f / wWidth), -0.8f), new Vector2f(36f/wWidth,36f/wHeight));
        hotbar = new GuiTexture(hotbarT.getID(), new Vector2f(0f, -0.8f), new Vector2f(324f/wWidth,36f/wHeight));
        crosshair = new GuiTexture(crossT.getID(), new Vector2f(0f, 0f), new Vector2f(36/wWidth,36f/wHeight));
        this.guiRenderer = guiRenderer;
        guiRenderer.addGui(hotbar);
        loadBlockTextures();
        guiRenderer.addGui(selector);
        guiRenderer.addGui(crosshair);
    }

    public void moveSelector(float offsetX){
        if(selector.getPosition().x + offsetX > -324f/wWidth &&selector.getPosition().x + offsetX < 324f/wWidth){
            selector.setPosition(new Vector2f(selector.getPosition().x+offsetX, selector.getPosition().y));
        }
    }
}
