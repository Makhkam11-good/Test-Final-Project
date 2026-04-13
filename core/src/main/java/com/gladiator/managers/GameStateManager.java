package com.gladiator.managers;

/**
 * GameStateManager (State паттерн) - управляет переходами между экранами/состояниями игры.
 */
public class GameStateManager {

    public enum State {
        MENU, GAME, UPGRADE, GAME_OVER, VICTORY
    }

    public void push(State state) {
        // TODO: Реализовать в Фазе 2
    }

    public void pop() {
        // TODO: Реализовать в Фазе 2
    }

    public void set(State state) {
        // TODO: Реализовать в Фазе 2
    }
}
