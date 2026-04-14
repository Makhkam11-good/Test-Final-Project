package com.gladiator.decorator;

/**
 * ShieldDecorator - апгрейд "Щит" (+30 HP, -5% скорость).
 */
public class ShieldDecorator extends PlayerDecorator {

    public ShieldDecorator(PlayerStats wrapped) {
        super(wrapped);
    }

    @Override
    public int getMaxHp() {
        return wrapped.getMaxHp() + 30;
    }

    @Override
    public float getSpeed() {
        return wrapped.getSpeed() * 0.95f;  // -5% штраф к скорости
    }

    @Override
    public String getDescription() {
        return "Shield: +30 HP";
    }
}
