package com.gladiator.commands;

import java.util.ArrayDeque;
import java.util.Deque;

public class CommandHistory {
    private static final int MAX_HISTORY = 64;
    private final Deque<Command> history = new ArrayDeque<>(MAX_HISTORY);

    public void execute(Command command) {
        command.execute();
        if (history.size() >= MAX_HISTORY) {
            history.removeFirst();
        }
        history.addLast(command);
    }

    public void undo() {
        Command command = history.pollLast();
        if (command != null) {
            command.undo();
        }
    }

    public void clear() {
        history.clear();
    }
}
