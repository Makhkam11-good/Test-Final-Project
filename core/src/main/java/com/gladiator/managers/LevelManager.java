package com.gladiator.managers;

import com.badlogic.gdx.Gdx;
import com.gladiator.events.EventBus;
import com.gladiator.events.EventListener;
import com.gladiator.events.GameEvent;

/**
 * LevelManager tracks remaining enemies and posts WAVE_CLEARED.
 */
public class LevelManager {
    private int enemiesAlive;
    private final EventListener enemyDiedListener;

    public LevelManager() {
        enemiesAlive = 0;
        enemyDiedListener = event -> onEnemyDied();
        EventBus.getInstance().subscribe(GameEvent.Type.ENEMY_DIED, enemyDiedListener);
    }

    public void startWave(int enemyCount) {
        enemiesAlive = enemyCount;
        Gdx.app.log("LevelManager", "Wave start: " + GameManager.getInstance().getCurrentWave());
    }

    private void onEnemyDied() {
        enemiesAlive -= 1;
        if (enemiesAlive <= 0) {
            EventBus.getInstance().post(new GameEvent(GameEvent.Type.WAVE_CLEARED));
        }
    }

    public int getEnemiesAlive() {
        return enemiesAlive;
    }

    public void dispose() {
        EventBus.getInstance().unsubscribe(GameEvent.Type.ENEMY_DIED, enemyDiedListener);
    }
}
