package com.gladiator.decorator;

public class PoisonDecorator extends PlayerDecorator {

    public PoisonDecorator(PlayerStats wrapped) {
        super(wrapped);
    }

    @Override
    public int getDamage() {
        return wrapped.getDamage() + 10;
    }

    @Override
    public String getLabel() {
        return "Poison (+10 DMG)";
    }
}
