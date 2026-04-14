package com.gladiator.decorator;

/**
 * SpeedBootsDecorator - апгрейд "Сапоги скорости" (+25% скорость).
 */
public class SpeedBootsDecorator extends PlayerDecorator {

    public SpeedBootsDecorator(PlayerStats wrapped) {
        super(wrapped);
    }

    @Override
    public float getSpeed() {
        return wrapped.getSpeed() * 1.25f;
    }

    @Override
    public String getDescription() {
        return "Speed Boots: +25% speed";
    }
}
