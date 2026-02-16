package com.ubo.tp.message;

import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.core.database.DataBaseObserverImpl;
import com.ubo.tp.message.core.database.Database;
import com.ubo.tp.message.core.database.DbConnector;
import com.ubo.tp.message.core.database.EntityManager;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;
import mock.MessageAppMock;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.logger.LoggerFactory;
import com.ubo.tp.message.logger.LogLevel;

import java.util.UUID;

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

	/**
	 * Launcher.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		Logger logger = LoggerFactory.consoleLogger(LogLevel.DEBUG);

		Database database = new Database();

		EntityManager entityManager = new EntityManager(database);


		IDataManager dataManager = new DataManager(database, entityManager, logger);
        DataBaseObserverImpl dataBaseObserver = new DataBaseObserverImpl();
		dataManager.addObserver(dataBaseObserver);
		dataManager.setExchangeDirectory("E:\\ihm");
		User user = new User("toto", "toto", "toto");
		dataManager.sendUser(user);
		Message message = new Message(user, new UUID(12,12), "coucou");
		dataManager.sendMessage(message);

		DbConnector dbConnector = new DbConnector(database);


		if (IS_MOCK_ENABLED) {
			MessageAppMock mock = new MessageAppMock(dbConnector, dataManager);
			mock.showGUI();
		}

		MessageApp messageApp = new MessageApp(dataManager, logger);
		messageApp.init();
		messageApp.show();

	}
}
