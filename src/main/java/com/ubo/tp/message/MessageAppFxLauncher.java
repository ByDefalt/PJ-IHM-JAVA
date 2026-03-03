package com.ubo.tp.message;

import javafx.application.Application;

/**
 * Launcher JavaFX — nécessaire car Application.launch() doit être appelée
 * depuis une classe qui n'étend PAS Application (contrainte modules Java 11+).
 */
public class MessageAppFxLauncher {
    static void main(String[] args) {
        Application.launch(MessageAppFx.class, args);
    }
}

