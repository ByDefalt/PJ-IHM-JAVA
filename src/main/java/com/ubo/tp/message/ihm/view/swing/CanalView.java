package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class CanalView extends JComponent implements View {

    private static final Color BG_NORMAL    = new Color(54, 57, 63);
    private static final Color BG_HOVER     = new Color(72, 76, 84);
    private static final Color BORDER_HOVER = new Color(90, 95, 105);
    private static final Color PUBLIC_CLR   = new Color(88, 101, 242);
    private static final Color PRIVATE_CLR  = new Color(250, 166, 26);
    private static final Color LEAVE_CLR    = new Color(240, 71, 71);

    private final ViewContext viewContext;
    private JLabel prefixLabel;
    private JLabel canalNameLabel;
    private JLabel visibilityLabel;
    private JLabel leaveBtnLabel;
    private Channel channel;
    private boolean hovered = false;
    private boolean isOwner = false;

    public CanalView(ViewContext viewContext, Channel channel, Consumer<Channel> onLeave, boolean isOwner) {
        this.viewContext = viewContext;
        this.channel = channel;
        this.isOwner = isOwner;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        this.setOpaque(false);

        createPrefixLabel();
        createNameLabel();
        createVisibilityLabel();
        if (channel.isPrivate() && onLeave != null) {
            createLeaveButton(onLeave);
        }

        this.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                hovered = true;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                if (leaveBtnLabel != null) leaveBtnLabel.setVisible(true);
                repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                // Vérifier que la souris est vraiment sortie du composant
                // (Swing génère mouseExited quand on entre dans un enfant)
                Point p = e.getPoint();
                if (contains(p)) return;
                hovered = false;
                setCursor(Cursor.getDefaultCursor());
                if (leaveBtnLabel != null) leaveBtnLabel.setVisible(false);
                repaint();
            }
        });

        if (viewContext.logger() != null)
            viewContext.logger().debug("CanalView initialisée pour: " + channel.getName());
    }

    private void createPrefixLabel() {
        boolean priv = channel.isPrivate();
        prefixLabel = new JLabel(priv ? "🔒" : "#") {
            @Override public boolean contains(int x, int y) { return false; }
        };
        prefixLabel.setFont(new Font("SansSerif", Font.BOLD, priv ? 11 : 14));
        prefixLabel.setForeground(priv ? PRIVATE_CLR : PUBLIC_CLR);
        prefixLabel.setOpaque(false);
        this.add(prefixLabel, new GridBagConstraints(
                0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 5), 0, 0));
    }

    private void createNameLabel() {
        canalNameLabel = new JLabel(channel.getName() != null ? channel.getName() : "") {
            @Override public boolean contains(int x, int y) { return false; }
        };
        canalNameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        canalNameLabel.setForeground(new Color(220, 221, 222));
        canalNameLabel.setOpaque(false);
        this.add(canalNameLabel, new GridBagConstraints(
                1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 4), 0, 0));
    }

    private void createVisibilityLabel() {
        boolean priv = channel.isPrivate();
        visibilityLabel = new JLabel(priv ? "privé" : "public") {
            @Override public boolean contains(int x, int y) { return false; }
        };
        visibilityLabel.setFont(new Font("SansSerif", Font.PLAIN, 9));
        visibilityLabel.setForeground(priv ? PRIVATE_CLR : PUBLIC_CLR);
        visibilityLabel.setOpaque(false);
        this.add(visibilityLabel, new GridBagConstraints(
                2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
    }

    private void createLeaveButton(Consumer<Channel> onLeave) {
        leaveBtnLabel = new JLabel("✕");
        leaveBtnLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        // Rouge vif pour suppression (propriétaire), rouge doux pour quitter (membre)
        leaveBtnLabel.setForeground(isOwner ? LEAVE_CLR : new Color(210, 110, 110));
        leaveBtnLabel.setOpaque(false);
        leaveBtnLabel.setVisible(false);
        leaveBtnLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        leaveBtnLabel.setToolTipText(isOwner ? "Supprimer le canal" : "Quitter le canal");
        leaveBtnLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        leaveBtnLabel.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { leaveBtnLabel.setVisible(true); }
            @Override public void mouseClicked(MouseEvent e) {
                e.consume();
                onLeave.accept(channel);
            }
        });
        this.add(leaveBtnLabel, new GridBagConstraints(
                3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 4, 0, 0), 0, 0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc = 12, pad = 2;
        int w = getWidth() - pad * 2, h = getHeight() - pad * 2;
        if (hovered) {
            g2.setColor(BG_HOVER);
            g2.fillRoundRect(pad, pad, w, h, arc, arc);
            g2.setStroke(new BasicStroke(1.2f));
            g2.setColor(BORDER_HOVER);
            g2.drawRoundRect(pad, pad, w - 1, h - 1, arc, arc);
        } else {
            g2.setColor(BG_NORMAL);
            g2.fillRoundRect(pad, pad, w, h, arc, arc);
        }
        g2.dispose();
    }

    public Channel getChannel() { return channel; }

    public void updateChannel(Channel updated) {
        String oldName = this.channel != null ? this.channel.getName() : "<null>";
        this.channel = updated;
        boolean priv = updated.isPrivate();
        canalNameLabel.setText(updated.getName() != null ? updated.getName() : "");
        prefixLabel.setText(priv ? "🔒" : "#");
        prefixLabel.setForeground(priv ? PRIVATE_CLR : PUBLIC_CLR);
        visibilityLabel.setText(priv ? "privé" : "public");
        visibilityLabel.setForeground(priv ? PRIVATE_CLR : PUBLIC_CLR);
        if (viewContext.logger() != null)
            viewContext.logger().debug("CanalView.updateChannel : '" + oldName + "' -> '" + updated.getName() + "'");
        this.revalidate();
        this.repaint();
    }
}
