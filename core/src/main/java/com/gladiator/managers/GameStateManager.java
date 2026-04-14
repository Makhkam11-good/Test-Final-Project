package com.gladiator.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.gladiator.entities.Player;

/**
 * GameStateManager (State паттерн) - управляет переходами между экранами/состояниями игры.
 * Хранит стек состояний и переключает экраны через game.setScreen().
 */
public class GameStateManager {

    public enum State {
        MENU, GAME, UPGRADE, GAME_OVER, VICTORY
    }

    private Game game;
    private Stack<State> stateStack = new Stack<>();
    private Map<State, Screen> screens = new HashMap<>();
    private Player currentPlayer;  // Фаза 7: ссылка на текущего игрока для UpgradeScreen

    public GameStateManager(Game game) {
        this.game = game;
    }

    /**
     * Регистрирует отображение для состояния.
     */
    public void registerScreen(State state, Screen screen) {
        screens.put(state, screen);
    }

    /**
     * Добавляет состояние в стек и переключает экран.
     */
    public void push(State state) {
        stateStack.push(state);
        switchScreen(state);
    }

    /**
     * Удаляет текущее состояние из стека и переключается на предыдущее.
     */
    public void pop() {
        if (!stateStack.isEmpty()) {
            stateStack.pop();
        }
        if (!stateStack.isEmpty()) {
            switchScreen(stateStack.peek());
        }
    }

    /**
     * Заменяет текущее состояние на новое (очищает стек и добавляет новое).
     */
    public void set(State state) {
        stateStack.clear();
        stateStack.push(state);
        switchScreen(state);
    }

    /**
     * Переключает экран к заданному состоянию.
     */
    private void switchScreen(State state) {
        Screen screen = screens.get(state);
        if (screen != null) {
            game.setScreen(screen);
        }
    }

    /**
     * Получить текущее состояние.
     */
    public State getCurrentState() {
        return stateStack.isEmpty() ? null : stateStack.peek();
    }

    /**
     * Установить текущего игрока. Используется GameScreen для UpgradeScreen (Фаза 7).
     */
    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    /**
     * Получить текущего игрока. Используется UpgradeScreen (Фаза 7).
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
}
