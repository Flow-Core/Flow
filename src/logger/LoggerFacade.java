package logger;

public final class LoggerFacade {
    private static Logger logger = null;

    private LoggerFacade() {}

    public static Logger getLogger() {
        if (logger == null) {
            throw new LoggerException("Logger was not initialized");
        }

        return logger;
    }

    public static void initLogger(Logger logger) {
        if (LoggerFacade.logger == null) {
            LoggerFacade.logger = logger;
        } else {
            throw new LoggerException("Logger was already initialized");
        }
    }

    public static void clearLogger() {
        LoggerFacade.logger = null;
    }
}
