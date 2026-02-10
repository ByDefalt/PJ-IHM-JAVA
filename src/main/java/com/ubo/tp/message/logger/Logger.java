package com.ubo.tp.message.logger;

public interface Logger {

    void trace(String message);

    void debug(String message);

    void info(String message);

    void warn(String message);

    void error(String message);

    void error(String message, Throwable t);

    boolean isLevelEnabled(com.ubo.tp.message.logger.LogLevel level);
}
