package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.ihm.service.ICanalView;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;

public class CanalView extends JComponent implements ICanalView {

    private final Logger LOGGER;

    private JLabel canalNameLabel;

    private Channel channel;

    public CanalView(Logger logger, Channel channel) {
        this.LOGGER = logger;
        this.channel = channel;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        this.setOpaque(false);

        init();
        if (LOGGER != null) LOGGER.debug("CanalView initialisée pour: " + channel.getName());
    }

    private void init() {

        setOpaque(true);
        createNameLabel();
        createConnector();
    }

    private void createNameLabel() {
        canalNameLabel = new JLabel(channel.getName() != null ? channel.getName() : "");
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
                if (LOGGER != null) LOGGER.debug("CanalView cliqué: " + channel.getName());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    private void setChannel(Channel channel) {
        String oldName = this.channel != null ? this.channel.getName() : "<null>";
        this.channel = channel;
        canalNameLabel.setText(channel.getName() != null ? channel.getName() : "");
        if (LOGGER != null) LOGGER.debug("CanalView.setChannel : '" + oldName + "' -> '" + channel.getName() + "'");
        //TODO: le reste des infos du canal (description, nombre de membres, etc.)
        // Forcer rafraîchissement visuel
        this.revalidate();
        this.repaint();
    }

    @Override
    public void updateChannel(Channel channel) {
        this.setChannel(channel);
    }
}
