package com.gladiator.factories;

import com.gladiator.ai.AggressiveAI;
import com.gladiator.ai.PatrolAI;
import com.gladiator.entities.Enemy;
import com.gladiator.managers.AssetManager;
import com.gladiator.managers.GameManager;

public class SlimeFactory extends EnemyFactory {

    @Override
    public Enemy create(float x, float y) {
        float speedMult = GameManager.getInstance().getDifficulty().getEnemySpeedMult();
        float damageMult = GameManager.getInstance().getDifficulty().getEnemyDamageMult();

        Enemy enemy = new Enemy(x, y, 32f, 32f);
        enemy.setStats(20f, 5f * damageMult, 60f * speedMult);
        enemy.setScoreReward(10);
        enemy.setTypeName("Slime");
        enemy.setTexture(AssetManager.getInstance().getTexture(AssetManager.TEX_SLIME));
        enemy.setAnimationKey(AssetManager.ANIM_SLIME);
        enemy.setRenderColor(1f, 1f, 1f);
        enemy.setPatrolAggroAi(new PatrolAI(), new AggressiveAI(), 150f);
        return enemy;
    }
}
