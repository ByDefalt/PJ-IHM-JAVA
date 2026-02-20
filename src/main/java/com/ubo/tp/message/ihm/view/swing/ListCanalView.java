package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.ihm.service.ICanalView;
import com.ubo.tp.message.ihm.service.IListCanalView;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Vue affichant une liste de CanalView empilés verticalement dans une zone défilante.
 */
public class ListCanalView extends JComponent implements IListCanalView {

    private final Logger logger;

    private final JPanel canalsPanel;
    private final JScrollPane scrollPane;
    private final List<ICanalView> canalViews = new ArrayList<>();

    /**
     * Référence au glue pour le déplacer sans itérer pendant modification
     */
    private Component glue;

    public ListCanalView(Logger logger) {
        this.logger = logger;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(new Color(54, 57, 63));
        this.setOpaque(true);

        canalsPanel = createCanalsPanel();
        scrollPane = createScrollPane(canalsPanel);
        addScrollPaneToThis();

        // Glue initial
        glue = Box.createVerticalGlue();
        canalsPanel.add(glue, glueConstraints(0));
        if (this.logger != null) this.logger.debug("ListCanalView initialisée");
    }

    private JPanel createCanalsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(54, 57, 63));
        panel.setOpaque(true);
        return panel;
    }

    private JScrollPane createScrollPane(JPanel content) {
        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(new Color(54, 57, 63));
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private void addScrollPaneToThis() {
        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        );
        this.add(scrollPane, gbc);
    }

    public void addCanal(ICanalView canalView) {
        if (canalView == null) return;

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> addCanal(canalView));
            return;
        }

        int row = canalViews.size();
        canalViews.add(canalView);

        // Retirer uniquement le glue
        canalsPanel.remove(glue);

        // Ajouter le canal au bon gridy
        canalsPanel.add((Component) canalView, canalConstraints(row));

        // Remettre le glue après le dernier élément
        glue = Box.createVerticalGlue();
        canalsPanel.add(glue, glueConstraints(canalViews.size()));

        canalsPanel.revalidate();
        canalsPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            if (bar != null) bar.setValue(bar.getMaximum());
        });

        if (this.logger != null) this.logger.debug("Canal ajouté: " + canalView.getChannel().getName());
    }

    private GridBagConstraints canalConstraints(int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 4, 4, 4);
        return gbc;
    }

    private GridBagConstraints glueConstraints(int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        return gbc;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public void addCanal(Channel canal) {
        boolean isPresent = canalViews.stream().anyMatch(cv -> cv.getChannel().equals(canal));
        if (isPresent) {
            if (this.logger != null) this.logger.warn("Canal déjà présent: " + canal);
        } else {
            addCanal(new CanalView(logger, canal));
            if (this.logger != null) this.logger.debug("Canal ajouté à la vue: " + canal);
        }
    }

    @Override
    public void removeCanal(Channel canal) {
        Optional<ICanalView> opt = canalViews.stream().filter(cv -> cv.getChannel().equals(canal)).findFirst();
        if (opt.isPresent()) {
            ICanalView found = opt.get();
            this.canalViews.remove(found);
            if (logger != null) logger.debug("Canal supprimé de la vue: " + canal);
            // update UI
            removeCanalUI();
        } else {
            if (logger != null) logger.debug("Canal non trouvé dans la vue, pas supprimé: " + canal);
        }
    }

    @Override
    public void updateCanal(Channel canal) {
        Optional<ICanalView> opt = canalViews.stream().filter(cv -> cv.getChannel().equals(canal)).findFirst();
        if (opt.isPresent()) {
            ICanalView iCanalView = opt.get();
            // update model inside the view and refresh UI
            updateCanalUI(iCanalView, canal);
            if (logger != null) logger.debug("Canal mis à jour dans la vue: " + canal);
        } else {
            if (logger != null) logger.debug("Canal non trouvé pour mise à jour dans la vue: " + canal);
        }
    }

    // --- Helpers UI ---

    private void rebuildCanalsPanel() {
        canalsPanel.removeAll();
        int row = 0;
        for (ICanalView cv : canalViews) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = row++;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.weightx = 1.0;
            gbc.weighty = 0.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(4, 4, 4, 4);
            canalsPanel.add((Component) cv, gbc);
        }
        // add glue
        glue = Box.createVerticalGlue();
        canalsPanel.add(glue, glueConstraints(row));
        canalsPanel.revalidate();
        canalsPanel.repaint();
    }

    private void removeCanalUI() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::removeCanalUI);
            return;
        }
        rebuildCanalsPanel();
    }

    private void updateCanalUI(ICanalView view, Channel channel) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> updateCanalUI(view, channel));
            return;
        }
        view.updateChannel(channel);
        canalsPanel.revalidate();
        canalsPanel.repaint();
    }
}
