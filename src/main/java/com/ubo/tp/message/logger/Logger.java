package com.ubo.tp.message.logger;

/**
 * Interface de logging utilisée dans l'application.
 * <p>
 * Fournit des méthodes pour journaliser des messages à différents niveaux
 * (TRACE, DEBUG, INFO, WARN, ERROR). Les implémentations peuvent router
 * les messages vers la console, un fichier, ou un sink nul.
 * </p>
 * <p>
 * Contrat : les appels aux méthodes de logging doivent être peu coûteux pour
 * l'appelant (les implémentations peuvent filtrer par niveau avant de formater
 * le message). Les implémentations doivent être sûres en environnement
 * multi-thread si elles sont partagées entre threads.
 * </p>
 */
public interface Logger {

    /**
     * Log a trace level message (finely grained debugging information).
     *
     * @param message text to log
     */
    void trace(String message);

    /**
     * Log a debug level message (useful for debugging during development).
     *
     * @param message text to log
     */
    void debug(String message);

    /**
     * Log an informational message (normal runtime events).
     *
     * @param message text to log
     */
    void info(String message);

    /**
     * Log a warning message (potentially harmful situations).
     *
     * @param message text to log
     */
    void warn(String message);

    /**
     * Log an error message (error events that might still allow the application to continue).
     *
     * @param message text to log
     */
    void error(String message);

    /**
     * Log an error message with an associated Throwable (exception/stacktrace).
     *
     * @param message text to log
     * @param t throwable to log
     */
    void error(String message, Throwable t);

    /**
     * Indique si un niveau de log est activé pour cette instance.
     * Utile pour éviter de construire des messages coûteux quand le niveau est désactivé.
     *
     * @param level niveau à tester
     * @return true si le niveau est activé
     */
    boolean isLevelEnabled(com.ubo.tp.message.logger.LogLevel level);
}
