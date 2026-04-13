package com.gladiator.decorator;

/**
 * ShieldDecorator - апгрейд "Щит" (+30 HP).
 */
public class ShieldDecorator extends PlayerDecorator {

    public ShieldDecorator(PlayerStats wrapped) {
        super(wrapped);
    }

    @Override
    public int getMaxHp() {
        return wrapped.getMaxHp() + 30;
    }
}
