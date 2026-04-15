package com.gladiator.entities.boss;

import com.gladiator.entities.Boss;

/**
 * ChaseBossState - Босс медленно преследует игрока (80 px/sec, 3 сек), потом переходит в Dash.
 */
public class ChaseBossState implements BossState {
    
    private float timer = 0f;
    private static final float DURATION = 3.0f;
    private static final float SPEED = 80f;

    @Override
    public void enter(Boss boss) {
        timer = 0f;
        System.out.println("Boss: CHASE");
    }

    @Override
    public void update(Boss boss, float delta, float playerX, float playerY) {
        timer += delta;
        
        // Движение к игроку
        float dx = playerX - boss.x;
        float dy = playerY - boss.y;
        float len = (float) Math.sqrt(dx*dx + dy*dy);
        if (len > 0) {
            boss.lastDirX = dx/len;
            boss.lastDirY = dy/len;
            boss.velocityX = boss.lastDirX * SPEED;
            boss.velocityY = boss.lastDirY * SPEED;
        }
        
        if (timer >= DURATION) {
            boss.changeState(new DashBossState());
        }
    }

    @Override
    public void exit(Boss boss) {
        boss.velocityX = 0;
        boss.velocityY = 0;
    }
}
