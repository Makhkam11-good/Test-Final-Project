package com.gladiator.managers;

import com.gladiator.events.EventBus;
import com.gladiator.events.GameEvent;

/**
 * LevelManager - управляет волнами врагов и отслеживает их уничтожение.
 * Паттерн: Observer (слушает ENEMY_DIED события).
 * PHASE 11: Синхронизация с GameManager для правильного отсчета волн
 */
public class LevelManager {
    private int enemiesAlive;
    
    public LevelManager() {
        this.enemiesAlive = 0;
        
        // Подписываемся на событие смерти врага
        EventBus.getInstance().subscribe(
            GameEvent.Type.ENEMY_DIED,
            event -> onEnemyDied()
        );
    }
    
    /**
     * Начать новую волну с указанным количеством врагов.
     * PHASE 11: Используем GameManager для номера волны, не ведем свой счетчик
     */
    public void startWave(int enemyCount) {
        enemiesAlive = enemyCount;
        int currentWave = GameManager.getInstance().getCurrentWave();
        System.out.println("Wave " + currentWave + " started, enemies: " + enemyCount);
    }
    
    /**
     * Вызывается когда враг убит.
     * Публикует WAVE_CLEARED если враги закончились.
     */
    private void onEnemyDied() {
        enemiesAlive--;
        System.out.println("Enemies left: " + enemiesAlive);
        
        if (enemiesAlive <= 0) {
            System.out.println("WAVE CLEARED!");
            EventBus.getInstance().post(new GameEvent(GameEvent.Type.WAVE_CLEARED));
        }
    }
    
    /**
     * Получить количество живых врагов.
     */
    public int getEnemiesAlive() {
        return enemiesAlive;
    }
}
