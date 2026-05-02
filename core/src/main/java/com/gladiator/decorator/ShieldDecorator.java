package com.gladiator.decorator;

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
        return wrapped.getSpeed() * 0.95f;
    }

    @Override
    public String getLabel() {
        return "Shield (+30 HP)";
    }
}
