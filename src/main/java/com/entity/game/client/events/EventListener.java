package com.entity.game.client.events;

public interface EventListener<T extends Event> {
    void handle(T event);
}
