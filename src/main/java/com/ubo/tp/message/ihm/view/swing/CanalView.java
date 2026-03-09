package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphiccontroller.service.IListCanalGraphicController.ChannelEditCallback;
import com.ubo.tp.message.ihm.view.service.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Supplier;

public class CanalView extends JComponent implements View {

    private static final Color BG_NORMAL  = new Color(54,  57,  63);
    private static final Color BG_HOVER   = new Color(72,  76,  84);
    private static final Color BORDER_HOVER = new Color(90, 95, 105);
    private static final Color PUBLIC_CLR  = new Color(55, 205, 0);
    private static final Color PRIVATE_CLR = new Color(250, 166, 26);

    private final ViewContext viewContext;
    private JLabel prefixLabel;
    private JLabel canalNameLabel;
    private JLabel visibilityLabel;
    private JLabel editBtnLabel;
    private Channel channel;
    private boolean hovered = false;
    private final boolean isOwner;
    private int unreadCount = 0;

    public CanalView(ViewContext viewContext, Channel channel,
                     ChannelEditCallback onEdit, boolean isOwner, Supplier<List<User>> allUsersSupplier) {
        this.viewContext = viewContext;
        this.channel     = channel;
        this.isOwner     = isOwner;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        this.setOpaque(false);

        createPrefixLabel();
        createNameLabel();
        createVisibilityLabel();
        if (channel.isPrivate() && onEdit != null) {
            createEditButton(onEdit, allUsersSupplier);
        }

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                if (editBtnLabel != null) editBtnLabel.setVisible(true);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Point p = e.getPoint();
                if (contains(p)) return;
                hovered = false;
                setCursor(Cursor.getDefaultCursor());
                if (editBtnLabel != null) editBtnLabel.setVisible(false);
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
        visibilityLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        visibilityLabel.setForeground(priv ? PRIVATE_CLR : PUBLIC_CLR);
        visibilityLabel.setOpaque(false);
        this.add(visibilityLabel, new GridBagConstraints(
                2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
    }

    private void createEditButton(ChannelEditCallback onEdit, Supplier<List<User>> allUsersSupplier) {
        editBtnLabel = new JLabel("\u270F");
        editBtnLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        editBtnLabel.setForeground(new Color(180, 180, 200));
        editBtnLabel.setOpaque(false);
        editBtnLabel.setVisible(false);
        editBtnLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        editBtnLabel.setToolTipText("Options du canal");
        editBtnLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));

        editBtnLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { editBtnLabel.setVisible(true); }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                e.consume();
                // Evaluer la liste fraîche au moment du clic
                showEditPopup(onEdit, allUsersSupplier.get(), e.getComponent(), e.getX(), e.getY());
            }
        });

        this.add(editBtnLabel, new GridBagConstraints(
                3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 4, 0, 0), 0, 0));
    }

    private void showEditPopup(ChannelEditCallback onEdit, List<User> allUsers,
                               Component invoker, int x, int y) {
        JPopupMenu popup = new JPopupMenu();
        stylePopup(popup);

        // ── Quitter / Supprimer ──────────────────────────────────────────
        if (isOwner) {
            JMenuItem deleteItem = new JMenuItem("🗑  Supprimer le canal");
            deleteItem.setForeground(new Color(240, 71, 71));
            styleMenuItem(deleteItem);
            deleteItem.addActionListener(ev -> {
                int confirm = JOptionPane.showConfirmDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Supprimer définitivement le canal « " + channel.getName() + " » ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) onEdit.onDelete(channel);
            });
            popup.add(deleteItem);
        } else {
            JMenuItem leaveItem = new JMenuItem("🚪  Quitter le canal");
            leaveItem.setForeground(new Color(220, 150, 80));
            styleMenuItem(leaveItem);
            leaveItem.addActionListener(ev -> onEdit.onLeave(channel));
            popup.add(leaveItem);
        }

        // ── Gestion des membres (propriétaire seulement) ─────────────────
        if (isOwner) {
            popup.addSeparator();

            // Ajouter un membre
            List<User> currentMembers = channel.getUsers();
            List<User> addable = allUsers == null ? java.util.Collections.emptyList() :
                    allUsers.stream().filter(u -> !currentMembers.contains(u)).toList();

            JMenu addMenu = new JMenu("➕  Ajouter un membre");
            styleMenuItem(addMenu);
            if (addable.isEmpty()) {
                JMenuItem none = new JMenuItem("(aucun utilisateur disponible)");
                none.setEnabled(false);
                addMenu.add(none);
            } else {
                for (User u : addable) {
                    JMenuItem item = new JMenuItem(u.getName() + " (@" + u.getUserTag() + ")");
                    styleMenuItem(item);
                    item.addActionListener(ev -> onEdit.onAddUser(channel, u));
                    addMenu.add(item);
                }
            }
            popup.add(addMenu);

            // Retirer un membre
            JMenu removeMenu = new JMenu("➖  Retirer un membre");
            styleMenuItem(removeMenu);
            if (currentMembers.isEmpty()) {
                JMenuItem none = new JMenuItem("(aucun membre)");
                none.setEnabled(false);
                removeMenu.add(none);
            } else {
                for (User u : currentMembers) {
                    JMenuItem item = new JMenuItem(u.getName() + " (@" + u.getUserTag() + ")");
                    styleMenuItem(item);
                    item.addActionListener(ev -> onEdit.onRemoveUser(channel, u));
                    removeMenu.add(item);
                }
            }
            popup.add(removeMenu);
        }

        popup.show(invoker, x, y);
    }

    private void stylePopup(JPopupMenu popup) {
        popup.setBackground(new Color(40, 43, 48));
        popup.setBorder(BorderFactory.createLineBorder(new Color(70, 73, 80), 1));
    }

    private void styleMenuItem(JMenuItem item) {
        item.setBackground(new Color(40, 43, 48));
        if (item.getForeground() == null || item.getForeground().equals(Color.BLACK))
            item.setForeground(new Color(220, 221, 222));
        item.setOpaque(true);
        item.setFont(new Font("SansSerif", Font.PLAIN, 12));
    }

    private void styleMenuItem(JMenu menu) {
        menu.setBackground(new Color(40, 43, 48));
        menu.setForeground(new Color(220, 221, 222));
        menu.setOpaque(true);
        menu.setFont(new Font("SansSerif", Font.PLAIN, 12));
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
            int diameter = Math.max(tw + 8, 16);
            int bx = getWidth() - diameter - 4;
            int by = (getHeight() - diameter) / 2;
            g2.setColor(new Color(240, 71, 71));
            g2.fillOval(bx, by, diameter, diameter);
            g2.setColor(Color.WHITE);
            g2.drawString(text, bx + (diameter - tw) / 2, by + fm.getAscent() + (diameter - fm.getHeight()) / 2);
        }

        g2.dispose();
    }

    /** Incrémente le compteur de messages non lus et redessine. */
    public void incrementUnread() {
        unreadCount++;
        repaint();
    }

    /** Remet le compteur à zéro et redessine. */
    public void clearUnread() {
        unreadCount = 0;
        repaint();
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
