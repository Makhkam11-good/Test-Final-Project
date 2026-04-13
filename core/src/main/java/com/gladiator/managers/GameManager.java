package com.gladiator.managers;

/**
 * GameManager (Singleton) - управляет игровой логикой: сложность, счёт, текущая волна.
 */
public class GameManager {
    private static GameManager instance;

    private GameManager() {
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }
}
