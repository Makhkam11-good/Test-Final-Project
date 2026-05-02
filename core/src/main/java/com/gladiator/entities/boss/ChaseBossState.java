package com.gladiator.entities.boss;

import com.gladiator.entities.Boss;

public class ChaseBossState implements BossState {

    private float timer = 0f;
    private static final float DURATION = 3.0f;
    private static final float SPEED = 80f;

    @Override
    public void enter(Boss boss) {
        timer = 0f;
    }

    @Override
    public void update(Boss boss, float delta, float playerX, float playerY) {
        timer += delta;
        
        // Движение к игроку
        float dx = playerX - boss.getX();
        float dy = playerY - boss.getY();
        float len = (float) Math.sqrt(dx*dx + dy*dy);
        if (len > 0) {
            boss.lastDirX = dx/len;
            boss.lastDirY = dy/len;
            boss.velocityX = boss.lastDirX * SPEED;
            boss.velocityY = boss.lastDirY * SPEED;
        }
        
        if (timer >= DURATION) {
            boss.changeState(boss.getDashState());
        }
    }

    @Override
    public void exit(Boss boss) {
        boss.velocityX = 0;
        boss.velocityY = 0;
    }
}
