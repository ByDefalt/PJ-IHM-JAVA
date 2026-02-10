package com.ubo.tp.message;

import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.database.DataBaseObserverImpl;
import com.ubo.tp.message.core.database.Database;
import com.ubo.tp.message.core.database.DbConnector;
import com.ubo.tp.message.core.database.EntityManager;
import com.ubo.tp.message.ihm.MessageApp;
import mock.MessageAppMock;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.logger.LoggerFactory;
import com.ubo.tp.message.logger.LogLevel;

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

		Database database = new Database();

		EntityManager entityManager = new EntityManager(database);

		DataManager dataManager = new DataManager(database, entityManager);
		DataBaseObserverImpl dataBaseObserver = new DataBaseObserverImpl();
		dataManager.addObserver(dataBaseObserver);

		DbConnector dbConnector = new DbConnector(database);

		Logger logger = LoggerFactory.consoleLogger(LogLevel.INFO);

		if (IS_MOCK_ENABLED) {
			MessageAppMock mock = new MessageAppMock(dbConnector, dataManager);
			mock.showGUI();
		}

		MessageApp messageApp = new MessageApp(dataManager, logger);
		messageApp.init();
		messageApp.show();

	}
}
