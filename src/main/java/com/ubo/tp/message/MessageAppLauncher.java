package com.ubo.tp.message;

import com.ubo.tp.message.common.Constants;
import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.core.database.Database;
import com.ubo.tp.message.core.database.DbConnector;
import com.ubo.tp.message.core.database.EntityManager;
import com.ubo.tp.message.core.database.observer.DataBaseObserverImpl;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;
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

    protected static boolean IS_MOCK_ENABLED = true;
    protected static String EXCHANGE_DIRECTORY_PATH = "E:\\ihm";

    static void main(String[] args) throws InterruptedException {
        // Nettoyage du répertoire d'échange (hors EDT, c'est correct)
        try {
            java.io.File exchangeDir = new java.io.File(EXCHANGE_DIRECTORY_PATH);
            if (!exchangeDir.exists()) exchangeDir.mkdirs();
            clearExchangeDirectoryFiles();
        } catch (Exception e) {
            System.err.println("Impossible de nettoyer le répertoire d'échange : " + e.getMessage());
        }

        Logger logger = LoggerFactory.consoleLogger(LogLevel.DEBUG);
        Database database = new Database();
        EntityManager entityManager = new EntityManager(database);
        IDataManager dataManager = new DataManager(database, entityManager, logger);
        dataManager.setExchangeDirectory(EXCHANGE_DIRECTORY_PATH);

        createTestData(dataManager, entityManager);

        DbConnector dbConnector = new DbConnector(database);
        MessageAppMock mock = new MessageAppMock(dbConnector, dataManager);

        // TOUTE construction et affichage de l'UI doit se faire sur l'EDT
        SwingUtilities.invokeLater(() -> {
            //mock.showGUI();
            MessageApp messageApp = new MessageApp(dataManager, logger);
            messageApp.init();
            messageApp.show();
        });
    }

    public static void createTestData(IDataManager dataManager, EntityManager entityManager) {
        User user1 = new User("toto", "toto", "Toto");
        User user2 = new User("alice", "alice", "Alice");
        User user3 = new User("bob", "bob", "Bob");

        dataManager.sendUser(user1);
        dataManager.sendUser(user2);
        dataManager.sendUser(user3);

        Channel channel1 = new Channel(user1, "general");
        Channel channel2 = new Channel(user1, "random");

        dataManager.sendChannel(channel1);
        dataManager.sendChannel(channel2);

        Message m1 = new Message(UUID.randomUUID(),user1, channel1.getUuid(), 0, "Bonjour de " + user1.getName());
        Message m2 = new Message(UUID.randomUUID(),user2, channel1.getUuid(), 124, "Bonjour de " + user2.getName());
        Message m3 = new Message(UUID.randomUUID(),user3, channel1.getUuid(), 1708425600, "Bonjour de " + user3.getName());

        dataManager.sendMessage(m1);
        dataManager.sendMessage(m2);
        dataManager.sendMessage(m3);
    }

    public static void clearExchangeDirectoryFiles() {
        if (EXCHANGE_DIRECTORY_PATH == null) return;
        File dir = new File(EXCHANGE_DIRECTORY_PATH);
        if (!dir.exists() || !dir.isDirectory()) return;

        File[] files = dir.listFiles((d, name) ->
                name.endsWith(Constants.USER_FILE_EXTENSION) ||
                        name.endsWith(Constants.MESSAGE_FILE_EXTENSION) ||
                        name.endsWith(Constants.CHANNEL_FILE_EXTENSION)
        );
        if (files == null) return;
        for (File f : files) {
            try {
                f.delete();
            } catch (Exception ignored) {
            }
        }
    }
}