package com.gladiator.factories;

import com.badlogic.gdx.math.Rectangle;
import com.gladiator.entities.Enemy;
import com.gladiator.managers.GameManager;

/**
 * GoblinFactory - фабрика для создания Гоблинов.
 * Гоблин: HP=40, damage=12, speed=100, reward=25.
 * Фаза 6: применяет множители сложности из GameManager.
 * Фаза 9: устанавливает animKey для спрайта
 */
public class GoblinFactory extends EnemyFactory {

    @Override
    public Enemy create(float x, float y) {
        Enemy enemy = new Enemy();
        enemy.x = x;
        enemy.y = y;
        enemy.maxHp = 40f;
        enemy.hp = 40f;
        enemy.damage = 12f;
        enemy.speed = 100f;
        enemy.scoreReward = 25;
        enemy.alive = true;
        enemy.bounds = new Rectangle(x, y, Enemy.WIDTH, Enemy.HEIGHT);
        
        // Установим ключ анимации (Фаза 9)
        enemy.animKey = "goblin_walk";
        
        // Применяем множители сложности (Фаза 6)
        float speedMult = GameManager.getInstance().getDifficulty().getEnemySpeedMult();
        float damageMult = GameManager.getInstance().getDifficulty().getEnemyDamageMult();
        enemy.speed *= speedMult;
        enemy.damage *= damageMult;
        
        System.out.println("Created Goblin: speed=" + enemy.speed + " damage=" + enemy.damage);
        
        return enemy;
    }
}
