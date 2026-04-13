package com.gladiator.entities.states;

import com.gladiator.entities.Player;

/**
 * DeadState - Рыцарь мертв (HP <= 0).
 */
public class DeadState implements PlayerState {
    private Player player;

    public DeadState(Player player) {
        this.player = player;
    }

    @Override
    public void enter() {
        System.out.println("Player: Dead");
    }

    @Override
    public void update(float delta) {
        // Ничего не делаем - финальное состояние
    }

    @Override
    public void exit() {
    }
}
