package logger;

public interface Logger {
    void log(Severity severity, String message, int line, String file);
    RuntimeException panic(String message, int line, String file);

    void dump();
    boolean hasErrors();

    enum Severity {
        DEBUG,
        WARNING,
        ERROR
    }
}
