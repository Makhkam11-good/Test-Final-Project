package com.gladiator.decorator;

/**
 * PlayerStats (Decorator паттерн) - интерфейс для получения характеристик Рыцаря.
 */
public interface PlayerStats {
    int getMaxHp();
    int getDamage();
    float getSpeed();
    float getAttackCooldown();
}
