package com.gladiator.decorator;

/**
 * PoisonDecorator - апгрейд "Ядовитый удар" (+10 урона).
 */
public class PoisonDecorator extends PlayerDecorator {

    public PoisonDecorator(PlayerStats wrapped) {
        super(wrapped);
    }

    @Override
    public int getDamage() {
        return wrapped.getDamage() + 10;
    }
}
