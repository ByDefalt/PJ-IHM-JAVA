package com.ubo.tp.message.logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileLogger implements Logger, AutoCloseable {

    private final LogLevel level;
    private final PrintWriter writer;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public FileLogger(LogLevel level, String filePath) throws IOException {
        this.level = level;
        this.writer = new PrintWriter(new FileWriter(filePath, true), true);
    }

    private boolean enabled(LogLevel l) {
        return l.ordinal() >= level.ordinal();
    }

    private String ts() {
        return LocalDateTime.now().format(fmt);
    }

    private void write(LogLevel lvl, String message) {
        if (!enabled(lvl)) return;
        writer.printf("[%s] %s - %s\n", ts(), lvl.name(), message);
    }

    @Override
    public void trace(String message) { write(LogLevel.TRACE, message); }

    @Override
    public void debug(String message) { write(LogLevel.DEBUG, message); }

    @Override
    public void info(String message) { write(LogLevel.INFO, message); }

    @Override
    public void warn(String message) { write(LogLevel.WARN, message); }

    @Override
    public void error(String message) { write(LogLevel.ERROR, message); }

    @Override
    public void error(String message, Throwable t) {
        if (!enabled(LogLevel.ERROR)) return;
        write(LogLevel.ERROR, message);
        t.printStackTrace(writer);
    }

    @Override
    public boolean isLevelEnabled(LogLevel level) { return enabled(level); }

    @Override
    public void close() throws Exception { writer.flush(); writer.close(); }
}

