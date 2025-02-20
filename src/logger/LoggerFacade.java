package logger;

import parser.nodes.ASTMetaDataStore;
import parser.nodes.ASTNode;

public final class LoggerFacade {
    private static Logger logger = null;

    private LoggerFacade() {}

    public static void error(String message, ASTNode node) {
        LoggerFacade.getLogger().log(
            Logger.Severity.ERROR,
            message,
            ASTMetaDataStore.getInstance().getLine(node),
            ASTMetaDataStore.getInstance().getFile(node)
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
