package com.gladiator.entities.states;

import com.gladiator.entities.Player;

public class RunState implements PlayerState {
    private Player player;

    public RunState(Player player) {
        this.player = player;
    }

    @Override
    public void enter() {
    }

    @Override
    public void update(float delta) {
        if (!player.isMoving()) {
            player.changeState(player.getIdleState());
        }
    }

    @Override
    public void exit() {
    }
}
