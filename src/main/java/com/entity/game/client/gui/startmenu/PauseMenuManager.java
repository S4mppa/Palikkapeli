package com.entity.game.client.gui.startmenu;



import com.entity.game.client.display.Window;

public class PauseMenuManager {
    private static PauseMenu currentMenu;
    private static boolean canContinue = false;
    public static void setCurrentMenu(PauseMenu pauseMenu){
        Window.hideCursor(false);
        currentMenu = pauseMenu;
    }
    public static boolean canContinue(){
        if(currentMenu == null) return true;
        return currentMenu.canContinue();
    }
}
