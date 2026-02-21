package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Vue affichant une liste de CanalView empilés verticalement dans une zone défilante.
 */
public class ListCanalView extends JComponent implements View {

    private final Logger logger;

    private final JPanel canalsPanel;
    private final JScrollPane scrollPane;

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

        glue = Box.createVerticalGlue();
        canalsPanel.add(glue, glueConstraints(0));

        if (this.logger != null) this.logger.debug("ListCanalView initialisée");
    }

    public void addCanalUI(CanalView canalView, int row) {
        if (canalView == null) return;

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> addCanalUI(canalView, row));
            return;
        }

        canalsPanel.remove(glue);
        canalsPanel.add(canalView, canalConstraints(row));

        glue = Box.createVerticalGlue();
        canalsPanel.add(glue, glueConstraints(row + 1));

        canalsPanel.revalidate();
        canalsPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            if (bar != null) bar.setValue(bar.getMaximum());
        });

        if (this.logger != null) this.logger.debug("CanalView ajoutée (row=" + row + ")");
    }

    public void rebuildUI(List<CanalView> ordered) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> rebuildUI(ordered));
            return;
        }
        canalsPanel.removeAll();
        int row = 0;
        for (CanalView cv : ordered) {
            canalsPanel.add(cv, canalConstraints(row++));
        }
        glue = Box.createVerticalGlue();
        canalsPanel.add(glue, glueConstraints(row));
        canalsPanel.revalidate();
        canalsPanel.repaint();
    }

    public void updateCanalUI(CanalView view, Channel channel) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> updateCanalUI(view, channel));
            return;
        }
        view.updateChannel(channel);
        canalsPanel.revalidate();
        canalsPanel.repaint();
        if (this.logger != null) this.logger.debug("CanalView mise à jour pour: " + channel);
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
}
