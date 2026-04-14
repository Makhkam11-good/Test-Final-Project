package com.gladiator.factories;

import com.badlogic.gdx.math.Rectangle;
import com.gladiator.entities.Enemy;
import com.gladiator.managers.GameManager;

/**
 * SlimeFactory - фабрика для создания Слизи.
 * Слизь: HP=20, damage=5, speed=60, reward=10.
 * Фаза 6: применяет множители сложности из GameManager.
 */
public class SlimeFactory extends EnemyFactory {

    @Override
    public Enemy create(float x, float y) {
        Enemy enemy = new Enemy();
        enemy.x = x;
        enemy.y = y;
        enemy.maxHp = 20f;
        enemy.hp = 20f;
        enemy.damage = 5f;
        enemy.speed = 60f;
        enemy.scoreReward = 10;
        enemy.alive = true;
        enemy.bounds = new Rectangle(x, y, Enemy.WIDTH, Enemy.HEIGHT);
        
        // Применяем множители сложности (Фаза 6)
        float speedMult = GameManager.getInstance().getDifficulty().getEnemySpeedMult();
        float damageMult = GameManager.getInstance().getDifficulty().getEnemyDamageMult();
        enemy.speed *= speedMult;
        enemy.damage *= damageMult;
        
        System.out.println("Created Slime: speed=" + enemy.speed + " damage=" + enemy.damage);
        
        return enemy;
    }
}
