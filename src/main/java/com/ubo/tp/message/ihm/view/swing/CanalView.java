package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.ihm.view.contexte.ViewContext;

import javax.swing.*;
import java.awt.*;

public class CanalView extends JComponent implements View {

    private final ViewContext viewContext;

    private JLabel canalNameLabel;
    private Channel channel;

    public CanalView(ViewContext viewContext, Channel channel) {
        this.viewContext = viewContext;
        this.channel = channel;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        this.setOpaque(true);

        createNameLabel();

        if (viewContext.logger() != null) viewContext.logger().debug("CanalView initialisée pour: " + channel.getName());
    }

    private void createNameLabel() {
        Font baseFont = UIManager.getFont("Label.font");
        Font labelFont = (baseFont != null) ? baseFont.deriveFont(Font.BOLD, 13f)
                : new Font("SansSerif", Font.BOLD, 13);

        Color labelColor = UIManager.getColor("Label.foreground");
        if (labelColor == null) labelColor = new Color(220, 221, 222);

        canalNameLabel = new JLabel(channel.getName() != null ? channel.getName() : "");
        canalNameLabel.setFont(labelFont);
        canalNameLabel.setForeground(labelColor);

        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(4, 0, 4, 0), 0, 0
        );
        this.add(canalNameLabel, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    public Channel getChannel() {
        return channel;
    }

    public void updateChannel(Channel channel) {
        String oldName = this.channel != null ? this.channel.getName() : "<null>";
        this.channel = channel;
        canalNameLabel.setText(channel.getName() != null ? channel.getName() : "");
        if (viewContext.logger() != null) viewContext.logger().debug("CanalView.updateChannel : '" + oldName + "' -> '" + channel.getName() + "'");
        this.revalidate();
        this.repaint();
    }
}