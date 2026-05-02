package com.gladiator.decorator;

public class FireWeaponDecorator extends PlayerDecorator {

    public FireWeaponDecorator(PlayerStats wrapped) {
        super(wrapped);
    }

    @Override
    public int getDamage() {
        return wrapped.getDamage() + 15;
    }

    @Override
    public String getLabel() {
        return "Fire Weapon (+15 DMG)";
    }
}
