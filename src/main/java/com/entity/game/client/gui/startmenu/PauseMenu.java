package com.entity.game.client.gui.startmenu;

public abstract class PauseMenu {
    protected boolean canContinue = false;
    public boolean canContinue(){
        return canContinue;
    }

    public abstract void destroy();
}
