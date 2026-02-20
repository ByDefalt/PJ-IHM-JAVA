package com.ubo.tp.message;

import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.core.database.observer.DataBaseObserverImpl;
import com.ubo.tp.message.core.database.Database;
import com.ubo.tp.message.core.database.DbConnector;
import com.ubo.tp.message.core.database.EntityManager;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.logger.LogLevel;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.logger.LoggerFactory;
import mock.MessageAppMock;

/**
 * Classe de lancement de l'application.
 *
 * @author S.Lucas
 */
public class MessageAppLauncher {

    /**
     * Indique si le mode bouchoné est activé.
     */
    protected static boolean IS_MOCK_ENABLED = true;

	protected static String EXCHANGE_DIRECTORY_PATH = "E:\\ihm";

    /**
     * Launcher.
     *
     * @param args
     */
    static void main(String[] args) throws InterruptedException {
        Logger logger = LoggerFactory.consoleLogger(LogLevel.DEBUG);

        Database database = new Database();

        EntityManager entityManager = new EntityManager(database);


        IDataManager dataManager = new DataManager(database, entityManager, logger);
        DataBaseObserverImpl dataBaseObserver = new DataBaseObserverImpl();
        dataManager.addObserver(dataBaseObserver);
        dataManager.setExchangeDirectory(EXCHANGE_DIRECTORY_PATH);

        // Vider le répertoire d'échange avant de créer le jeu de test
        try {
            // s'assurer que le répertoire existe
            java.io.File exchangeDir = new java.io.File(EXCHANGE_DIRECTORY_PATH);
            if (!exchangeDir.exists()) {
                exchangeDir.mkdirs();
            }
            // supprimer les fichiers d'échange existants
            entityManager.clearExchangeDirectoryFiles();
        } catch (Exception e) {
            System.err.println("Impossible de nettoyer le répertoire d'échange : " + e.getMessage());
        }

        // Création simplifiée et unifiée des données de test (le répertoire a été vidé)
        createTestData(dataManager, entityManager);

        DbConnector dbConnector = new DbConnector(database);

		MessageAppMock mock = new MessageAppMock(dbConnector, dataManager);
		mock.showGUI();
		MessageApp messageApp = new MessageApp(dataManager, logger);
		messageApp.init();
		messageApp.show();


    }

    /**
     * Crée un petit jeu de données de test (utilisateurs, canaux, messages).
     * Le répertoire d'échange est supposé vidé avant l'appel à cette méthode.
     *
     * @param dataManager   gestionnaire de données (utilisé pour écrire les fichiers)
     * @param entityManager accès bas niveau (fourni si besoin)
     */
    public static void createTestData(IDataManager dataManager, EntityManager entityManager) {
        // Création simple d'utilisateurs
        User user1 = new User("toto", "toto", "Toto");
        User user2 = new User("alice", "alice", "Alice");
        User user3 = new User("bob", "bob", "Bob");

        dataManager.sendUser(user1);
        dataManager.sendUser(user2);
        dataManager.sendUser(user3);

        // Création simple de canaux (créateur = user1)
        Channel channel1 = new Channel(user1, "general");
        Channel channel2 = new Channel(user1, "random");

        dataManager.sendChannel(channel1);
        dataManager.sendChannel(channel2);

        // Création de quelques messages (vers channel1)
        Message m1 = new Message(user1, channel1.getUuid(), "Bonjour de " + user1.getName());
        Message m2 = new Message(user2, channel1.getUuid(), "Bonjour de " + user2.getName());
        Message m3 = new Message(user3, channel1.getUuid(), "Bonjour de " + user3.getName());

        dataManager.sendMessage(m1);
        dataManager.sendMessage(m2);
        dataManager.sendMessage(m3);
    }
}
