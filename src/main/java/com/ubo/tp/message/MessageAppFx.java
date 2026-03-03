package com.ubo.tp.message;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.impl.NavigationController;
import com.ubo.tp.message.controller.service.IAppMainController;
import com.ubo.tp.message.controller.service.INavigationController;
import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.core.database.Database;
import com.ubo.tp.message.core.database.DbConnector;
import com.ubo.tp.message.core.database.EntityManager;
import com.ubo.tp.message.core.selected.ISelected;
import com.ubo.tp.message.core.selected.Selected;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.core.session.Session;
import com.ubo.tp.message.factory.view.javafx.ComposantJavaFXFactory;
import com.ubo.tp.message.factory.view.ViewFactory;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.logger.LogLevel;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.logger.LoggerFactory;
import javafx.application.Application;
import javafx.stage.Stage;
import mock.MessageAppMock;

/**
 * Point d'entrée JavaFX de l'application.
 * <p>
 * Peut fonctionner en deux modes :
 * <ul>
 *   <li><b>Mode autonome</b> : lancé via {@link MessageAppFxLauncher}, crée sa
 *       propre infrastructure (Database, DataManager…). Les deux UIs communiquent
 *       alors via le répertoire d'échange (polling fichier).</li>
 *   <li><b>Mode partagé</b> : un {@link ControllerContext} est injecté via
 *       {@link #setSharedContext} avant l'appel à {@code Application.launch()}.
 *       Les deux UIs partagent alors exactement la même base en mémoire et
 *       voient instantanément les messages de l'autre.</li>
 * </ul>
 */
public class MessageAppFx extends Application {

    private static final String EXCHANGE_DIRECTORY_PATH =
            System.getProperty("exchange.dir", "E:\\ihm");
    private static final boolean IS_MOCK_ENABLED = false;

    // -------------------------------------------------------------------------
    // Contexte partagé injecté depuis l'extérieur (mode dual-UI dans un seul JVM)
    // Seul le DataManager est partagé — session et selected restent propres à chaque UI.
    // -------------------------------------------------------------------------
    private static ControllerContext sharedControllerContext = null;

    /**
     * Injecter un contexte existant avant l'appel à {@code Application.launch()}.
     * Si non appelé, {@link MessageAppFx} crée son propre contexte.
     */
    public static void setSharedContext(ControllerContext ctx) {
        sharedControllerContext = ctx;
    }

    // -------------------------------------------------------------------------

    @Override
    public void start(Stage primaryStage) {
        final ControllerContext controllerContext;

        if (sharedControllerContext != null) {
            // --- Mode partagé : on réutilise uniquement le DataManager et le logger ---
            // Session et Selected sont propres à cette UI JavaFX.
            Logger logger = sharedControllerContext.logger();
            IDataManager dataManager = sharedControllerContext.dataManager();
            ISession session = new Session();
            ISelected selected = new Selected();
            controllerContext = new ControllerContext(logger, dataManager, session, selected);
            logger.info("(FX) Démarrage en mode partagé — même DataManager que Swing");
        } else {
            // --- Mode autonome : on crée une infrastructure indépendante ---
            Logger logger = LoggerFactory.consoleLogger(LogLevel.DEBUG);
            logger.info("(FX) Démarrage en mode autonome");

            Database database = new Database();
            EntityManager entityManager = new EntityManager(database);
            IDataManager dataManager = new DataManager(database, entityManager, logger);
            dataManager.setExchangeDirectory(EXCHANGE_DIRECTORY_PATH);

            ISession session = new Session();
            MessageAppLauncher.createTestData(dataManager);

            DbConnector dbConnector = new DbConnector(database);
            MessageAppMock mock = new MessageAppMock(dbConnector, dataManager);
            if (IS_MOCK_ENABLED) mock.showGUI();

            ISelected selected = new Selected();
            controllerContext = new ControllerContext(logger, dataManager, session, selected);
        }

        // Holder pour casser la dépendance circulaire factory ↔ navigationController
        ComposantJavaFXFactory[] factoryHolder = new ComposantJavaFXFactory[1];

        INavigationController navigationController = new NavigationController(
                controllerContext,
                new ViewFactory() {
                    @Override public IAppMainController createAppMainController()  { return factoryHolder[0].createAppMainController(); }
                    @Override public View createLoginView()                         { return factoryHolder[0].createLoginView(); }
                    @Override public View createRegisterView()                      { return factoryHolder[0].createRegisterView(); }
                    @Override public View createUpdateAccountView()                 { return factoryHolder[0].createUpdateAccountView(); }
                    @Override public View createChatMainView()                      { return factoryHolder[0].createChatMainView(); }
                }
        );

        ViewContext viewContext = new ViewContext(
                controllerContext.logger(),
                controllerContext.session(),
                controllerContext.selected(),
                navigationController
        );
        factoryHolder[0] = new ComposantJavaFXFactory(controllerContext, viewContext, primaryStage);

        IAppMainController mainController = factoryHolder[0].createAppMainController();
        mainController.getGraphicController().setVisibility(true);
    }
}

