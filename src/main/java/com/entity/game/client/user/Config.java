package com.entity.game.client.user;

import org.joml.Vector3f;

import java.io.*;
import java.util.Properties;

public class Config extends Properties {
    private final static String DATA_FOLDER = "settings/";
    private final File file;

    public Config(Properties defaults, String path) throws Exception{
        super(defaults);
        this.file = new File(DATA_FOLDER, path);
        load();
        save();
    }

    public static Config initConfig(String path) {
        try {
            Properties properties = loadDefaults(DATA_FOLDER+path);
            return new Config(properties, path);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void load() throws Exception {
        if(!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        for(Object key : defaults.keySet()){
            setProperty(key.toString(), defaults.getProperty(key.toString()));
        }
        load(new FileInputStream(file.getAbsolutePath()));
    }

    public static Properties loadDefaults(String path) throws Exception{
        InputStream in = Config.class.getResourceAsStream("/" + path);
        Properties defaults = new Properties();
        defaults.load(in);
        return defaults;
    }

    public void save() throws IOException {
        if(!file.exists()) {
            if(!file.createNewFile()) return;
        }

        FileOutputStream stream = new FileOutputStream(file.getAbsolutePath());
        store(stream, "");
        stream.close();
    }

    public int getInt(String key){
        return Integer.parseInt(getProperty(key));
    }

    public boolean getBoolean(String key){
        return Boolean.parseBoolean(getProperty(key));
    }

    public double getDouble(String key){
        return Double.parseDouble(getProperty(key));
    }
    public float getFloat(String key){
        return Float.parseFloat(getProperty(key));
    }

    public Vector3f getVector3(String key){
        String[] split = getProperty(key).split(",");
        float v1 = Float.parseFloat(split[0].trim());
        float v2 = Float.parseFloat(split[1].trim());
        float v3 = Float.parseFloat(split[2].trim());
        return new Vector3f(v1,v2,v3);
    }
}

