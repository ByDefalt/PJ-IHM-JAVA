package com.ubo.tp.message.logger;

/**
 * Fabrique de loggers utilitaires.
 * <p>
 * Fournit des méthodes factory pour obtenir rapidement une implémentation de
 * {@link Logger} adaptée (console, fichier, null). Ne garde pas d'état global.
 * </p>
 */
public final class LoggerFactory {

    private LoggerFactory() {}

    /**
     * Retourne un logger console configuré au niveau donné.
     *
     * @param level niveau minimal
     * @return instance de {@link ConsoleLogger}
     */
    public static Logger consoleLogger(LogLevel level) {
        return new ConsoleLogger(level);
    }

    /**
     * Retourne un logger muet (no-op).
     *
     * @return instance de {@link NullLogger}
     */
    public static Logger nullLogger() {
        return new NullLogger();
    }

    /**
     * Retourne un logger fichier configuré au niveau donné.
     *
     * @param level niveau minimal
     * @param filePath chemin du fichier de log
     * @return instance de {@link FileLogger}
     * @throws java.io.IOException si le fichier ne peut pas être ouvert
     */
    public static Logger fileLogger(LogLevel level, String filePath) throws java.io.IOException {
        return new FileLogger(level, filePath);
    }
}
