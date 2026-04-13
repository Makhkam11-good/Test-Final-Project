package com.gladiator.decorator;

/**
 * BasePlayer - базовые характеристики Рыцаря без апгрейдов.
 */
public class BasePlayer implements PlayerStats {

    @Override
    public int getMaxHp() {
        return 100;
    }

    @Override
    public int getDamage() {
        return 10;
    }

    @Override
    public float getSpeed() {
        return 150f;
    }

    @Override
    public float getAttackCooldown() {
        return 1.0f;
    }
}
