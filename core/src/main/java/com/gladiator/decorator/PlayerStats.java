package com.gladiator.decorator;

/**
 * PlayerStats defines base stats for the Decorator chain.
 */
public interface PlayerStats {
    int getMaxHp();
    int getDamage();
    float getSpeed();
    float getAttackCooldown();
}
