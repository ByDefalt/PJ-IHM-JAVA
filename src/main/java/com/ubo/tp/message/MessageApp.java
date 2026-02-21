package com.ubo.tp.message;

import com.ubo.tp.message.controller.service.IAppMainController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.factory.ComposantSwingFactory;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.theme.DiscordTheme;

import java.io.File;

/**
 * Classe principale l'application.
 *
 * @author S.Lucas
 */
public class MessageApp {
    private final Logger logger;
    /**
     * Base de données.
     */
    protected IDataManager mDataManager;
    /**
     * Contrôleur de la vue principale de l'application.
     */
    protected IAppMainController mMainController;

    protected ISession mSession;

    /**
     * Constructeur.
     *
     * @param dataManager
     */
    public MessageApp(IDataManager dataManager, Logger logger, ISession session) {
        this.mDataManager = dataManager;
        this.logger = logger;
        this.mSession = session;
    }

    /**
     * Initialisation de l'application.
     */
    public void init() {
        // Init du look and feel de l'application
        this.initLookAndFeel();

        // Initialisation de l'IHM
        this.initGui();

        // Initialisation du répertoire d'échange
        this.initDirectory();
    }

    /**
     * Initialisation du look and feel de l'application.
     */
    protected void initLookAndFeel() {
        try {
            DiscordTheme theme = new DiscordTheme();
            theme.apply();
            this.logger.debug("Thème Discord appliqué");
        } catch (Exception e) {
            this.logger.warn("Impossible de définir le Look and Feel natif: " + e.getMessage());
        }
    }

    /**
     * Initialisation de l'interface graphique.
     */
    protected void initGui() {
        mMainController = ComposantSwingFactory.createAppMainController(mDataManager, logger, mSession);
    }

    /**
     * Initialisation du répertoire d'échange (depuis la conf ou depuis un file
     * chooser). <br/>
     * <b>Le chemin doit obligatoirement avoir été saisi et être valide avant de
     * pouvoir utiliser l'application</b>
     */
    protected void initDirectory() {
    }

    /**
     * Indique si le fichier donné est valide pour servir de répertoire d'échange
     *
     * @param directory , Répertoire à tester.
     */
    protected boolean isValidExchangeDirectory(File directory) {
        // Valide si répertoire disponible en lecture et écriture
        return directory != null && directory.exists() && directory.isDirectory() && directory.canRead()
                && directory.canWrite();
    }

    /**
     * Initialisation du répertoire d'échange.
     *
     * @param directoryPath
     */
    protected void initDirectory(String directoryPath) {
        mDataManager.setExchangeDirectory(directoryPath);
    }

    public void show() {
        mMainController.getGraphicController().setVisibility(true);
    }

}
