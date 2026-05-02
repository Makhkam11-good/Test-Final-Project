package com.gladiator.entities.states;

import com.gladiator.entities.Player;

public class AttackState implements PlayerState {
    private Player player;
    private float timer;

    public AttackState(Player player) {
        this.player = player;
        this.timer = 0f;
    }

    @Override
    public void enter() {
        timer = player.getAttackDuration();
        player.setAttacking(true);
    }

    @Override
    public void update(float delta) {
        timer -= delta;
        if (timer <= 0) {
            player.setAttacking(false);
            if (player.isMoving()) {
                player.changeState(player.getRunState());
            } else {
                player.changeState(player.getIdleState());
            }
        }
    }

    @Override
    public void exit() {
    }
}
