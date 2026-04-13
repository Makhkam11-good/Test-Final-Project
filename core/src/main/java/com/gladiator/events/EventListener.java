package com.gladiator.events;

/**
 * EventListener - слушатель событий в системе Observer паттерна.
 */
@FunctionalInterface
public interface EventListener {
    void onEvent(GameEvent event);
}
