package com.entity.game.client.gui.startmenu;

import com.entity.game.client.misc.WorldConstants;
import com.entity.game.client.textures.GuiTexture;
import org.joml.Vector2d;
import org.joml.Vector2f;


public abstract class GuiItem {
    protected Vector2f size;
    protected Vector2f pos;
    protected boolean isHoveredOver;

    public GuiItem(float sizeX, float sizeY, float x, float y) {
        this.size = new Vector2f(sizeX,sizeY);
        this.pos = new Vector2f(x,y);
    }

    public void setHoveredOver(boolean hoveredOver) {
        isHoveredOver = hoveredOver;
    }

    public boolean isHoveredOver() {
        return isHoveredOver;
    }

    public Vector2d getPos(){
        return new Vector2d((pos.x + 1.0) * WorldConstants.WINDOW_WIDTH/2, (1.0 - pos.y) * WorldConstants.WINDOW_HEIGHT/2);
    }


    public Vector2f getSize() {
        return size;
    }

    public Vector2f getPosRelative(){
        return pos;
    }

    public Vector2f getScale(){
        return new Vector2f(size.x, size.y);
    }

    public abstract GuiTexture getGuiTexture();
}
