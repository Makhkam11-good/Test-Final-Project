package com.gladiator.decorator;

/**
 * PlayerStats (Decorator паттерн) - интерфейс для получения характеристик Рыцаря.
 */
public interface PlayerStats {
    int getMaxHp();
    int getDamage();
    float getSpeed();
    float getAttackCooldown();
    float getDamageReduction();  // множитель снижения урона (0.0 = нет, 0.2 = -20%)
    String getDescription();      // описание для карточки апгрейда
}
