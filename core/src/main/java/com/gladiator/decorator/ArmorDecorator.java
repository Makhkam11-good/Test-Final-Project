package com.gladiator.decorator;

/**
 * ArmorDecorator - апгрейд "Броня" (-20% входящий урон, мультипликативное стакание).
 */
public class ArmorDecorator extends PlayerDecorator {

    public ArmorDecorator(PlayerStats wrapped) {
        super(wrapped);
    }

    @Override
    public float getDamageReduction() {
        // Мультипликативное стакание:
        // first: 1 - (1 - 0) * 0.8 = 0.2 (-20%)
        // second: 1 - (1 - 0.2) * 0.8 = 1 - 0.64 = 0.36 (-36%)
        return 1f - (1f - wrapped.getDamageReduction()) * 0.8f;
    }

    @Override
    public String getDescription() {
        return "Armor: -20% damage taken";
    }
}
