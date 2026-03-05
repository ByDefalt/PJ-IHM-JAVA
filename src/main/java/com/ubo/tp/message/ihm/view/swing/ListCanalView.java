package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphicController.service.IListCanalGraphicController.ChannelCreationCallback;
import com.ubo.tp.message.ihm.view.service.View;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Vue affichant une liste de CanalView empilés verticalement dans une zone défilante.
 */
public class ListCanalView extends JComponent implements View {

    private static final Color BG       = new Color(54, 57, 63);
    private static final Color BG_INPUT = new Color(64, 68, 75);
    private static final Color BG_ITEM  = new Color(47, 49, 54);
    private static final Color ACCENT   = new Color(88, 101, 242);
    private static final Color MUTED    = new Color(185, 187, 190);

    private final ViewContext viewContext;
    private final JPanel canalsPanel;
    private final JScrollPane scrollPane;
    private final JTextField searchField;
    private Component glue;

    /** Callback déclenché à la confirmation du formulaire de création de canal. */
    private ChannelCreationCallback onNewChannelConfirm;
    /** Utilisateurs disponibles (sans l'utilisateur connecté). */
    private List<User> availableUsers = new ArrayList<>();

    public ListCanalView(ViewContext viewContext) {
        this.viewContext = viewContext;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(BG);
        this.setOpaque(true);

        canalsPanel = createCanalsPanel();
        scrollPane  = createScrollPane(canalsPanel);
        searchField = createSearchField();

        addScrollPaneToThis();

        glue = Box.createVerticalGlue();
        canalsPanel.add(glue, glueConstraints(0));

        // ── Menu contextuel clic droit ──
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem newChannelItem = new JMenuItem("✦  Nouveau canal");
        newChannelItem.setFont(new Font("Arial", Font.PLAIN, 13));
        newChannelItem.addActionListener(e -> showNewChannelDialog());
        contextMenu.add(newChannelItem);

        MouseAdapter rightClick = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e)  { if (e.isPopupTrigger()) contextMenu.show(e.getComponent(), e.getX(), e.getY()); }
            @Override public void mouseReleased(MouseEvent e) { if (e.isPopupTrigger()) contextMenu.show(e.getComponent(), e.getX(), e.getY()); }
        };
        this.addMouseListener(rightClick);
        canalsPanel.addMouseListener(rightClick);
        scrollPane.addMouseListener(rightClick);
        scrollPane.getViewport().addMouseListener(rightClick);

        if (this.viewContext.logger() != null) this.viewContext.logger().debug("ListCanalView initialisée");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // API formulaire
    // ─────────────────────────────────────────────────────────────────────────

    public void setOnNewChannelConfirm(ChannelCreationCallback onConfirm) {
        this.onNewChannelConfirm = onConfirm;
    }

    public void setAvailableUsers(List<User> users) {
        this.availableUsers = users != null ? new ArrayList<>(users) : new ArrayList<>();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Dialog de création de canal
    // ─────────────────────────────────────────────────────────────────────────

    private void showNewChannelDialog() {
        Window owner = SwingUtilities.getWindowAncestor(this);

        // ── État sélection ──
        Set<User> selectedUsers = new LinkedHashSet<>();

        // ── Dialog ──
        JDialog dialog;
        if (owner instanceof Frame)       dialog = new JDialog((Frame)  owner, "Créer un canal", true);
        else if (owner instanceof Dialog) dialog = new JDialog((Dialog) owner, "Créer un canal", true);
        else                              dialog = new JDialog((Frame)   null,  "Créer un canal", true);

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().setBackground(BG);
        dialog.getContentPane().setLayout(new BorderLayout(0, 0));

        // ── Titre ──
        JLabel titleLabel = new JLabel("Créer un canal");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        dialog.getContentPane().add(titleLabel, BorderLayout.NORTH);

        // ── Corps ──
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(0, 20, 8, 20));

        // -- Nom --
        body.add(miniLabel("NOM DU CANAL"));
        body.add(Box.createVerticalStrut(4));

        JTextField nameField = styledTextField(20);
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(nameField);
        body.add(Box.createVerticalStrut(4));

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(240, 71, 71));
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(errorLabel);
        body.add(Box.createVerticalStrut(10));

        // -- Privé --
        JCheckBox privateCheck = new JCheckBox("Canal privé");
        privateCheck.setBackground(BG);
        privateCheck.setForeground(new Color(220, 221, 222));
        privateCheck.setFont(new Font("Arial", Font.PLAIN, 13));
        privateCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(privateCheck);
        body.add(Box.createVerticalStrut(14));

        // -- Membres --
        if (!availableUsers.isEmpty()) {
            body.add(miniLabel("INVITER DES MEMBRES"));
            body.add(Box.createVerticalStrut(6));

            // Barre de recherche
            JTextField userSearchField = styledTextField(20);
            userSearchField.setToolTipText("Rechercher un utilisateur…");
            userSearchField.setAlignmentX(Component.LEFT_ALIGNMENT);
            body.add(userSearchField);
            body.add(Box.createVerticalStrut(6));

            // Panel chips (sélectionnés)
            JPanel chipsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
            chipsPanel.setBackground(BG);
            chipsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            body.add(chipsPanel);

            // Liste des users
            JPanel userListPanel = new JPanel();
            userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));
            userListPanel.setBackground(BG);

            JScrollPane userScroll = new JScrollPane(userListPanel);
            userScroll.setPreferredSize(new Dimension(380, 160));
            userScroll.setBorder(BorderFactory.createLineBorder(new Color(32, 34, 37)));
            userScroll.getViewport().setBackground(BG);
            userScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
            body.add(userScroll);

            // Méthode de reconstruction de la liste
            Runnable[] rebuildRef = new Runnable[1];
            rebuildRef[0] = () -> {
                String q = userSearchField.getText().trim().toLowerCase();
                userListPanel.removeAll();
                for (User u : availableUsers) {
                    String display = (u.getName() != null ? u.getName() : "") + " @" + u.getUserTag();
                    if (!q.isEmpty() && !display.toLowerCase().contains(q)) continue;

                    boolean sel = selectedUsers.contains(u);
                    JPanel row = buildUserRow(u, sel, selectedUsers, chipsPanel, rebuildRef, dialog);
                    userListPanel.add(row);
                }
                userListPanel.revalidate();
                userListPanel.repaint();
            };

            rebuildRef[0].run();

            userSearchField.getDocument().addDocumentListener(new DocumentListener() {
                @Override public void insertUpdate(DocumentEvent e)  { rebuildRef[0].run(); }
                @Override public void removeUpdate(DocumentEvent e)  { rebuildRef[0].run(); }
                @Override public void changedUpdate(DocumentEvent e) { rebuildRef[0].run(); }
            });
        }

        dialog.getContentPane().add(body, BorderLayout.CENTER);

        // ── Boutons ──
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        btns.setBackground(BG);

        JButton cancelBtn = accentButton("Annuler", new Color(78, 80, 88), MUTED);
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton createBtn = accentButton("Créer le canal", ACCENT, Color.WHITE);
        createBtn.setFont(new Font("Arial", Font.BOLD, 13));
        createBtn.addActionListener(e -> {
            String raw = nameField.getText().trim().toLowerCase().replaceAll("\\s+", "-");
            if (raw.isEmpty()) { errorLabel.setText("Le nom ne peut pas être vide."); return; }
            if (!raw.matches("[a-z0-9\\-_]+")) { errorLabel.setText("Lettres, chiffres, tirets, underscores uniquement."); return; }
            if (onNewChannelConfirm != null) {
                onNewChannelConfirm.onCreate(raw, privateCheck.isSelected(), new ArrayList<>(selectedUsers));
            }
            dialog.dispose();
        });

        nameField.addActionListener(e -> createBtn.doClick());

        btns.add(cancelBtn);
        btns.add(createBtn);
        dialog.getContentPane().add(btns, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setMinimumSize(new Dimension(460, availableUsers.isEmpty() ? 280 : 520));
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    /** Construit une ligne utilisateur cliquable. */
    private JPanel buildUserRow(User u, boolean selected,
                                Set<User> selectedUsers,
                                JPanel chipsPanel,
                                Runnable[] rebuildRef,
                                JDialog dialog) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(selected ? new Color(88, 101, 242, 60) : BG_ITEM);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BG),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Avatar initiale
        String initial = (u.getName() != null && !u.getName().isEmpty())
                ? String.valueOf(u.getName().charAt(0)).toUpperCase() : "?";
        JLabel avatar = new JLabel(initial, SwingConstants.CENTER);
        avatar.setFont(new Font("Arial", Font.BOLD, 13));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(true);
        avatar.setBackground(ACCENT);
        avatar.setPreferredSize(new Dimension(30, 30));
        avatar.setBorder(BorderFactory.createEmptyBorder());

        // Infos
        JPanel info = new JPanel(new GridLayout(2, 1, 0, 1));
        info.setOpaque(false);
        JLabel nameLbl = new JLabel(u.getName() != null ? u.getName() : "(sans nom)");
        nameLbl.setFont(new Font("Arial", Font.BOLD, 13));
        nameLbl.setForeground(Color.WHITE);
        JLabel tagLbl = new JLabel("@" + u.getUserTag());
        tagLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        tagLbl.setForeground(MUTED);
        info.add(nameLbl);
        info.add(tagLbl);

        // Checkmark
        JLabel check = new JLabel(selected ? "✓" : "");
        check.setFont(new Font("Arial", Font.BOLD, 14));
        check.setForeground(ACCENT);
        check.setPreferredSize(new Dimension(20, 20));

        row.add(avatar, BorderLayout.WEST);
        row.add(info, BorderLayout.CENTER);
        row.add(check, BorderLayout.EAST);

        // Hover
        row.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (!selectedUsers.contains(u)) row.setBackground(new Color(64, 68, 75));
            }
            @Override public void mouseExited(MouseEvent e) {
                row.setBackground(selectedUsers.contains(u) ? new Color(88, 101, 242, 60) : BG_ITEM);
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (selectedUsers.contains(u)) selectedUsers.remove(u);
                else selectedUsers.add(u);
                refreshChipsPanel(chipsPanel, selectedUsers, rebuildRef);
                rebuildRef[0].run();
            }
        });

        return row;
    }

    /** Reconstruit les chips des utilisateurs sélectionnés. */
    private void refreshChipsPanel(JPanel chipsPanel, Set<User> selectedUsers, Runnable[] rebuildRef) {
        chipsPanel.removeAll();
        for (User u : selectedUsers) {
            JPanel chip = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            chip.setBackground(ACCENT);
            chip.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 6));

            JLabel chipName = new JLabel(u.getName() != null ? u.getName() : "@" + u.getUserTag());
            chipName.setForeground(Color.WHITE);
            chipName.setFont(new Font("Arial", Font.PLAIN, 12));

            JLabel x = new JLabel("✕");
            x.setForeground(Color.WHITE);
            x.setFont(new Font("Arial", Font.BOLD, 10));
            x.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            x.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    selectedUsers.remove(u);
                    refreshChipsPanel(chipsPanel, selectedUsers, rebuildRef);
                    rebuildRef[0].run();
                }
            });

            chip.add(chipName);
            chip.add(x);
            chipsPanel.add(chip);
        }
        chipsPanel.revalidate();
        chipsPanel.repaint();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // API gestion des canaux (inchangée)
    // ─────────────────────────────────────────────────────────────────────────

    public void addCanalUI(CanalView canalView, int row) {
        if (canalView == null) return;
        if (!SwingUtilities.isEventDispatchThread()) { SwingUtilities.invokeLater(() -> addCanalUI(canalView, row)); return; }
        canalsPanel.remove(glue);
        canalsPanel.add(canalView, canalConstraints(row));
        glue = Box.createVerticalGlue();
        canalsPanel.add(glue, glueConstraints(row + 1));
        canalsPanel.revalidate();
        canalsPanel.repaint();
        SwingUtilities.invokeLater(() -> { JScrollBar bar = scrollPane.getVerticalScrollBar(); if (bar != null) bar.setValue(bar.getMaximum()); });
        if (this.viewContext.logger() != null) this.viewContext.logger().debug("CanalView ajoutée (row=" + row + ")");
    }

    public void rebuildUI(List<CanalView> ordered) {
        if (!SwingUtilities.isEventDispatchThread()) { SwingUtilities.invokeLater(() -> rebuildUI(ordered)); return; }
        canalsPanel.removeAll();
        int row = 0;
        for (CanalView cv : ordered) canalsPanel.add(cv, canalConstraints(row++));
        glue = Box.createVerticalGlue();
        canalsPanel.add(glue, glueConstraints(row));
        canalsPanel.revalidate();
        canalsPanel.repaint();
        applyFilter(searchField != null ? searchField.getText() : null);
    }

    public void updateCanalUI(CanalView view, Channel channel) {
        if (!SwingUtilities.isEventDispatchThread()) { SwingUtilities.invokeLater(() -> updateCanalUI(view, channel)); return; }
        view.updateChannel(channel);
        canalsPanel.revalidate();
        canalsPanel.repaint();
        if (this.viewContext.logger() != null) this.viewContext.logger().debug("CanalView mise à jour pour: " + channel);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers internes
    // ─────────────────────────────────────────────────────────────────────────

    private JPanel createCanalsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);
        panel.setOpaque(true);
        return panel;
    }

    private JScrollPane createScrollPane(JPanel content) {
        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private void addScrollPaneToThis() {
        GridBagConstraints gbcSearch = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 6, 0), 0, 0);
        this.add(searchField, gbcSearch);
        GridBagConstraints gbc = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        this.add(scrollPane, gbc);
    }

    private GridBagConstraints canalConstraints(int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.weightx = 1.0; gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 4, 4, 4);
        return gbc;
    }

    private GridBagConstraints glueConstraints(int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH; gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        return gbc;
    }

    @Override
    protected void paintComponent(Graphics g) { super.paintComponent(g); }

    private JTextField createSearchField() {
        JTextField tf = new JTextField();
        tf.setColumns(15);
        tf.setOpaque(true);
        tf.setBackground(new Color(47, 49, 54));
        tf.setForeground(new Color(220, 221, 222));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 34, 37)),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        tf.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { applyFilter(tf.getText()); }
            @Override public void removeUpdate(DocumentEvent e)  { applyFilter(tf.getText()); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilter(tf.getText()); }
        });
        return tf;
    }

    private void applyFilter(String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase();
        if (!SwingUtilities.isEventDispatchThread()) { SwingUtilities.invokeLater(() -> applyFilter(query)); return; }
        for (Component c : canalsPanel.getComponents()) {
            if (c instanceof CanalView) c.setVisible(matches((CanalView) c, q));
        }
        canalsPanel.revalidate();
        canalsPanel.repaint();
    }

    private boolean matches(CanalView cv, String q) {
        if (q == null || q.isEmpty()) return true;
        Channel ch = cv.getChannel();
        if (ch == null) return false;
        return ch.getName() != null && ch.getName().toLowerCase().contains(q);
    }

    // ── Helpers visuels du dialog ──────────────────────────────────────────

    private JLabel miniLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(MUTED);
        l.setFont(new Font("Arial", Font.BOLD, 11));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField styledTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setBackground(BG_INPUT);
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 34, 37)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        return tf;
    }

    private JButton accentButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
