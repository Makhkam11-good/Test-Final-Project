package com.gladiator.commands;

/**
 * Command (Command паттерн) - интерфейс для представления действия как объекта.
 */
public interface Command {
    void execute();
    void undo();
}
