package com.ubo.tp.message.ihm.screen;

import com.ubo.tp.message.ihm.service.IMessageAppMainView;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Vue principale de l'application (fenêtre principale).
 * <p>
 * Expose une API pour ajouter / afficher / retirer des vues secondaires via
 * un identifiant, et fournit un callback pour la sélection du répertoire d'échange.
 * </p>
 */
public class AppMainView extends View implements IMessageAppMainView {


    private final JFrame mainFrame;
    private final Logger logger;

    // Panneau central où on injecte le contenu (login, main app, etc.)
    private final JPanel contentPanel;
    private final CardLayout contentLayout;

    // Callback appelé quand un répertoire d'échange est sélectionné
    private Consumer<String> onExchangeDirectorySelected;

    public static final String DEFAULT_VIEW_ID = "default";

    /**
     * Crée et configure la fenêtre principale.
     *
     * @param logger logger de l'application
     */
    public AppMainView(Logger logger) {
        this.logger = logger;

        this.logger.info("Initialisation de AppMainView");

        this.mainFrame = new JFrame("MessageApp");
        this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mainFrame.setSize(800, 600);
        Image iconImage = this.loadIcon("/images/logo_20.png");
        this.mainFrame.setIconImage(iconImage);

        this.contentLayout = new CardLayout();
        this.contentPanel = new JPanel(contentLayout);
        this.mainFrame.getContentPane().add(this.contentPanel, BorderLayout.CENTER);

        this.createMenuBar();

        this.logger.info("AppMainView initialisée");
    }


    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu Fichier
        JMenu fileMenu = new JMenu("Fichier");

        // Sélecteur de fichier
        JMenuItem selectDirItem = new JMenuItem("Sélectionner répertoire", new ImageIcon(Objects.requireNonNull(this.loadIcon("/images/editIcon_20.png"))));
        selectDirItem.addActionListener(e -> this.showFileChooser());
        fileMenu.add(selectDirItem);

        fileMenu.addSeparator(); // Séparateur

        JMenuItem exitItem = new JMenuItem("Quitter", new ImageIcon(Objects.requireNonNull(this.loadIcon("/images/exitIcon_20.png"))));
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // Menu Aide
        JMenu helpMenu = new JMenu("Aide");
        JMenuItem aboutItem = new JMenuItem("À propos", new ImageIcon(Objects.requireNonNull(this.loadIcon("/images/logo_20.png"))));
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
        JLabel logoLabel = new JLabel(new ImageIcon(Objects.requireNonNull(logoImage)));
        panel.add(logoLabel, BorderLayout.WEST);

        // Texte À propos
        JPanel textPanel = getTextPanel();
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

    private JPanel getTextPanel() {
        JPanel textPanel = new JPanel(new BorderLayout(5, 5));
        JLabel titleLabel = new JLabel("MessageApp");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JTextArea descArea = new JTextArea(
                """
                        Application de messagerie
                        
                        Version 1.0
                        
                        © 2026 Message App"""
        );
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setOpaque(false);
        descArea.setFont(new Font("Arial", Font.PLAIN, 12));

        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descArea, BorderLayout.CENTER);
        return textPanel;
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

            // notifier le contrôleur / callback
            if (this.onExchangeDirectorySelected != null) {
                this.onExchangeDirectorySelected.accept(selectedDirectory.getAbsolutePath());
            }

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

    @Override
    public void setOnExchangeDirectorySelected(Consumer<String> onExchangeDirectorySelected) {
        this.onExchangeDirectorySelected = onExchangeDirectorySelected;
    }

    @Override
    public void setMainContent(JComponent component) {
        // alias: add with default id and show it
        this.addView(DEFAULT_VIEW_ID, component);
        this.showView(DEFAULT_VIEW_ID);
    }

    @Override
    public void addView(String id, JComponent component) {
        if (component == null || id == null) return;
        SwingUtilities.invokeLater(() -> {
            JPanel wrapper = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            wrapper.add(component, gbc);

            // tag the wrapper so we can find/remove it later
            wrapper.setName(id);

            this.contentPanel.add(wrapper, id);
            this.contentPanel.revalidate();
            this.contentPanel.repaint();
        });
    }

    @Override
    public void addView(String id, JComponent component, GridBagConstraints constraints) {
        if (component == null || id == null) return;
        SwingUtilities.invokeLater(() -> {
            JPanel wrapper = new JPanel(new GridBagLayout());
            // Appliquer les contraintes fournies
            wrapper.add(component, constraints);

            // tag the wrapper for removal/lookup
            wrapper.setName(id);

            this.contentPanel.add(wrapper, id);
            this.contentPanel.revalidate();
            this.contentPanel.repaint();
        });
    }

    @Override
    public void showView(String id) {
        if (id == null) return;
        SwingUtilities.invokeLater(() -> {
            this.contentLayout.show(this.contentPanel, id);
        });
    }

    @Override
    public void removeView(String id) {
        if (id == null) return;
        SwingUtilities.invokeLater(() -> {
            Component[] comps = this.contentPanel.getComponents();
            for (Component c : comps) {
                if (id.equals(c.getName())) {
                    this.contentPanel.remove(c);
                    this.contentPanel.revalidate();
                    this.contentPanel.repaint();
                    break;
                }
            }
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
