package com.gladiator.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/**
 * GameStateManager controls screen transitions using a state stack.
 */
public class GameStateManager {

    public enum State {
        MENU, GAME, UPGRADE, GAME_OVER, VICTORY
    }

    private final Game game;
    private final Stack<State> stateStack = new Stack<>();
    private final Map<State, Screen> screens = new HashMap<>();

    public GameStateManager(Game game) {
        this.game = game;
    }

    public void registerScreen(State state, Screen screen) {
        Screen previous = screens.put(state, screen);
        if (previous != null && previous != screen && getCurrentState() != state) {
            previous.dispose();
        }
    }

    public void push(State state) {
        stateStack.push(state);
        switchScreen(state);
    }

    public void pop() {
        if (!stateStack.isEmpty()) {
            stateStack.pop();
        }
        if (!stateStack.isEmpty()) {
            switchScreen(stateStack.peek());
        }
    }

    public void set(State state) {
        stateStack.clear();
        stateStack.push(state);
        switchScreen(state);
    }

    private void switchScreen(State state) {
        Screen screen = screens.get(state);
        if (screen != null) {
            game.setScreen(screen);
        }
    }

    public State getCurrentState() {
        return stateStack.isEmpty() ? null : stateStack.peek();
    }
}
