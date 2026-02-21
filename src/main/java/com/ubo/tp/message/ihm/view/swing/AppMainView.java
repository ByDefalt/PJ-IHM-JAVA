package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.utils.LoadIcon;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Vue principale de l'application (fenêtre principale).
 */
public class AppMainView extends JComponent implements View {

    private final JFrame mainFrame;
    private final ViewContext viewContext;
    private final JPanel contentPanel;
    private final CardLayout contentLayout;
    private JMenu connectMenu;
    private Consumer<String> onExchangeDirectorySelected;

    public AppMainView(ViewContext viewContext) {
        this.viewContext = viewContext;
        this.viewContext.logger().info("Initialisation de AppMainView");

        this.mainFrame = new JFrame("MessageApp");
        this.mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.mainFrame.setSize(800, 600);
        this.mainFrame.setMinimumSize(new Dimension(800, 600));
        Image iconImage = LoadIcon.loadIcon("/images/logo_20.png");
        this.mainFrame.setIconImage(iconImage);

        this.mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                viewContext.logger().info("Fermeture de l'application demandée");
                try {
                    if (viewContext.session() != null && viewContext.session().getConnectedUser() != null) {
                        viewContext.logger().info("Déconnexion en cours...");
                        new Thread(() -> {
                            try {
                                viewContext.session().disconnect();
                            } catch (Exception ex) {
                                viewContext.logger().error("Erreur lors de la déconnexion", ex);
                            } finally {
                                SwingUtilities.invokeLater(() -> {
                                    mainFrame.dispose();
                                    System.exit(0);
                                });
                            }
                        }, "disconnect-thread").start();
                    } else {
                        mainFrame.dispose();
                        System.exit(0);
                    }
                } catch (Exception ex) {
                    viewContext.logger().error("Erreur lors du traitement de la fermeture", ex);
                    mainFrame.dispose();
                    System.exit(0);
                }
            }
        });

        this.contentLayout = new CardLayout();
        this.contentPanel = new JPanel(contentLayout);
        this.mainFrame.getContentPane().add(this.contentPanel, BorderLayout.CENTER);

        this.createMenuBar();
        this.viewContext.logger().info("AppMainView initialisée");
    }

    /**
     * Affiche ou masque le menu "Connexion" selon l'état de la session.
     * Peut être appelée depuis n'importe quel thread.
     */
    public void setConnectMenuVisible(boolean visible) {
        SwingUtilities.invokeLater(() -> {
            connectMenu.setVisible(visible);
            // Forcer le rafraîchissement de la barre de menu
            mainFrame.getJMenuBar().revalidate();
            mainFrame.getJMenuBar().repaint();
        });
    }

    /**
     * Remplace le contenu central de la fenêtre par le composant donné.
     * Peut être appelée depuis n'importe quel thread.
     */
    public void setContent(JComponent component) {
        if (component == null) return;

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> setContent(component));
            return;
        }

        viewContext.logger().debug("Setting main view");
        contentPanel.removeAll();

        JPanel wrapper = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        wrapper.add(component, gbc);

        contentPanel.add(wrapper);
        contentPanel.revalidate();
        contentPanel.repaint();
        viewContext.logger().debug("Main view set");
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu Fichier
        JMenu fileMenu = new JMenu("Fichier");

        JMenuItem selectDirItem = new JMenuItem("Sélectionner répertoire",
                new ImageIcon(Objects.requireNonNull(LoadIcon.loadIcon("/images/editIcon_20.png"))));
        selectDirItem.addActionListener(e -> this.showFileChooser());
        fileMenu.add(selectDirItem);
        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Quitter",
                new ImageIcon(Objects.requireNonNull(LoadIcon.loadIcon("/images/exitIcon_20.png"))));
        exitItem.addActionListener(e -> mainFrame.dispatchEvent(
                new java.awt.event.WindowEvent(mainFrame, java.awt.event.WindowEvent.WINDOW_CLOSING)));
        fileMenu.add(exitItem);

        // Menu Aide
        JMenu helpMenu = new JMenu("Aide");
        JMenuItem aboutItem = new JMenuItem("À propos",
                new ImageIcon(Objects.requireNonNull(LoadIcon.loadIcon("/images/logo_20.png"))));
        aboutItem.addActionListener(_ -> this.showAboutDialog());
        helpMenu.add(aboutItem);

        // Menu Connexion — caché par défaut, affiché uniquement si connecté
        this.connectMenu = new JMenu("Connexion");
        this.connectMenu.setVisible(false);

        JMenuItem disconnectItem = new JMenuItem("Déconnexion");
        disconnectItem.addActionListener(_ -> {
            if (this.viewContext.session().getConnectedUser() != null) {
                this.viewContext.session().disconnect();
            }
        });
        this.connectMenu.add(disconnectItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        menuBar.add(this.connectMenu);

        this.mainFrame.setJMenuBar(menuBar);
        this.viewContext.logger().debug("MenuBar créé");
    }

    private void showAboutDialog() {
        this.viewContext.logger().debug("Afficher le dialogue À propos");
        JDialog aboutDialog = new JDialog(this.mainFrame, "À propos", true);
        aboutDialog.setSize(400, 250);
        aboutDialog.setLocationRelativeTo(this.mainFrame);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Image logoImage = LoadIcon.loadIcon("/images/logo_50.png");
        JLabel logoLabel = new JLabel(new ImageIcon(Objects.requireNonNull(logoImage)));
        panel.add(logoLabel, BorderLayout.WEST);

        panel.add(createTextPanel(), BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> aboutDialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        aboutDialog.add(panel);
        aboutDialog.setVisible(true);
        this.viewContext.logger().debug("Dialogue À propos affiché");
    }

    private void showFileChooser() {
        this.viewContext.logger().debug("Afficher FileChooser");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Sélectionner le répertoire d'échange");
        fileChooser.setApproveButtonText("Sélectionner");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        int result = fileChooser.showOpenDialog(this.mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            this.viewContext.logger().info("Répertoire sélectionné : " + selectedDirectory.getAbsolutePath());
            if (this.onExchangeDirectorySelected != null) {
                this.onExchangeDirectorySelected.accept(selectedDirectory.getAbsolutePath());
            }
        } else {
            this.viewContext.logger().debug("Sélection annulée");
        }
    }

    private JPanel createTextPanel() {
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

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public ViewContext getViewContext() {
        return viewContext;
    }

    public Consumer<String> getOnExchangeDirectorySelected() {
        return onExchangeDirectorySelected;
    }

    public void setOnExchangeDirectorySelected(Consumer<String> onExchangeDirectorySelected) {
        this.onExchangeDirectorySelected = onExchangeDirectorySelected;
    }
}