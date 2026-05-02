package com.gladiator.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EventBus (Singleton + Observer) - центральная система обмена событиями между компонентами.
 */
public class EventBus {
    private static EventBus instance;
    private final Map<GameEvent.Type, List<EventListener>> listeners = new HashMap<>();

    private EventBus() {
    }

    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    /**
     * Подписать слушателя на событие определённого типа.
     */
    public void subscribe(GameEvent.Type type, EventListener listener) {
        listeners.computeIfAbsent(type, k -> new ArrayList<>()).add(listener);
    }

    /**
     * Отписать слушателя от события.
     */
    public void unsubscribe(GameEvent.Type type, EventListener listener) {
        List<EventListener> list = listeners.get(type);
        if (list != null) {
            list.remove(listener);
        }
    }

    /**
     * Publish a game event to all current listeners.
     */
    public void post(GameEvent event) {
        List<EventListener> list = listeners.get(event.getType());
        if (list == null) {
            return;
        }
        List<EventListener> snapshot = new ArrayList<>(list);
        for (int i = 0; i < snapshot.size(); i++) {
            snapshot.get(i).onEvent(event);
        }
    }

    /**
     * Очистить все подписчики (при выходе из игры).
     */
    public void clear() {
        listeners.clear();
    }
}
