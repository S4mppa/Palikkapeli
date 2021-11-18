package com.entity.game.client;

public abstract class Scheduler {
    public static void runAsync(Runnable runnable){
        new Thread(runnable).start();
    }
}
