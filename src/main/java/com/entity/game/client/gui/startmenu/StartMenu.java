package com.entity.game.client.gui.startmenu;


import com.entity.game.client.gui.GuiListener;
import com.entity.game.client.gui.GuiRenderer;
import com.entity.game.client.textMesh.FontType;
import com.entity.game.client.textMesh.GUIText;
import com.entity.game.client.textRendering.TextMaster;
import com.entity.game.client.textures.GuiTexture;
import com.entity.game.client.textures.Texture;
import com.entity.game.client.display.Window;
import com.entity.game.client.events.GUIClickEvent;
import org.joml.Vector2f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StartMenu extends PauseMenu{
    private GuiRenderer guiRenderer;
    private static FontType fontType;
    private List<Button> buttons;
    private GUIText title;
    private GuiTexture background;
    public StartMenu(GuiRenderer guiRenderer){
        fontType = new FontType(new Texture("fonts/voxelfont1.png").getID(), new File("fonts/voxelfont.fnt"));
        this.guiRenderer = guiRenderer;
        buttons = new ArrayList<>();
        init();
        GuiListener.getGuiEventBus().subscribe(GUIClickEvent.class, this::onButtonClick);
    }

    public void onButtonClick(GUIClickEvent event){
        if(event.getGuiItem() instanceof Button){
            Button button = (Button) event.getGuiItem();
            if(button.getText().equals("Continue")){
                canContinue = true;
                destroy();
            }
            else if(button.getText().equals("Quit")){
                System.exit(0);
            }
        }
    }

    public void destroy(){
        buttons.forEach(button -> {
            button.destroy(guiRenderer);
            GuiListener.removeItem(button);
        });
        guiRenderer.removeGui(background);
        TextMaster.removeText(title);
        Window.hideCursor(true);
    }

    public static FontType getFont(){
        return fontType;
    }

    public void addButton(Button button){
        guiRenderer.addGui(button.getGuiTexture());
        GuiListener.addNewItem(button);
        buttons.add(button);
    }

    public void init(){
        title = new GUIText("Palikkapeli", 4f, fontType, new Vector2f(0.01f,0.2f), 1f, true);
        title.setColour(0,0,0);
        background = new GuiTexture(new Texture("gui/background.png").getID(), new Vector2f(0, 0f), new Vector2f(1, 1));
        guiRenderer.addGui(background);
        addButton(new Button("Continue", 0f, 0.25f, 0.211f, 0.035f));
        addButton(new Button("Worlds", -0.11f, 0.15f, 0.1f, 0.035f));
        addButton(new Button("Settings", 0.11f, 0.15f, 0.1f, 0.035f));
        addButton(new Button("Quit", 0f, 0.05f, 0.1f, 0.035f));
    }
}
