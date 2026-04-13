package com.gladiator.decorator;

/**
 * ArmorDecorator - апгрейд "Броня" (-20% входящий урон).
 */
public class ArmorDecorator extends PlayerDecorator {

    public ArmorDecorator(PlayerStats wrapped) {
        super(wrapped);
    }

    // TODO: в Фазе 7 реализовать множитель для входящего урона (-20%)
}
