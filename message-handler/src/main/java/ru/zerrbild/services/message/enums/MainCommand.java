package ru.zerrbild.services.message.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MainCommand {
    START("/start"),
    HELP("/help"),
    REGISTER("/register"),
    CANCEL("/cancel"),
    RESET("/reset"),
    UNDEFINED("");
    
    private final String tgCommand;

    @Override
    public String toString() {
        return tgCommand;
    }
    
    public static MainCommand findCommand(String textFromUser) {
        for (MainCommand command : MainCommand.values()) {
            if (command.tgCommand.equals(textFromUser)) return command;
        }
        return UNDEFINED;
    }
}
