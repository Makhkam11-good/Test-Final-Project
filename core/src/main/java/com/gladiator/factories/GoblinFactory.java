package com.gladiator.factories;

import com.gladiator.ai.AggressiveAI;
import com.gladiator.entities.Enemy;
import com.gladiator.managers.AssetManager;
import com.gladiator.managers.GameManager;

public class GoblinFactory extends EnemyFactory {

    @Override
    public Enemy create(float x, float y) {
        float speedMult = GameManager.getInstance().getDifficulty().getEnemySpeedMult();
        float damageMult = GameManager.getInstance().getDifficulty().getEnemyDamageMult();

        Enemy enemy = new Enemy(x, y, 32f, 48f);
        enemy.setStats(40f, 12f * damageMult, 100f * speedMult);
        enemy.setScoreReward(25);
        enemy.setTypeName("Goblin");
        enemy.setTexture(AssetManager.getInstance().getTexture(AssetManager.TEX_GOBLIN));
        enemy.setAnimationKey(AssetManager.ANIM_GOBLIN);
        enemy.setRenderColor(1f, 1f, 1f);
        enemy.setAi(new AggressiveAI());
        return enemy;
    }
}
