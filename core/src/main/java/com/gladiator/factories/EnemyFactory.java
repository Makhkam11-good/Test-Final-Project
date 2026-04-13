package com.gladiator.factories;

import com.gladiator.entities.Enemy;

/**
 * EnemyFactory (Factory Method паттерн) - абстрактный класс для создания врагов.
 */
public abstract class EnemyFactory {
    public abstract Enemy create(float x, float y);
}
