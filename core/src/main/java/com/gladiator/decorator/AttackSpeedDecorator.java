package com.gladiator.decorator;

public class AttackSpeedDecorator extends PlayerDecorator {

    public AttackSpeedDecorator(PlayerStats wrapped) {
        super(wrapped);
    }

    @Override
    public float getAttackCooldown() {
        return wrapped.getAttackCooldown() * 0.8f;
    }

    @Override
    public String getLabel() {
        return "Attack Speed (-20% CD)";
    }
}
