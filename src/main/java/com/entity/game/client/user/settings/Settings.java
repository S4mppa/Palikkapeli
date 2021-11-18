package com.entity.game.client.user.settings;


import com.entity.game.client.Main;
import com.entity.game.client.Scheduler;
import com.entity.game.client.events.SettingsLoadEvent;

public abstract class Settings {
    public static boolean loaded = false;
    public static WindowSettings windowSettings;
    public static InputSettings inputSettings;
    public static UserSettings userSettings;
    public static void init(){
        windowSettings = new WindowSettings();
        inputSettings = new InputSettings();
        userSettings = new UserSettings();
        Main.send(new SettingsLoadEvent());
    }
}


