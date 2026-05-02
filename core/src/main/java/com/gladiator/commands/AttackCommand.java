package com.gladiator.commands;

import com.gladiator.entities.Player;

public class AttackCommand implements Command {
    private final Player player;

    public AttackCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute() {
        player.performAttack();
    }

    @Override
    public void undo() {
        // No-op: attacks are not reversible in this game.
    }
}
