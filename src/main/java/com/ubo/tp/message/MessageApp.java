package com.ubo.tp.message;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IAppMainController;
import com.ubo.tp.message.factory.ComposantSwingFactory;
import com.ubo.tp.message.theme.DiscordTheme;

import java.io.File;

/**
 * Classe principale l'application.
 *
 * @author S.Lucas
 */
public class MessageApp {
    private final ControllerContext controllerContext;
    protected IAppMainController mMainController;

    public MessageApp(ControllerContext controllerContext) {
        this.controllerContext = controllerContext;
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
            this.controllerContext.logger().debug("Thème Discord appliqué");
        } catch (Exception e) {
            this.controllerContext.logger().warn("Impossible de définir le Look and Feel natif: " + e.getMessage());
        }
    }

    /**
     * Initialisation de l'interface graphique.
     */
    protected void initGui() {
        mMainController = ComposantSwingFactory.createAppMainController();
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
        this.controllerContext.dataManager().setExchangeDirectory(directoryPath);
    }

    public void show() {
        mMainController.getGraphicController().setVisibility(true);
    }

}
