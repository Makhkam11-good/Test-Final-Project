package com.gladiator.events;

/**
 * GameEvent (Observer паттерн) - событие, которое публикует различные системы игры.
 */
public class GameEvent {

    public enum Type {
        ENEMY_DIED, WAVE_CLEARED, PLAYER_HURT, PLAYER_DIED, BOSS_DIED
    }

    private final Type type;
    private final Object payload;

    public GameEvent(Type type) {
        this(type, null);
    }

    public GameEvent(Type type, Object payload) {
        this.type = type;
        this.payload = payload;
    }
    
    /**
     * Получает тип события.
     */
    public Type getType() {
        return type;
    }
    
    /**
     * Получает payload события с безопасным приведением типа.
     */
    @SuppressWarnings("unchecked")
    public <T> T getPayload(Class<T> clazz) {
        if (payload == null) {
            return null;
        }
        if (clazz.isInstance(payload)) {
            return (T) payload;
        }
        throw new ClassCastException("Payload is not of type " + clazz.getName());
    }
    
    /**
     * Проверяет есть ли payload.
     */
    public boolean hasPayload() {
        return payload != null;
    }
}
