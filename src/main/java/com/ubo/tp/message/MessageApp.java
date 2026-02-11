package com.ubo.tp.message;

import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.controller.impl.AppMainController;
import com.ubo.tp.message.ihm.service.IAppMainView;

import javax.swing.*;
import java.io.File;

/**
 * Classe principale l'application.
 *
 * @author S.Lucas
 */
public class MessageApp {
	/**
	 * Base de données.
	 */
	protected IDataManager mDataManager;

	/**
	 * Contrôleur de la vue principale de l'application.
	 */
	protected AppMainController mMainController;

	private final Logger logger;

	/**
	 * Constructeur.
	 *
	 * @param dataManager
	 */
	public MessageApp(IDataManager dataManager, Logger logger) {
		this.mDataManager = dataManager;
		this.logger = logger;
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
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			this.logger.debug("LookAndFeel défini sur le système");
		} catch (Exception e) {
			this.logger.warn("Impossible de définir le Look and Feel natif: " + e.getMessage());
		}
	}

	/**
	 * Initialisation de l'interface graphique.
	 */
	protected void initGui() {
		// this.mMainController...
		mMainController = new AppMainController(mDataManager, logger);


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
		mMainController.getView().show();
	}


	public IDataManager getmDataManager() {
		return mDataManager;
	}

	public IAppMainView getmMainView() {
		return mMainController != null ? mMainController.getView() : null;
	}
}
