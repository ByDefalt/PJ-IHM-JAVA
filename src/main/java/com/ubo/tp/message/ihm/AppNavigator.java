package com.ubo.tp.message.ihm;

import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * Navigator centralisé pour afficher des vues dans la fenêtre principale.
 */
public final class AppNavigator {

    private static final AppNavigator INSTANCE = new AppNavigator();

    private JFrame mainFrame;
    private JTabbedPane tabbedPane;
    private Logger logger;

    private AppNavigator() {
    }

    public static AppNavigator getInstance() {
        return INSTANCE;
    }

    // Surcharge pour injection "constructor-like" du logger
    public static AppNavigator getInstance(Logger logger) {
        AppNavigator inst = INSTANCE;
        if (inst.logger == null && logger != null) {
            inst.logger = logger;
            inst.logger.debug("AppNavigator logger injected");
        }
        return inst;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
        if (this.logger != null) this.logger.debug("AppNavigator logger set via setter");
    }

    /**
     * Initialise le navigator avec la frame principale. Installe un JTabbedPane
     * en tant que content pane central.
     */
    public void setMainFrame(JFrame frame) {
        this.mainFrame = frame;
        Container c = frame.getContentPane();
        c.removeAll();
        c.setLayout(new BorderLayout());
        this.tabbedPane = new JTabbedPane();
        c.add(this.tabbedPane, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
        frame.pack();
        if (logger != null) logger.debug("AppNavigator initialized with main frame");
    }

    /**
     * Ajoute un onglet avec la vue fournie et la sélectionne. Si un onglet avec
     * le même titre existe déjà, le sélectionne à la place.
     */
    public void addTab(String title, JComponent view) {
        if (this.tabbedPane == null) {
            showViewSingle(view);
            return;
        }
        int idx = this.tabbedPane.indexOfTab(title);
        if (idx >= 0) {
            this.tabbedPane.setSelectedIndex(idx);
            if (logger != null) logger.info("Selected existing tab: " + title);
            return;
        }
        this.tabbedPane.addTab(title, view);
        this.tabbedPane.setSelectedComponent(view);
        if (logger != null) logger.info("Added new tab: " + title);
    }

    /**
     * Supprime l'onglet identifié par le titre.
     */
    public void removeTab(String title) {
        if (this.tabbedPane == null) return;
        int idx = this.tabbedPane.indexOfTab(title);
        if (idx >= 0) {
            this.tabbedPane.remove(idx);
            if (logger != null) logger.info("Removed tab: " + title);
        }
    }

    /**
     * Ajoute un onglet contenant un JSplitPane (left|right).
     * dividerLocation : 0.0 (left fixed) .. 1.0 (right fixed)
     */
    public void addSplit(String title, JComponent left, JComponent right, double dividerLocation) {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(dividerLocation);
        if (this.tabbedPane == null) {
            showViewSingle(split);
        } else {
            int idx = this.tabbedPane.indexOfTab(title);
            if (idx >= 0) {
                this.tabbedPane.setSelectedIndex(idx);
                if (logger != null) logger.info("Selected existing split tab: " + title);
                return;
            }
            this.tabbedPane.addTab(title, split);
            this.tabbedPane.setSelectedComponent(split);
            if (logger != null) logger.info("Added split tab: " + title);
        }
    }

    /**
     * Affiche une vue seule (remplace le content pane central) — pas d'onglets.
     */
    public void showViewSingle(JComponent view) {
        if (this.mainFrame == null) return;
        Container c = this.mainFrame.getContentPane();
        c.removeAll();
        c.setLayout(new BorderLayout());
        c.add(view, BorderLayout.CENTER);
        this.mainFrame.revalidate();
        this.mainFrame.repaint();
        this.mainFrame.pack();
        if (logger != null) logger.info("Displayed single view");
    }
}
