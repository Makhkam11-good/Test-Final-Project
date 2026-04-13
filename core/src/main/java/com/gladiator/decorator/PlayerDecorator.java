package com.gladiator.decorator;

/**
 * PlayerDecorator (Decorator паттерн) - абстрактный класс для апгрейдов Рыцаря.
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
}
