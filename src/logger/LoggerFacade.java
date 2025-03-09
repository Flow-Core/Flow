package logger;

import parser.nodes.ASTMetaDataStore;
import parser.nodes.ASTNode;

public final class LoggerFacade {
    private static Logger logger = null;

    private LoggerFacade() {}

    public static void error(String message, ASTNode node) {
        log(Logger.Severity.ERROR, message, node);
    }

    public static void error(String message) {
        log(Logger.Severity.ERROR, message);
    }

    public static void warning(String message, ASTNode node) {
        log(Logger.Severity.WARNING, message, node);
    }

    public static void warning(String message) {
        log(Logger.Severity.WARNING, message);
    }

    public static void log(Logger.Severity severity, String message, ASTNode node) {
        LoggerFacade.getLogger().log(
            severity,
            message,
            ASTMetaDataStore.getInstance().getLine(node),
            ASTMetaDataStore.getInstance().getFile(node)
        );
    }

    public static void log(Logger.Severity severity, String message) {
        LoggerFacade.getLogger().log(
            severity,
            message
        );
    }

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
