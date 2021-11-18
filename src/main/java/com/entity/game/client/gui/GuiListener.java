package com.entity.game.client.gui;


import com.entity.game.client.gui.startmenu.Button;
import com.entity.game.client.gui.startmenu.GuiItem;
import com.entity.game.client.events.EventBus;
import com.entity.game.client.events.GUIClickEvent;
import com.entity.game.client.misc.WorldConstants;
import com.entity.game.client.user.MouseInput;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

public class GuiListener {

    private static EventBus guiEventBus;
    private static ArrayDeque<GuiItem> delQue;
    private static ArrayList<GuiItem> guiItems;
    public GuiListener(){

        guiEventBus = new EventBus();
        guiItems = new ArrayList<>();
        delQue = new ArrayDeque<>();
        guiEventBus.subscribe(GUIClickEvent.class, this::onButtonClick);
    }

    public void onButtonClick(GUIClickEvent event){
        if(event.getGuiItem() instanceof Button){
            Button button = (Button) event.getGuiItem();
            button.getGuiTexture().setColour(new Vector4f(0,0,0,0));
        }
    }

    public static EventBus getGuiEventBus() {
        return guiEventBus;
    }

    public static void addNewItem(GuiItem item){
        guiItems.add(item);
    }

    public static void removeItem(GuiItem item){
        delQue.add(item);
    }

    public void listen(){
        Vector2d mPos = MouseInput.getCurrentPos();
        Iterator<GuiItem> itemIterator = guiItems.iterator();
        while (itemIterator.hasNext()){
            GuiItem guiItem = itemIterator.next();
            if(!delQue.isEmpty()){
                if(delQue.getFirst().equals(guiItem)){
                    delQue.removeFirst();
                    itemIterator.remove();
                    continue;
                }
                
            }
            Vector2f itemPos = guiItem.getPosRelative();
            Vector2f itemSize = guiItem.getScale();
            double mX = mPos.x / (WorldConstants.WINDOW_WIDTH/2f) - 1.0;
            double mY = 1.0 - mPos.y / (WorldConstants.WINDOW_HEIGHT/2f);
            if(mX > itemPos.x - itemSize.x && mX < itemPos.x + itemSize.x
                    && mY > itemPos.y - itemSize.y && mY < itemPos.y+itemSize.y){
                guiItem.setHoveredOver(true);
                if(MouseInput.isLeftButtonPressed()){
                    guiEventBus.send(new GUIClickEvent(guiItem, ClickType.LEFT));
                }
                else if(MouseInput.isRightButtonPressed()){
                    guiEventBus.send(new GUIClickEvent(guiItem, ClickType.RIGHT));
                }
            }
            else {
                guiItem.setHoveredOver(false);
            }
        }
    }
}
