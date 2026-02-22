package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CanalView extends JComponent implements View {

    private final ViewContext viewContext;

    private JLabel canalNameLabel;
    private Channel channel;
    private boolean hovered = false;

    // Couleur de fond normale (doit correspondre à celle du parent ListCanalView)
    private static final Color BG_NORMAL  = new Color(54, 57, 63);
    // Couleur légèrement plus claire au survol
    private static final Color BG_HOVER   = new Color(72, 76, 84);
    // Bordure subtile au survol
    private static final Color BORDER_HOVER = new Color(90, 95, 105);

    public CanalView(ViewContext viewContext, Channel channel) {
        this.viewContext = viewContext;
        this.channel = channel;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        // NON opaque : on peint tout manuellement dans paintComponent,
        // les coins hors du rectangle arrondi seront transparents
        // et laisseront voir le fond du parent.
        this.setOpaque(false);

        createNameLabel();

        // Un seul listener sur CanalView.
        // Le label a contains() = false donc il ne "vole" plus les événements souris :
        // Swing route tout directement ici.
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                setCursor(Cursor.getDefaultCursor());
                repaint();
            }
        });

        if (viewContext.logger() != null)
            viewContext.logger().debug("CanalView initialisée pour: " + channel.getName());
    }

    private void createNameLabel() {
        Font baseFont = UIManager.getFont("Label.font");
        Font labelFont = (baseFont != null) ? baseFont.deriveFont(Font.BOLD, 13f)
                : new Font("SansSerif", Font.BOLD, 13);

        Color labelColor = UIManager.getColor("Label.foreground");
        if (labelColor == null) labelColor = new Color(220, 221, 222);

        // contains() = false : le label est invisible pour le dispatcher de souris Swing.
        // Les événements remontent au CanalView parent sans être interceptés.
        canalNameLabel = new JLabel(channel.getName() != null ? channel.getName() : "") {
            @Override
            public boolean contains(int x, int y) {
                return false;
            }
        };
        canalNameLabel.setFont(labelFont);
        canalNameLabel.setForeground(labelColor);
        canalNameLabel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(4, 0, 4, 0), 0, 0
        );
        this.add(canalNameLabel, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Pas d'appel à super.paintComponent() car setOpaque(false) :
        // on gère entièrement le fond ici.
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 12;
        int pad = 2;
        int w = getWidth()  - pad * 2;
        int h = getHeight() - pad * 2;

        if (hovered) {
            // Fond légèrement plus clair + bordure subtile
            g2.setColor(BG_HOVER);
            g2.fillRoundRect(pad, pad, w, h, arc, arc);

            g2.setStroke(new BasicStroke(1.2f));
            g2.setColor(BORDER_HOVER);
            g2.drawRoundRect(pad, pad, w - 1, h - 1, arc, arc);
        } else {
            // Fond normal : rectangle arrondi de la même couleur que le parent
            // → visuellement identique au parent, sans coins parasites
            g2.setColor(BG_NORMAL);
            g2.fillRoundRect(pad, pad, w, h, arc, arc);
        }

        g2.dispose();
    }

    public Channel getChannel() {
        return channel;
    }

    public void updateChannel(Channel channel) {
        String oldName = this.channel != null ? this.channel.getName() : "<null>";
        this.channel = channel;
        canalNameLabel.setText(channel.getName() != null ? channel.getName() : "");
        if (viewContext.logger() != null)
            viewContext.logger().debug("CanalView.updateChannel : '" + oldName + "' -> '" + channel.getName() + "'");
        this.revalidate();
        this.repaint();
    }
}