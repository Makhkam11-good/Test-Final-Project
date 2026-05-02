package com.gladiator.entities.states;

import com.gladiator.entities.Player;

public class IdleState implements PlayerState {
    private Player player;

    public IdleState(Player player) {
        this.player = player;
    }

    @Override
    public void enter() {
    }

    @Override
    public void update(float delta) {
        if (player.isMoving()) {
            player.changeState(player.getRunState());
        }
    }

    @Override
    public void exit() {
    }
}
