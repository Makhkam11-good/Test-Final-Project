package com.gladiator.decorator;

/**
 * PlayerDecorator is the base for all upgrade decorators.
 */
public abstract class PlayerDecorator implements PlayerStats {
    protected PlayerStats wrapped;

    public PlayerDecorator(PlayerStats wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public int getMaxHp() {
        return wrapped.getMaxHp();
    }

    @Override
    public int getDamage() {
        return wrapped.getDamage();
    }

    @Override
    public float getSpeed() {
        return wrapped.getSpeed();
    }

    @Override
    public float getAttackCooldown() {
        return wrapped.getAttackCooldown();
    }

    public abstract String getLabel();
}
