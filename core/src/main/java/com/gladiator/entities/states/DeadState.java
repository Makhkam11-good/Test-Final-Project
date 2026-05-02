package com.gladiator.entities.states;

import com.gladiator.entities.Player;

public class DeadState implements PlayerState {
    private Player player;

    public DeadState(Player player) {
        this.player = player;
    }

    @Override
    public void enter() {
    }

    @Override
    public void update(float delta) {
        // Ничего не делаем - финальное состояние
    }

    @Override
    public void exit() {
    }
}
