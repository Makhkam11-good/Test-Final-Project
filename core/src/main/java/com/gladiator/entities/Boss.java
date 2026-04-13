package com.gladiator.entities;

/**
 * Boss (Финальный Босс) - враг на волне 10, использует State паттерн для смены поведения.
 */
public class Boss extends Enemy {
    public Object currentState;

    public Boss() {
        super();
    }

    public void changeState(Object newState) {
        // TODO: Реализовать в Фазе 8
    }

    @Override
    public void update(float delta) {
        // TODO: Реализовать в Фазе 8
    }
}
