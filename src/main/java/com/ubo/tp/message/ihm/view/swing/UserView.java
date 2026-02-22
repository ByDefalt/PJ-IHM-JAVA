package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UserView extends JComponent implements View {

    private final ViewContext viewContext;

    private JLabel userNameLabel;
    private JLabel statusLabel;
    private User user;
    private boolean hovered = false;

    private static final Color BG_NORMAL    = new Color(54, 57, 63);
    private static final Color BG_HOVER     = new Color(72, 76, 84);
    private static final Color BORDER_HOVER = new Color(90, 95, 105);

    public UserView(ViewContext viewContext, User user) {
        this.viewContext = viewContext;
        this.user = user;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        // Non opaque : on peint tout manuellement, les coins hors du
        // rectangle arrondi restent transparents.
        this.setOpaque(false);

        createNameLabel();
        createStatusLabel();

        // Un seul listener : les labels ont contains() = false donc
        // ils ne volent pas les événements souris.
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
            viewContext.logger().debug("UserView initialisée pour: " + user.getName());
    }

    private void createNameLabel() {
        Font baseFont = UIManager.getFont("Label.font");
        Font nameFont = (baseFont != null) ? baseFont.deriveFont(Font.BOLD, 13f)
                : new Font("SansSerif", Font.BOLD, 13);

        Color nameColor = UIManager.getColor("Label.foreground");
        if (nameColor == null) nameColor = new Color(220, 221, 222);

        userNameLabel = new JLabel(user.getName() != null ? user.getName() : "") {
            @Override
            public boolean contains(int x, int y) {
                return false;
            }
        };
        userNameLabel.setFont(nameFont);
        userNameLabel.setForeground(nameColor);
        userNameLabel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(4, 0, 2, 2), 0, 0
        );
        this.add(userNameLabel, gbc);
    }

    private void createStatusLabel() {
        Font baseFont = UIManager.getFont("Label.font");
        Font statusFont = (baseFont != null) ? baseFont.deriveFont(Font.PLAIN, 11f)
                : new Font("SansSerif", Font.PLAIN, 11);

        Color statusColor = UIManager.getColor("Label.disabledForeground");
        if (statusColor == null) statusColor = new Color(114, 118, 125);

        statusLabel = new JLabel("") {
            @Override
            public boolean contains(int x, int y) {
                return false;
            }
        };
        statusLabel.setFont(statusFont);
        statusLabel.setForeground(statusColor);
        statusLabel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints(
                0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 4, 2), 0, 0
        );
        this.add(statusLabel, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 12;
        int pad = 2;
        int w = getWidth()  - pad * 2;
        int h = getHeight() - pad * 2;

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

    public User getUser() {
        return user;
    }

    private void setUser(User user) {
        this.user = user;
        userNameLabel.setText(user.getName() != null ? user.getName() : "");
    }

    public void updateUser(User user) {
        this.setUser(user);
        this.revalidate();
        this.repaint();
    }
}