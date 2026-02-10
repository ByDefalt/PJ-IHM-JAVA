package com.ubo.tp.message.logger;

public final class LoggerFactory {

    private LoggerFactory() {}

    public static Logger consoleLogger(LogLevel level) {
        return new ConsoleLogger(level);
    }

    public static Logger nullLogger() {
        return new NullLogger();
    }

    public static Logger fileLogger(LogLevel level, String filePath) throws java.io.IOException {
        return new FileLogger(level, filePath);
    }
}

