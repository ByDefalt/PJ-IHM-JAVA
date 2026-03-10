package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UserView extends JComponent implements View {

    private static final Color BG_NORMAL = new Color(54, 57, 63);
    private static final Color BG_HOVER = new Color(72, 76, 84);
    private static final Color BORDER_HOVER = new Color(90, 95, 105);
    private static final Color ONLINE_CLR = new Color(35, 165, 90);
    private static final Color OFFLINE_CLR = new Color(116, 127, 141);

    private final ViewContext viewContext;
    private JLabel userNameLabel;
    private JLabel tagLabel;
    private JLabel statusDotLabel;
    private User user;
    private boolean hovered = false;
    private int unreadCount = 0;

    public UserView(ViewContext viewContext, User user) {
        this.viewContext = viewContext;
        this.user = user;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        this.setOpaque(false);

        createStatusDot();
        createNameLabel();
        createTagLabel();

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (contains(e.getPoint())) return; // souris encore dans le composant (entre dans un enfant)
                hovered = false;
                setCursor(Cursor.getDefaultCursor());
                repaint();
            }
        });

        if (viewContext.logger() != null)
            viewContext.logger().debug("UserView initialisée pour: " + user.getName());
    }

    private void createStatusDot() {
        statusDotLabel = new JLabel() {
            @Override
            public boolean contains(int x, int y) {
                return false;
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = user.isOnline() ? ONLINE_CLR : OFFLINE_CLR;
                int d = 10;
                int px = (getWidth() - d) / 2;
                int py = (getHeight() - d) / 2;
                if (user.isOnline()) {
                    g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 60));
                    g2.fillOval(px - 2, py - 2, d + 4, d + 4);
                }
                g2.setColor(c);
                g2.fillOval(px, py, d, d);
                g2.dispose();
            }
        };
        statusDotLabel.setPreferredSize(new Dimension(14, 14));
        statusDotLabel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, 1, 2, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 6), 0, 0);
        this.add(statusDotLabel, gbc);
    }

    private void createNameLabel() {
        Font nameFont = new Font("SansSerif", Font.BOLD, 13);
        userNameLabel = new JLabel(user.getName() != null ? user.getName() : "") {
            @Override
            public boolean contains(int x, int y) {
                return false;
            }
        };
        userNameLabel.setFont(nameFont);
        userNameLabel.setForeground(new Color(220, 221, 222));
        userNameLabel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints(
                1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 0, 1, 0), 0, 0);
        this.add(userNameLabel, gbc);
    }

    private void createTagLabel() {
        tagLabel = new JLabel("@" + user.getUserTag()) {
            @Override
            public boolean contains(int x, int y) {
                return false;
            }
        };
        tagLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        tagLabel.setForeground(new Color(114, 118, 125));
        tagLabel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints(
                1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 2, 0), 0, 0);
        this.add(tagLabel, gbc);
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

        // Badge messages non lus
        if (unreadCount > 0) {
            String text = unreadCount > 99 ? "99+" : String.valueOf(unreadCount);
            g2.setFont(new Font("SansSerif", Font.BOLD, 9));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(text);
            int diameter = Math.max(tw + 8, 14);
            int by = (getHeight() - diameter) / 2;
            int bx = getWidth() - diameter - 6;
            g2.setColor(new Color(240, 71, 71));
            g2.fillOval(bx, by, diameter, diameter);
            g2.setColor(Color.WHITE);
            g2.drawString(text, bx + (diameter - tw) / 2, by + fm.getAscent() + (diameter - fm.getHeight()) / 2);
        }

        g2.dispose();
    }

    public User getUser() {
        return user;
    }

    public void updateUser(User updated) {
        this.user = updated;
        userNameLabel.setText(updated.getName() != null ? updated.getName() : "");
        tagLabel.setText("@" + updated.getUserTag());
        statusDotLabel.repaint();
        this.revalidate();
        this.repaint();
    }

    public void incrementUnread() {
        unreadCount++;
        revalidate();
        repaint();
    }

    public void clearUnread() {
        unreadCount = 0;
        revalidate();
        repaint();
    }
}