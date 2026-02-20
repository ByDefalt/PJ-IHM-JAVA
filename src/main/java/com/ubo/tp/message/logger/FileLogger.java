package com.ubo.tp.message.logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Logger qui écrit les messages dans un fichier de manière asynchrone et
 * thread-safe en utilisant une file et un thread dédié d'écriture.
 * <p>
 * Les appels aux méthodes de logging sont non bloquants (ils enfilent les
 * messages) ; un worker consomme la file et écrit séquentiellement sur le
 * fichier. À la fermeture, la file est drainée avant la fermeture du writer.
 * </p>
 */
public class FileLogger implements Logger, AutoCloseable {

    private final LogLevel level;
    private final PrintWriter writer;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Async queue + worker
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final Thread worker;
    private final AtomicBoolean running = new AtomicBoolean(true);

    /**
     * Crée un FileLogger pointant vers le fichier donné.
     *
     * @param level    niveau minimal de log
     * @param filePath chemin du fichier de log (créé si nécessaire)
     * @throws IOException si le fichier ne peut pas être ouvert
     */
    public FileLogger(LogLevel level, String filePath) throws IOException {
        this.level = level;
        this.writer = new PrintWriter(new FileWriter(filePath, true), true);

        // worker thread writes queued messages to file
        this.worker = new Thread(() -> {
            try {
                // loop until stopped and queue drained
                while (running.get() || !queue.isEmpty()) {
                    try {
                        String line = queue.poll(500, TimeUnit.MILLISECONDS);
                        if (line != null) {
                            writer.println(line);
                        }
                    } catch (InterruptedException e) {
                        // re-check running flag
                    }
                }
            } finally {
                // Ensure writer is flushed even if worker exits unexpectedly
                writer.flush();
            }
        }, "FileLogger-Writer");
        this.worker.setDaemon(true);
        this.worker.start();
    }

    private boolean enabled(LogLevel l) {
        return l.ordinal() >= level.ordinal();
    }

    private String ts() {
        return LocalDateTime.now().format(fmt);
    }

    private String format(LogLevel lvl, String msg) {
        return String.format("[%s] %s - %s", ts(), lvl.name(), msg);
    }

    private void enqueue(String s) {
        if (!running.get()) return; // ignore after close initiated
        // best-effort: try to offer without blocking indefinitely
        queue.offer(s);
    }

    private void write(LogLevel lvl, String message) {
        if (!enabled(lvl)) return;
        enqueue(format(lvl, message));
    }

    @Override
    public void trace(String message) {
        write(LogLevel.TRACE, message);
    }

    @Override
    public void debug(String message) {
        write(LogLevel.DEBUG, message);
    }

    @Override
    public void info(String message) {
        write(LogLevel.INFO, message);
    }

    @Override
    public void warn(String message) {
        write(LogLevel.WARN, message);
    }

    @Override
    public void error(String message) {
        write(LogLevel.ERROR, message);
    }

    @Override
    public void error(String message, Throwable t) {
        if (!enabled(LogLevel.ERROR)) return;
        // capture stack trace into string and enqueue as a single message
        StringWriter sw = new StringWriter();
        t.printStackTrace(new java.io.PrintWriter(sw));
        String combined = format(LogLevel.ERROR, message + "\n" + sw);
        enqueue(combined);
    }

    @Override
    public boolean isLevelEnabled(LogLevel level) {
        return enabled(level);
    }

    /**
     * Arrête le worker, attend la vidange de la file et ferme le writer.
     */
    @Override
    public void close() throws Exception {
        // signal stop
        running.set(false);
        // interrupt worker in case it's waiting
        worker.interrupt();
        try {
            // wait up to 2s for worker to finish
            worker.join(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // drain any remaining messages synchronously to ensure persistence
        String line;
        while ((line = queue.poll()) != null) {
            writer.println(line);
        }
        writer.flush();
        writer.close();
    }
}
