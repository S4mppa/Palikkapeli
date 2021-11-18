package com.entity.game.client.user;

import java.io.IOException;

public class ConfigManager {
    public static Config settings = Config.initConfig("settings.properties");
    public static Config user = Config.initConfig("user.properties");

    public static void saveAll() {
        try {
            settings.save();
            user.save();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
