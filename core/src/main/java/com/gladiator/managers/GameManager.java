package com.gladiator.managers;

import com.gladiator.strategy.DifficultyStrategy;
import com.gladiator.strategy.MediumDifficulty;

/**
 * GameManager (Singleton) holds run state and difficulty strategy.
 */
public class GameManager {
    private static GameManager instance;

    private DifficultyStrategy difficulty;
    private int score;
    private int currentWave;
    private int enemiesKilled;
    private int upgradesCollected;
    private float timeSurvived;
    private String lastDeathCause;

    private GameManager() {
        this.difficulty = new MediumDifficulty();
        resetRun();
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void startNewRun(DifficultyStrategy difficulty) {
        this.difficulty = difficulty;
        resetRun();
    }

    public void resetRun() {
        score = 0;
        currentWave = 1;
        enemiesKilled = 0;
        upgradesCollected = 0;
        timeSurvived = 0f;
        lastDeathCause = "";
    }

    public DifficultyStrategy getDifficulty() {
        return difficulty;
    }

    public void addScore(int points) {
        score += points;
    }

    public int getScore() {
        return score;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public void setCurrentWave(int wave) {
        currentWave = wave;
    }

    public void addEnemyKill() {
        enemiesKilled += 1;
    }

    public int getEnemiesKilled() {
        return enemiesKilled;
    }

    public void addUpgrade() {
        upgradesCollected += 1;
    }

    public int getUpgradesCollected() {
        return upgradesCollected;
    }

    public void addTime(float delta) {
        timeSurvived += delta;
    }

    public float getTimeSurvived() {
        return timeSurvived;
    }

    public void setLastDeathCause(String cause) {
        lastDeathCause = cause;
    }

    public String getLastDeathCause() {
        return lastDeathCause;
    }
}
