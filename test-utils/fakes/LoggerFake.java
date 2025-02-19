package fakes;

import logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class LoggerFake implements Logger {
    private final List<String> logs = new ArrayList<>();

    @Override
    public void log(Severity severity, String message, int line, String file) {
        logs.add(String.format("%s: %s at line: %d", file, message, line));
    }

    @Override
    public RuntimeException panic(String message, int line, String file) {
        return new RuntimeException(String.format("%s: %s at line: %d", file, message, line));
    }

    @Override
    public void dump() {
        for (String log : logs) {
            System.out.println(log);
        }
    }

    @Override
    public boolean hasErrors() {
        return !logs.isEmpty();
    }
}
