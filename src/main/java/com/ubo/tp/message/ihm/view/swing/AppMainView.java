package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.ihm.service.IAppMainView;
import com.ubo.tp.message.ihm.service.View;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Vue principale de l'application (fenêtre principale).
 */
public class AppMainView extends JComponent implements IAppMainView {

    public static final String DEFAULT_VIEW_ID = "default";
    private final JFrame mainFrame;
    private final Logger logger;
    private final JPanel contentPanel;
    private final CardLayout contentLayout;
    private Consumer<String> onExchangeDirectorySelected;

    public AppMainView(Logger logger) {
        this.logger = logger;
        this.logger.info("Initialisation de AppMainView");

        this.mainFrame = new JFrame("MessageApp");
        this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mainFrame.setSize(800, 600);
        // Empêcher qu'un revalidation/repaint ne réduise la fenêtre : définir une taille minimale
        this.mainFrame.setMinimumSize(new Dimension(800, 600));
        Image iconImage = this.loadIcon("/images/logo_20.png");
        this.mainFrame.setIconImage(iconImage);

        this.contentLayout = new CardLayout();
        this.contentPanel = new JPanel(contentLayout);
        this.mainFrame.getContentPane().add(this.contentPanel, BorderLayout.CENTER);

        this.createMenuBar();
        this.logger.info("AppMainView initialisée");
    }

    // -------------------------------------------------------------------------
    // API publique
    // -------------------------------------------------------------------------

    @Override
    public void setMainContent(View view) {
        this.addView(DEFAULT_VIEW_ID, view);
        this.showView(DEFAULT_VIEW_ID);
    }

    @Override
    public void addView(String id, View view) {
        if (view == null || id == null) return;

        // Exécuter sur l'EDT, mais de façon synchrone si on y est déjà
        // pour éviter que addView et showView soient séparés par d'autres events
        Runnable task = () -> {
            JComponent component = (JComponent) view;

            // Retirer l'ancienne vue avec ce même id si elle existe
            // Note : le nom est posé sur le wrapper AVANT l'ajout
            Component[] existing = this.contentPanel.getComponents();
            for (Component c : existing) {
                if (id.equals(c.getName())) {
                    this.contentPanel.remove(c);
                    break;
                }
            }

            // Créer le wrapper et lui donner son nom AVANT de l'ajouter
            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setName(id); // <-- DOIT être avant contentPanel.add()

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            wrapper.add(component, gbc);

            this.contentPanel.add(wrapper, id);
            this.contentPanel.revalidate();
            this.contentPanel.repaint();
        };

        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }

    @Override
    public void showView(String id) {
        if (id == null) return;

        Runnable task = () -> this.contentLayout.show(this.contentPanel, id);

        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }

    @Override
    public void removeView(String id) {
        if (id == null) return;

        Runnable task = () -> {
            Component[] comps = this.contentPanel.getComponents();
            for (Component c : comps) {
                if (id.equals(c.getName())) {
                    this.contentPanel.remove(c);
                    this.contentPanel.revalidate();
                    this.contentPanel.repaint();
                    break;
                }
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }

    @Override
    public void setVisibility(boolean visible) {
        this.logger.debug("Request to show main frame");
        Runnable task = () -> {
            this.logger.debug("Showing main frame on EDT");
            this.mainFrame.setVisible(true);
        };
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }

    @Override
    public void setOnExchangeDirectorySelected(Consumer<String> onExchangeDirectorySelected) {
        this.onExchangeDirectorySelected = onExchangeDirectorySelected;
    }

    // -------------------------------------------------------------------------
    // Internals
    // -------------------------------------------------------------------------

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Fichier");

        JMenuItem selectDirItem = new JMenuItem("Sélectionner répertoire",
                new ImageIcon(Objects.requireNonNull(this.loadIcon("/images/editIcon_20.png"))));
        selectDirItem.addActionListener(e -> this.showFileChooser());
        fileMenu.add(selectDirItem);
        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Quitter",
                new ImageIcon(Objects.requireNonNull(this.loadIcon("/images/exitIcon_20.png"))));
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Aide");
        JMenuItem aboutItem = new JMenuItem("À propos",
                new ImageIcon(Objects.requireNonNull(this.loadIcon("/images/logo_20.png"))));
        aboutItem.addActionListener(e -> this.showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        this.mainFrame.setJMenuBar(menuBar);
        this.logger.debug("MenuBar créé");
    }

    private void showAboutDialog() {
        this.logger.debug("Afficher le dialogue À propos");
        JDialog aboutDialog = new JDialog(this.mainFrame, "À propos", true);
        aboutDialog.setSize(400, 250);
        aboutDialog.setLocationRelativeTo(this.mainFrame);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Image logoImage = this.loadIcon("/images/logo_50.png");
        JLabel logoLabel = new JLabel(new ImageIcon(Objects.requireNonNull(logoImage)));
        panel.add(logoLabel, BorderLayout.WEST);

        panel.add(getTextPanel(), BorderLayout.CENTER);

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

    private void showFileChooser() {
        this.logger.debug("Afficher FileChooser");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Sélectionner le répertoire d'échange");
        fileChooser.setApproveButtonText("Sélectionner");
        fileChooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));

        int result = fileChooser.showOpenDialog(this.mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedDirectory = fileChooser.getSelectedFile();
            this.logger.info("Répertoire sélectionné : " + selectedDirectory.getAbsolutePath());
            if (this.onExchangeDirectorySelected != null) {
                this.onExchangeDirectorySelected.accept(selectedDirectory.getAbsolutePath());
            }
        } else {
            this.logger.debug("Sélection annulée");
        }
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