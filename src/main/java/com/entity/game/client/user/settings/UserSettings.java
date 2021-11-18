package com.entity.game.client.user.settings;

import com.entity.game.client.user.ConfigManager;
import org.joml.Vector3f;

public class UserSettings extends Settings{
    private Vector3f lastPos;
    private String lastWorld;

    public UserSettings(){
        this.lastWorld = ConfigManager.user.getProperty("last-world");
        this.lastPos = ConfigManager.user.getVector3("last-pos");
    }

    public String getLastWorld() {
        return lastWorld;
    }

    public Vector3f getLastPos() {
        return lastPos;
    }
}
