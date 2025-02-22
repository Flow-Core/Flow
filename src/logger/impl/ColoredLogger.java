package logger.impl;

import logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class ColoredLogger implements Logger {
    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String BOLD = "\u001B[1m";

    private final List<String> errorLog = new ArrayList<>();
    private boolean hasErrors = false;

    @Override
    public void log(Severity severity, String message) {
        String color = switch (severity) {
            case INFO -> "";
            case DEBUG -> BLUE;
            case WARNING -> YELLOW;
            case ERROR -> RED;
        };

        String logMessage = String.format(color + BOLD + message);
        System.out.println(logMessage);
    }

    @Override
    public void log(Severity severity, String message, int line, String file) {
        String color = switch (severity) {
            case INFO -> "";
            case DEBUG -> BLUE;
            case WARNING -> YELLOW;
            case ERROR -> RED;
        };

        String logMessage = String.format(color + BOLD + "%s: %s at line: %d", file, message, line);
        System.out.println(logMessage);

        errorLog.add(logMessage);

        if (severity == Severity.ERROR) {
            hasErrors = true;
        }
    }

    @Override
    public RuntimeException panic(String message, int line, String file) {
        String log = String.format(RED + BOLD + "%s: %s at line: %d", file, message, line);
        System.out.println(log);
        return new RuntimeException(log);
    }

    @Override
    public RuntimeException panic(String message) {
        String log = RED + BOLD + message;
        System.out.println(log);
        return new RuntimeException(log);
    }

    @Override
    public void dump() {
        if (!errorLog.isEmpty()) {
            System.out.println(RED + BOLD + "=== Error Summary ===" + RESET);
            errorLog.forEach(System.out::println);
        } else {
            System.out.println(CYAN + "No errors logged." + RESET);
        }
    }

    @Override
    public boolean hasErrors() {
        return hasErrors;
    }
}
