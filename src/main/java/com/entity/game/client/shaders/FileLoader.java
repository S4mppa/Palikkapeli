package com.entity.game.client.shaders;

import java.io.File;
import java.util.Scanner;

public class FileLoader {
    public static String load(String file){
        String output = "";
        try {
            Scanner scanner = new Scanner( new File(file) );
            output = scanner.useDelimiter("\\A").next();;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return output;
    }
}
