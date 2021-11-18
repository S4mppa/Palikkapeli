package com.entity.game.client.user.settings;

import com.entity.game.client.user.ConfigManager;

public class InputSettings {
    private float mouseSensitivity = 1.0f;

    public InputSettings() {
        this.mouseSensitivity = ConfigManager.settings.getFloat("mouse-sensitivity");
    }

    public float getMouseSensitivity() {
        return mouseSensitivity;
    }

    public void setMouseSensitivity(float mouseSensitivity) {
        this.mouseSensitivity = mouseSensitivity;
    }
}
