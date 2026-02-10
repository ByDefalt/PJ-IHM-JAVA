package com.ubo.tp.message.logger;

public class NullLogger implements Logger {

    public NullLogger() {
    }

    @Override
    public void trace(String message) { }

    @Override
    public void debug(String message) { }

    @Override
    public void info(String message) { }

    @Override
    public void warn(String message) { }

    @Override
    public void error(String message) { }

    @Override
    public void error(String message, Throwable t) { }

    @Override
    public boolean isLevelEnabled(LogLevel level) { return false; }
}

