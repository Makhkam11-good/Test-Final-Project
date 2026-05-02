package com.gladiator.commands;

import com.gladiator.entities.Player;

public class MoveCommand implements Command {
    private final Player player;
    private float dirX;
    private float dirY;
    private float prevDirX;
    private float prevDirY;

    public MoveCommand(Player player) {
        this.player = player;
    }

    public void setDirection(float x, float y) {
        dirX = x;
        dirY = y;
    }

    @Override
    public void execute() {
        prevDirX = player.getMoveDirX();
        prevDirY = player.getMoveDirY();
        player.setMoveDirection(dirX, dirY);
    }

    @Override
    public void undo() {
        player.setMoveDirection(prevDirX, prevDirY);
    }
}
