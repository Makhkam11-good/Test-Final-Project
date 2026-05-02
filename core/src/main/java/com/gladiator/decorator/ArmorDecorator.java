package com.gladiator.decorator;

public class ArmorDecorator extends PlayerDecorator implements DamageModifier {

    public ArmorDecorator(PlayerStats wrapped) {
        super(wrapped);
    }

    @Override
    public float getDamageMultiplier() {
        return 0.8f;
    }

    @Override
    public String getLabel() {
        return "Armor (-20% DMG)";
    }
}
