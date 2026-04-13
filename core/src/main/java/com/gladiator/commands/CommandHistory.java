package com.gladiator.commands;

import java.util.Stack;

/**
 * CommandHistory - хранит историю команд для undo операций.
 */
public class CommandHistory {
    private Stack<Command> history = new Stack<>();

    public void execute(Command command) {
        command.execute();
        history.push(command);
    }

    public void undo() {
        if (!history.isEmpty()) {
            Command command = history.pop();
            command.undo();
        }
    }
}
