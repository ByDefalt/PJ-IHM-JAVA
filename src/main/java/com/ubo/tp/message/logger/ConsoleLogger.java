package com.ubo.tp.message.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConsoleLogger implements Logger {

    private final LogLevel level;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ConsoleLogger(LogLevel level) {
        this.level = level;
    }

    private boolean enabled(LogLevel l) {
        return l.ordinal() >= level.ordinal();
    }

    private String ts() {
        return LocalDateTime.now().format(fmt);
    }

    @Override
    public void trace(String message) {
        if (enabled(LogLevel.TRACE)) System.out.println(format(LogLevel.TRACE, message));
    }

    @Override
    public void debug(String message) {
        if (enabled(LogLevel.DEBUG)) System.out.println(format(LogLevel.DEBUG, message));
    }

    @Override
    public void info(String message) {
        if (enabled(LogLevel.INFO)) System.out.println(format(LogLevel.INFO, message));
    }

    @Override
    public void warn(String message) {
        if (enabled(LogLevel.WARN)) System.err.println(format(LogLevel.WARN, message));
    }

    @Override
    public void error(String message) {
        if (enabled(LogLevel.ERROR)) System.err.println(format(LogLevel.ERROR, message));
    }

    @Override
    public void error(String message, Throwable t) {
        if (enabled(LogLevel.ERROR)) {
            System.err.println(format(LogLevel.ERROR, message));
            t.printStackTrace(System.err);
        }
    }

    private String format(LogLevel lvl, String msg) {
        return String.format("[%s] %s - %s", ts(), lvl.name(), msg);
    }

    @Override
    public boolean isLevelEnabled(LogLevel level) {
        return enabled(level);
    }
}

