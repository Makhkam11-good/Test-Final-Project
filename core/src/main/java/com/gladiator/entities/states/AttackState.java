package com.gladiator.entities.states;

import com.gladiator.entities.Player;

/**
 * AttackState - Рыцарь атакует врагов.
 */
public class AttackState implements PlayerState {
    private Player player;
    private float timer = 0.3f;
    private static final float ATTACK_DURATION = 0.3f;

    public AttackState(Player player) {
        this.player = player;
        this.timer = ATTACK_DURATION;
    }

    @Override
    public void enter() {
        System.out.println("Player: Attacking");
        timer = ATTACK_DURATION;
    }

    @Override
    public void update(float delta) {
        timer -= delta;
        if (timer <= 0) {
            // Возвращаемся в IdleState после атаки
            if (Math.abs(player.velocityX) > 0 || Math.abs(player.velocityY) > 0) {
                player.changeState(new RunState(player));
            } else {
                player.changeState(new IdleState(player));
            }
        }
    }

    @Override
    public void exit() {
    }
}
