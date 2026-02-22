package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

/**
 * Vue affichant une liste de CanalView empilés verticalement dans une zone défilante.
 */
public class ListCanalView extends JComponent implements View {

    private final ViewContext viewContext;

    private final JPanel canalsPanel;
    private final JScrollPane scrollPane;
    // Champ de recherche
    private final JTextField searchField;
    private Component glue;

    public ListCanalView(ViewContext viewContext) {
        this.viewContext = viewContext;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(new Color(54, 57, 63));
        this.setOpaque(true);

        canalsPanel = createCanalsPanel();
        scrollPane = createScrollPane(canalsPanel);

        // create and insert search field
        searchField = createSearchField();

        addScrollPaneToThis();

        glue = Box.createVerticalGlue();
        canalsPanel.add(glue, glueConstraints(0));

        if (this.viewContext.logger() != null) this.viewContext.logger().debug("ListCanalView initialisée");
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

        if (this.viewContext.logger() != null) this.viewContext.logger().debug("CanalView ajoutée (row=" + row + ")");
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

        // réappliquer le filtre courant si nécessaire
        String q = searchField != null ? searchField.getText() : null;
        applyFilter(q);
    }

    public void updateCanalUI(CanalView view, Channel channel) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> updateCanalUI(view, channel));
            return;
        }
        view.updateChannel(channel);
        canalsPanel.revalidate();
        canalsPanel.repaint();
        if (this.viewContext.logger() != null)
            this.viewContext.logger().debug("CanalView mise à jour pour: " + channel);
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

    // Ajoute la barre de recherche en haut, puis le scrollpane en dessous
    private void addScrollPaneToThis() {
        // search field at y=0
        GridBagConstraints gbcSearch = new GridBagConstraints(
                0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 6, 0), 0, 0
        );
        this.add(searchField, gbcSearch);

        // scroll pane at y=1 (takes remaining space)
        GridBagConstraints gbc = new GridBagConstraints(
                0, 1, 1, 1, 1.0, 1.0,
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

    // Création du champ de recherche et du DocumentListener
    private JTextField createSearchField() {
        JTextField tf = new JTextField();
        tf.setColumns(15);
        tf.setOpaque(true);
        tf.setBackground(new Color(47, 49, 54));
        tf.setForeground(new Color(220, 221, 222));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 34, 37)),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        tf.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter(tf.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter(tf.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter(tf.getText());
            }
        });
        return tf;
    }

    // Applique le filtre côté client en masquant/montrant les CanalView
    private void applyFilter(String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase();
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> applyFilter(query));
            return;
        }
        for (Component c : canalsPanel.getComponents()) {
            if (c instanceof CanalView) {
                boolean match = matches((CanalView) c, q);
                c.setVisible(match);
            }
        }
        canalsPanel.revalidate();
        canalsPanel.repaint();
    }

    private boolean matches(CanalView cv, String q) {
        if (q == null || q.isEmpty()) return true;
        Channel ch = cv.getChannel();
        if (ch == null) return false;
        String name = ch.getName() != null ? ch.getName().toLowerCase() : "";
        return name.contains(q);
    }
}
