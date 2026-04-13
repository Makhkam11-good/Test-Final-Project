package com.gladiator.entities.states;

/**
 * PlayerState (State паттерн) - интерфейс для состояний Рыцаря.
 */
public interface PlayerState {
    void enter();
    void update(float delta);
    void exit();
}
