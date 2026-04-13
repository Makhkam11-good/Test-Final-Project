package com.gladiator.entities.states;

import com.gladiator.entities.Player;

/**
 * RunState - Рыцарь движется по экрану (нажаты WASD).
 */
public class RunState implements PlayerState {
    private Player player;

    public RunState(Player player) {
        this.player = player;
    }

    @Override
    public void enter() {
        System.out.println("Player: Running");
    }

    @Override
    public void update(float delta) {
        // Если игрок перестал двигаться, переходим в IdleState
        if (Math.abs(player.velocityX) <= 0 && Math.abs(player.velocityY) <= 0) {
            player.changeState(new IdleState(player));
        }
    }

    @Override
    public void exit() {
    }
}
