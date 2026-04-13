package com.gladiator.entities.states;

import com.gladiator.entities.Player;

/**
 * IdleState - Рыцарь стоит на месте (не движется).
 */
public class IdleState implements PlayerState {
    private Player player;

    public IdleState(Player player) {
        this.player = player;
    }

    @Override
    public void enter() {
        System.out.println("Player: Idle");
    }

    @Override
    public void update(float delta) {
        // Если игрок начал двигаться, переходим в RunState
        if (Math.abs(player.velocityX) > 0 || Math.abs(player.velocityY) > 0) {
            player.changeState(new RunState(player));
        }
    }

    @Override
    public void exit() {
    }
}
