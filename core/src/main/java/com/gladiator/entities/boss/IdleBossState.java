package com.gladiator.entities.boss;

import com.gladiator.entities.Boss;

/**
 * IdleBossState - Босс стоит на месте и вращается к игроку (1.5 сек), потом переходит в Chase.
 */
public class IdleBossState implements BossState {
    
    private float timer = 0f;
    private static final float DURATION = 1.5f;

    @Override
    public void enter(Boss boss) {
        boss.velocityX = 0;
        boss.velocityY = 0;
        timer = 0f;
        System.out.println("Boss: IDLE");
    }

    @Override
    public void update(Boss boss, float delta, float playerX, float playerY) {
        timer += delta;
        // Босс стоит на месте, только вращается к игроку (визуально)
        if (timer >= DURATION) {
            boss.changeState(new ChaseBossState());
        }
    }

    @Override
    public void exit(Boss boss) {
        // пусто
    }
}
