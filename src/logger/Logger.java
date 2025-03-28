package logger;

public interface Logger {
    void log(Severity severity, String message);
    void log(Severity severity, String message, int line, String file);
    RuntimeException panic(String message, int line, String file);
    RuntimeException panic(String message);

    void dump();
    boolean hasErrors();

    enum Severity {
        INFO,
        DEBUG,
        WARNING,
        ERROR
    }
}
