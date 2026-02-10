package com.ubo.tp.message.logger;

/**
 * Niveaux de journalisation disponibles. L'ordre définit la sévérité croissante
 * (TRACE le plus verbeux, ERROR le plus sévère). Les comparaisons d'ordinal
 * peuvent être utilisées pour filtrer les messages.
 */
public enum LogLevel {
    TRACE, DEBUG, INFO, WARN, ERROR
}
