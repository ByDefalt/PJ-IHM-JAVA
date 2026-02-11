package com.ubo.tp.message.ihm.view;

import com.ubo.tp.message.ihm.service.View;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Vue affichant une liste de CanalView empilés verticalement dans une zone défilante.
 * Style cohérent avec `MessageView` / `ListMessageView` / `ListUserView`.
 */
public class ListCanalView extends JComponent implements View {

    private final Logger logger;

    private final JPanel canalsPanel;
    private final JScrollPane scrollPane;
    private final List<CanalView> canalViews = new ArrayList<>();

    public ListCanalView(Logger logger, List<CanalView> initialCanals) {
        this.logger = logger;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(new Color(54, 57, 63));
        this.setOpaque(true);

        canalsPanel = createCanalsPanel();
        scrollPane = createScrollPane(canalsPanel);
        addScrollPaneToThis();

        if (initialCanals != null && !initialCanals.isEmpty()) {
            setCanals(initialCanals);
        } else {
            // ensure glue so content stays at top
            canalsPanel.add(Box.createVerticalGlue(), new GridBagConstraints(
                    0, 0, 1, 1, 1.0, 1.0,
                    GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0
            ));
        }

        if (this.logger != null) this.logger.debug("ListCanalView initialisée");
    }

    public ListCanalView(Logger logger) {
        this(logger, null);
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

    /**
     * Remplace la liste entière de canaux par `newCanals`.
     */
    public void setCanals(List<CanalView> newCanals) {
        clearCanals();
        if (newCanals == null) return;
        for (CanalView cv : newCanals) addCanal(cv);
        revalidate();
        repaint();
    }

    /**
     * Ajoute un CanalView à la fin de la liste et fait défiler vers le bas.
     */
    public void addCanal(CanalView canalView) {
        if (canalView == null) return;

        int row = canalViews.size();
        GridBagConstraints gbc = new GridBagConstraints(
                0, row, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(4, 4, 4, 4), 0, 0
        );
        gbc.fill = GridBagConstraints.HORIZONTAL;

        canalsPanel.add(canalView, gbc);
        canalViews.add(canalView);

        // remove old glue and re-add it after the last element
        for (Component c : canalsPanel.getComponents()) {
            if (c instanceof Box.Filler) canalsPanel.remove(c);
        }

        GridBagConstraints gbcGlue = new GridBagConstraints(
                0, canalViews.size(), 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        );
        canalsPanel.add(Box.createVerticalGlue(), gbcGlue);

        revalidate();
        repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });

        if (this.logger != null) this.logger.debug("Canal ajouté à ListCanalView: " + canalView.getCanalName());
    }

    public void clearCanals() {
        canalsPanel.removeAll();
        canalViews.clear();
        // keep a glue so top alignment works when empty
        GridBagConstraints gbcGlue = new GridBagConstraints(
                0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        );
        canalsPanel.add(Box.createVerticalGlue(), gbcGlue);
        revalidate();
        repaint();
    }

    public List<CanalView> getCanals() { return new ArrayList<>(canalViews); }

}
