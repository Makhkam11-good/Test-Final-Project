package com.gladiator.managers;

import com.gladiator.strategy.DifficultyStrategy;
import com.gladiator.strategy.MediumDifficulty;

/**
 * GameManager (Singleton) - управляет игровой логикой: сложность, счёт, текущая волна.
 * Фаза 6: Strategy паттерн для управления сложностью.
 */
public class GameManager {
    private static GameManager instance;

    private DifficultyStrategy difficulty;
    private int score;
    private int currentWave;

    private GameManager() {
        this.difficulty = new MediumDifficulty();  // Medium по умолчанию
        this.score = 0;
        this.currentWave = 1;
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    /**
     * Устанавливает стратегию сложности.
     */
    public void setDifficulty(DifficultyStrategy difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Получает текущую стратегию сложности.
     */
    public DifficultyStrategy getDifficulty() {
        return difficulty;
    }

    /**
     * Добавляет очки к текущему счёту.
     */
    public void addScore(int points) {
        this.score += points;
    }

    /**
     * Получает текущий счёт.
     */
    public int getScore() {
        return score;
    }

    /**
     * Получает текущую волну.
     */
    public int getCurrentWave() {
        return currentWave;
    }

    /**
     * Устанавливает текущую волну.
     */
    public void setCurrentWave(int wave) {
        this.currentWave = wave;
    }

    /**
     * Сбрасывает состояние для новой игры:
     * score=0, currentWave=1, difficulty=MediumDifficulty.
     */
    public void reset() {
        this.score = 0;
        this.currentWave = 1;
        this.difficulty = new MediumDifficulty();
    }
}
