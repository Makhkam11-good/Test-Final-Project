package com.gladiator.events;

/**
 * GameEvent (Observer паттерн) - событие, которое публикует различные системы игры.
 */
public class GameEvent {

    public enum Type {
        ENEMY_DIED, WAVE_CLEARED, PLAYER_HURT, PLAYER_DIED, BOSS_DIED
    }

    public Type type;
    public Object data;

    public GameEvent(Type type) {
        this.type = type;
    }

    public GameEvent(Type type, Object data) {
        this.type = type;
        this.data = data;
    }
}
