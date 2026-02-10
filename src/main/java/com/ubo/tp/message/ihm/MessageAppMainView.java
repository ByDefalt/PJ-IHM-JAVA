package com.ubo.tp.message.ihm;

import com.ubo.tp.message.controller.LoginController;
import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.ihm.screen.LoginView;
import com.ubo.tp.message.logger.LogLevel;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.logger.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Classe de la vue principale de l'application.
 */
public class MessageAppMainView {


    private final DataManager dataManager;
    private final JFrame mainFrame;
    private final LoginView loginView;
    private final Logger logger;

    public MessageAppMainView(DataManager dataManager, Logger logger) {
        this.dataManager = dataManager;
        this.logger = logger;

        this.logger.info("Initialisation de MessageAppMainView");

        // Configurer le Look and Feel natif du système
        this.setSystemLookAndFeel();

        this.mainFrame = new JFrame("MessageApp");
        this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mainFrame.setSize(800, 600);
        Image iconImage = this.loadIcon("/images/logo_20.png");
        this.mainFrame.setIconImage(iconImage);

        // Initialiser le navigator avec logger
        AppNavigator.getInstance(this.logger).setMainFrame(this.mainFrame);

        // Création et ajout de la loginView dans un onglet
        Logger lvLogger = LoggerFactory.consoleLogger(LogLevel.DEBUG);
        com.ubo.tp.message.controller.LoginController loginController = new com.ubo.tp.message.controller.LoginController(lvLogger);
        this.loginView = new LoginView(loginController, lvLogger);
        // Attacher la view au controller pour que celui-ci puisse initialiser les listeners
        loginController.setView(this.loginView);
        AppNavigator.getInstance().addTab("Connexion", this.loginView);

        // s'assurer que la frame a une taille raisonnable après ajout du contenu
        this.mainFrame.pack();
        this.mainFrame.setSize(800, 600); // garantir la taille désirée (overrides pack si nécessaire)
        this.mainFrame.setLocationRelativeTo(null); // centrer la fenêtre

        this.createMenuBar();

        this.logger.info("MessageAppMainView initialisée");
    }

    /**
     * Configure le Look and Feel natif du système d'exploitation.
     */
    private void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.logger.debug("LookAndFeel défini sur le système");
        } catch (Exception e) {
            this.logger.warn("Impossible de définir le Look and Feel natif: " + e.getMessage());
        }
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu Fichier
        JMenu fileMenu = new JMenu("Fichier");

        // Sélecteur de fichier
        JMenuItem selectDirItem = new JMenuItem("Sélectionner répertoire", new ImageIcon(this.loadIcon("/images/editIcon_20.png")));
        selectDirItem.addActionListener(e -> this.showFileChooser());
        fileMenu.add(selectDirItem);

        fileMenu.addSeparator(); // Séparateur

        JMenuItem exitItem = new JMenuItem("Quitter", new ImageIcon(this.loadIcon("/images/exitIcon_20.png")));
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // Menu Aide
        JMenu helpMenu = new JMenu("Aide");
        JMenuItem aboutItem = new JMenuItem("À propos", new ImageIcon(this.loadIcon("/images/logo_20.png")));
        aboutItem.addActionListener(e -> this.showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        this.mainFrame.setJMenuBar(menuBar);
        this.logger.debug("MenuBar créé");
    }

    /**
     * Affiche la boîte de dialogue À propos.
     */
    private void showAboutDialog() {
        this.logger.debug("Afficher le dialogue À propos");
        JDialog aboutDialog = new JDialog(this.mainFrame, "À propos", true);
        aboutDialog.setSize(400, 250);
        aboutDialog.setLocationRelativeTo(this.mainFrame);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Logo
        Image logoImage = this.loadIcon("/images/logo_50.png");
        JLabel logoLabel = new JLabel(new ImageIcon(logoImage));
        panel.add(logoLabel, BorderLayout.WEST);

        // Texte À propos
        JPanel textPanel = new JPanel(new BorderLayout(5, 5));
        JLabel titleLabel = new JLabel("MessageApp");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JTextArea descArea = new JTextArea(
            "Application de messagerie\n\n" +
            "Version 1.0\n\n" +
            "© 2026 Message App"
        );
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setOpaque(false);
        descArea.setFont(new Font("Arial", Font.PLAIN, 12));

        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descArea, BorderLayout.CENTER);
        panel.add(textPanel, BorderLayout.CENTER);

        // Bouton OK
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> aboutDialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        aboutDialog.add(panel);
        aboutDialog.setVisible(true);
        this.logger.debug("Dialogue À propos affiché");
    }

    /**
     * Affiche un sélecteur de fichier pour choisir un répertoire d'échange.
     */
    private void showFileChooser() {
        this.logger.debug("Afficher FileChooser");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Sélectionner le répertoire d'échange");
        fileChooser.setApproveButtonText("Sélectionner");

        // Définir le répertoire par défaut (home de l'utilisateur)
        fileChooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));

        int result = fileChooser.showOpenDialog(this.mainFrame);

        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedDirectory = fileChooser.getSelectedFile();
            this.logger.info("Répertoire sélectionné : " + selectedDirectory.getAbsolutePath());


            // logique link folder
            dataManager.setExchangeDirectory(selectedDirectory.getAbsolutePath());




        } else {
            this.logger.debug("Sélection annulée");
        }
    }

    public void show() {
        this.logger.debug("Request to show main frame");
        SwingUtilities.invokeLater(() -> {
            this.logger.debug("Showing main frame on EDT");
            this.mainFrame.setVisible(true);
        });
    }

    private Image loadIcon(String path) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url == null) {
                this.logger.warn("Ressource introuvable: " + path);
                return null;
            }
            return new ImageIcon(url).getImage();
        } catch (Exception e) {
            this.logger.error("Erreur lors du chargement de l'icone: " + path, e);
            return null;
        }
    }


}
