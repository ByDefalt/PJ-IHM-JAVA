package com.ubo.tp.message.ihm.view;

import com.ubo.tp.message.ihm.service.ICanalView;
import com.ubo.tp.message.ihm.service.View;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;

public class CanalView extends JComponent implements ICanalView {

    private final Logger LOGGER;

    private JLabel canalNameLabel;

    public CanalView(Logger logger, String canalName) {
        this.LOGGER = logger;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        this.setOpaque(false);

        init(canalName);
        if (LOGGER != null) LOGGER.debug("CanalView initialisée pour: " + canalName);
    }

    public CanalView(String canalName) {
        this(null, canalName);
    }

    private void init(String canalName) {
        createNameLabel(canalName);
        createConnector();
    }

    private void createNameLabel(String canalName) {
        canalNameLabel = new JLabel(canalName != null ? canalName : "");
        canalNameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        canalNameLabel.setForeground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(4, 0, 4, 0), 0, 0
        );
        this.add(canalNameLabel, gbc);
    }

    private void createConnector() {
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (LOGGER != null) LOGGER.debug("CanalView cliqué: " + getCanalName());
            }
        });
    }

    // API publique
    public String getCanalName() { return canalNameLabel.getText(); }
    public void setCanalName(String name) { this.canalNameLabel.setText(name); }

    @Override
    public JLabel getCanalLabel() { return canalNameLabel; }

}
