package com.entity.game.client.user.settings;

import com.entity.game.client.user.ConfigManager;

public class WindowSettings {
    private int width;
    private int height;
    private boolean fullscreen;

    public WindowSettings() {
        this.width = ConfigManager.settings.getInt("window-width");
        this.height = ConfigManager.settings.getInt("window-height");
        this.fullscreen = ConfigManager.settings.getBoolean("window-fullscreen");
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
