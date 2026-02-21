package com.ubo.tp.message;

import com.ubo.tp.message.common.Constants;
import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.core.database.Database;
import com.ubo.tp.message.core.database.DbConnector;
import com.ubo.tp.message.core.database.EntityManager;
import com.ubo.tp.message.core.selected.ISelected;
import com.ubo.tp.message.core.selected.Selected;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.core.session.Session;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.view.contexte.ViewContext;
import com.ubo.tp.message.logger.LogLevel;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.logger.LoggerFactory;
import mock.MessageAppMock;

import javax.swing.*;
import java.io.File;
import java.util.UUID;

/**
 * Classe de lancement de l'application.
 */
public class MessageAppLauncher {

    // Peut être surchargé via la propriété système "exchange.dir"
    private static final boolean IS_MOCK_ENABLED = false;
    private static final String EXCHANGE_DIRECTORY_PATH = System.getProperty("exchange.dir", "E:\\ihm");

    public static void main(String[] args) {
        try {
            prepareExchangeDirectory(EXCHANGE_DIRECTORY_PATH);
        } catch (Exception e) {
            System.err.println("Impossible de préparer le répertoire d'échange : " + e.getMessage());
        }

        Logger logger = LoggerFactory.consoleLogger(LogLevel.DEBUG);
        Database database = new Database();
        EntityManager entityManager = new EntityManager(database);
        IDataManager dataManager = new DataManager(database, entityManager, logger);
        dataManager.setExchangeDirectory(EXCHANGE_DIRECTORY_PATH);

        ISession session = new Session();
        createTestData(dataManager, entityManager);

        DbConnector dbConnector = new DbConnector(database);
        MessageAppMock mock = new MessageAppMock(dbConnector, dataManager);
        ISelected selected = new Selected();
        ControllerContext controllerContext = new ControllerContext(logger, dataManager, session, selected);
        ViewContext viewContext = new ViewContext(logger);

        SwingUtilities.invokeLater(() -> {
            MessageApp messageApp = new MessageApp(controllerContext, viewContext);
            messageApp.init();
            messageApp.show();
            if (IS_MOCK_ENABLED) {
                mock.showGUI();
            }
        });
    }

    private static void prepareExchangeDirectory(String path) {
        if (path == null || path.trim().isEmpty()) return;
        File exchangeDir = new File(path);
        if (!exchangeDir.exists()) {
            boolean ok = exchangeDir.mkdirs();
            if (!ok) {
                System.err.println("Impossible de créer le répertoire d'échange : " + path);
            }
        }
        clearExchangeDirectoryFiles(path);
    }

    public static void createTestData(IDataManager dataManager, EntityManager entityManager) {
        User user1 = new User("toto", "toto", "Toto");
        User user2 = new User("alice", "alice", "Alice");
        User user3 = new User("bob", "bob", "Bob");
        User user4 = new User("1", "1", "1");

        dataManager.sendUser(user1);
        dataManager.sendUser(user2);
        dataManager.sendUser(user3);
        dataManager.sendUser(user4);

        Channel channel1 = new Channel(user1, "general");
        Channel channel2 = new Channel(user1, "random");

        dataManager.sendChannel(channel1);
        dataManager.sendChannel(channel2);

        Message m1 = new Message(UUID.randomUUID(), user1, channel1.getUuid(), 0, "Bonjour de " + user1.getName());
        Message m2 = new Message(UUID.randomUUID(), user2, channel1.getUuid(), 124, "Bonjour de " + user2.getName());
        Message m3 = new Message(UUID.randomUUID(), user3, channel1.getUuid(), 1708425600, "Bonjour de " + user3.getName());

        dataManager.sendMessage(m1);
        dataManager.sendMessage(m2);
        dataManager.sendMessage(m3);
    }

    public static void clearExchangeDirectoryFiles(String path) {
        if (path == null) return;
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) return;

        File[] files = dir.listFiles((d, name) ->
                name.endsWith(Constants.USER_FILE_EXTENSION) ||
                        name.endsWith(Constants.MESSAGE_FILE_EXTENSION) ||
                        name.endsWith(Constants.CHANNEL_FILE_EXTENSION)
        );
        if (files == null) return;
        for (File f : files) {
            try {
                if (!f.delete()) {
                    // Ne pas arrêter l'exécution si un fichier n'a pas pu être supprimé
                    System.err.println("Impossible de supprimer le fichier : " + f.getAbsolutePath());
                }
            } catch (Exception ignored) {
            }
        }
    }
}