package com.gladiator.decorator;

public class SpeedBootsDecorator extends PlayerDecorator {

    public SpeedBootsDecorator(PlayerStats wrapped) {
        super(wrapped);
    }

    @Override
    public float getSpeed() {
        return wrapped.getSpeed() * 1.25f;
    }

    @Override
    public String getLabel() {
        return "Speed Boots (+25% SPD)";
    }
}
