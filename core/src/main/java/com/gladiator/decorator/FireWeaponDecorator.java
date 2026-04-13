package com.gladiator.decorator;

/**
 * FireWeaponDecorator - апгрейд "Огненный меч" (+15 урона).
 */
public class FireWeaponDecorator extends PlayerDecorator {

    public FireWeaponDecorator(PlayerStats wrapped) {
        super(wrapped);
    }

    @Override
    public int getDamage() {
        return wrapped.getDamage() + 15;
    }
}
