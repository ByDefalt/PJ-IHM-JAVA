package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.utils.EmojiBinders;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Composant représentant un seul message (bulle) — style simple inspiré de Discord.
 */
public class MessageView extends JComponent implements View {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH'h'mm")
            .withLocale(Locale.FRANCE);

    private static final Color BG_NORMAL = new Color(54, 57, 63);
    private static final Color BG_HOVER = new Color(72, 76, 84);
    private static final Color BORDER_HOVER = new Color(90, 95, 105);

    private final ViewContext viewContext;
    private final Message message;

    private JLabel authorLabel;
    private javax.swing.JComponent contentPane;
    private JLabel timeLabel;
    private boolean hovered = false;
    private final boolean canDelete;

    public MessageView(ViewContext viewContext, Message message) {
        this(viewContext, message, null, false);
    }

    public MessageView(ViewContext viewContext, Message message,
                       Consumer<Message> onDelete, boolean canDelete) {
        this.viewContext  = viewContext;
        this.message      = message;
        this.canDelete    = canDelete;
        this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        this.setLayout(new GridBagLayout());
        this.setOpaque(false);

        init(message, onDelete);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                // Indication visuelle possible quand suppression autorisée (aucune icône affichée)
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                setCursor(Cursor.getDefaultCursor());
                repaint();
            }
        });

        if (this.viewContext.logger() != null)
            this.viewContext.logger().debug("MessageView initialisée pour '" + message.getSender() + "'");
    }

    private void init(Message message, Consumer<Message> onDelete) {
        String authorName = "";
        if (message.getSender() != null) {
            String tag = message.getSender().getUserTag() != null ? message.getSender().getUserTag() : "";
            String name = message.getSender().getName() != null ? message.getSender().getName() : "";
            authorName = "@" + tag + " - " + name;
        }
        createBubble(authorName, message.getText(), message.getEmissionDate());
        if (canDelete && onDelete != null) createDeletePopup(onDelete);
    }

    private void createDeletePopup(Consumer<Message> onDelete) {
        // Pas de stockage additionnel nécessaire : onDelete est capturé par le listener
        // Clic droit (popup trigger) sur le composant : ouvrir un petit menu avec l'action de suppression
        MouseAdapter popupListener = new MouseAdapter() {
            private void showPopupIfRequested(MouseEvent e) {
                if (!canDelete || onDelete == null) return;
                if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                    e.consume();
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem deleteItem = new JMenuItem("Supprimer le message");
                    deleteItem.addActionListener(ev -> onDelete.accept(message));
                    popup.add(deleteItem);
                    popup.show(MessageView.this, e.getX(), e.getY());
                }
            }

            @Override public void mousePressed(MouseEvent e) { showPopupIfRequested(e); }
            @Override public void mouseReleased(MouseEvent e) { showPopupIfRequested(e); }
        };
        this.addMouseListener(popupListener);
    }

    private void createBubble(String author, String content, long emissionMillis) {
        JPanel bubble = new JPanel(new GridBagLayout()) {
            @Override
            public boolean contains(int x, int y) {
                return false;
            }
        };
        bubble.setOpaque(false);
        bubble.setBorder(new EmptyBorder(6, 8, 6, 8));

        GridBagConstraints gbcBubble = new GridBagConstraints(
                1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 0, 2, 2), 0, 0
        );
        this.add(bubble, gbcBubble);

        createHeader(bubble, author, emissionMillis);
        createContent(bubble, content);
    }

    private void createHeader(JPanel bubble, String author, long emissionMillis) {
        Color authorColor = UIManager.getColor("Label.foreground");
        if (authorColor == null) authorColor = Color.WHITE;

        Color timeColor = UIManager.getColor("Label.disabledForeground");
        if (timeColor == null) timeColor = new Color(114, 118, 125);

        Font baseFont = UIManager.getFont("Label.font");
        Font authorFont = (baseFont != null) ? baseFont.deriveFont(Font.BOLD, 13f)
                : new Font("SansSerif", Font.BOLD, 13);
        Font timeFont = (baseFont != null) ? baseFont.deriveFont(Font.PLAIN, 11f)
                : new Font("SansSerif", Font.PLAIN, 11);

        authorLabel = new JLabel(author != null ? author : "") {
            @Override
            public boolean contains(int x, int y) {
                return false;
            }
        };
        authorLabel.setForeground(authorColor);
        authorLabel.setFont(authorFont);
        authorLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        authorLabel.setOpaque(false);

        timeLabel = new JLabel(formatTimestamp(emissionMillis)) {
            @Override
            public boolean contains(int x, int y) {
                return false;
            }
        };
        timeLabel.setForeground(timeColor);
        timeLabel.setFont(timeFont);
        timeLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        timeLabel.setBorder(new EmptyBorder(0, 8, 0, 0));
        timeLabel.setOpaque(false);

        JPanel headerPanel = new JPanel() {
            @Override
            public boolean contains(int x, int y) {
                return false;
            }
        };
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.add(authorLabel);
        headerPanel.add(timeLabel);

        GridBagConstraints gbcHeader = new GridBagConstraints(
                0, 0, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 4, 0), 0, 0
        );
        bubble.add(headerPanel, gbcHeader);
    }

    private void createContent(JPanel bubble, String content) {
        Color contentColor = UIManager.getColor("TextArea.foreground");
        if (contentColor == null) contentColor = UIManager.getColor("Label.foreground");
        if (contentColor == null) contentColor = new Color(220, 221, 222);

        Font baseFont = UIManager.getFont("TextArea.font");
        Font contentFont = (baseFont != null) ? baseFont.deriveFont(Font.PLAIN, 13f)
                : new Font("SansSerif", Font.PLAIN, 13);

        contentPane = createContentComponent(content != null ? content : "", contentFont, contentColor);

        GridBagConstraints gbcContent = new GridBagConstraints(
                0, 1, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0
        );
        bubble.add(contentPane, gbcContent);
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String toHtml(String text, Font font, Color fg) {
        String color = String.format("#%02x%02x%02x", fg.getRed(), fg.getGreen(), fg.getBlue());
        String fontFamily = font.getFamily();
        int fontSize = font.getSize();
        // Détecter si le texte contient uniquement des codes emoji (:smile: :heart: ...)
        String raw = text == null ? "" : text;
        boolean onlyEmojiCodes = raw.trim().matches("(?:(?::\\w+:)\\s*)+");
        java.util.regex.Matcher cm = java.util.regex.Pattern.compile("(:\\w+:)").matcher(raw);
        int codeCount = 0;
        while (cm.find()) codeCount++;

        // Taille des images emoji : inline petit (16) lorsqu'il y a du texte, plus grand si message uniquement emoji
        int imgSize = 16;
        if (onlyEmojiCodes) {
            imgSize = (codeCount == 1) ? 48 : 32;
            // adapter la taille de la police du body si message uniquement emoji
            fontSize = imgSize;
        }

        String esc = escapeHtml(raw);
        String highlighted = esc.replaceAll("(@\\w+)", "<span style=\"color: #5865F2;\">$1</span>");
        // Remplacer les codes emoji par des images si possible (ressource locale ou Twemoji CDN)
        for (String code : EmojiBinders.getSupportedCodes()) {
            if (highlighted.contains(code)) {
                String url = EmojiBinders.getEmojiImageUrl(code);
                if (url != null) {
                    String img;
                    if (onlyEmojiCodes) {
                        // pixel size for messages that are only emojis (big) + spacing
                        // include width/height attributes (pixels) so JEditorPane respects sizing
                        img = "<img src=\"" + url + "\" width=\"" + imgSize + "\" height=\"" + imgSize + "\" style=\"display:inline-block;margin:0 6px;vertical-align:middle;\"/>";
                    } else {
                        // relative size for mixed content: scale to text height (fontSize px) for pleasant inline rendering
                        // use fontSize (px) to ensure JEditorPane scales image to match text height
                        img = "<img src=\"" + url + "\" width=\"" + fontSize + "\" height=\"" + fontSize + "\" style=\"display:inline-block;vertical-align:-0.15em;\"/>";
                    }
                    highlighted = highlighted.replace(code, img);
                } else {
                    // fallback : remplacer par le caractère unicode
                    String uni = EmojiBinders.replaceEmojiCodesUnicode(code);
                    highlighted = highlighted.replace(code, uni);
                }
            }
        }
        String bodyHtml;
        if (onlyEmojiCodes) {
            // Laisser les images grandes mais alignées à gauche
            bodyHtml = "<div style=\"text-align:left;line-height:1;\">" + highlighted + "</div>";
        } else {
            // Mixed content: images already use inline-block + relative height
            bodyHtml = highlighted;
        }
        return "<html><body style=\"font-family: '" + fontFamily + "'; font-size: " + fontSize + "px; color: " + color + "; background-color: transparent;\">" + bodyHtml + "</body></html>";
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 12;
        int pad = 2;
        int w = getWidth() - pad * 2;
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

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        if (message.getSender() != null) {
            String tag = message.getSender().getUserTag() != null ? message.getSender().getUserTag() : "";
            String name = message.getSender().getName() != null ? message.getSender().getName() : "";
            authorLabel.setText(tag + " - " + name);
        } else {
            authorLabel.setText("");
        }
        // mettre à jour le contenu HTML en reformatant les mentions
        Font baseFont = UIManager.getFont("TextArea.font");
        Font contentFont = (baseFont != null) ? baseFont.deriveFont(Font.PLAIN, 13f) : new Font("SansSerif", Font.PLAIN, 13);
        Color contentColor = UIManager.getColor("TextArea.foreground");
        if (contentColor == null) contentColor = UIManager.getColor("Label.foreground");
        if (contentColor == null) contentColor = new Color(220, 221, 222);
        // Update the content component (may replace JTextPane <-> JTextArea depending on content)
        updateContentComponent(message.getText() != null ? message.getText() : "", contentFont, contentColor);
        timeLabel.setText(formatTimestamp(message.getEmissionDate()));
        revalidate();
        repaint();
    }

    public void updateMessage(Message message) {
        this.setMessage(message);
    }

    // Render text into JTextPane's StyledDocument. Inserts scaled ImageIcons for emoji codes.
    private void renderTextToPane(javax.swing.JTextPane pane, String text, Font font, Color fg) {
        try {
            javax.swing.text.StyledDocument doc = pane.getStyledDocument();
            doc.remove(0, doc.getLength());

            String raw = text == null ? "" : text;
            boolean onlyEmojiCodes = raw.trim().matches("(?:(?::\\w+:)\\s*)+");
            java.util.regex.Pattern codePat = java.util.regex.Pattern.compile("(:\\w+:)");
            java.util.regex.Matcher cm = codePat.matcher(raw);
            int codeCount = 0;
            while (cm.find()) codeCount++;

            int imgSize = 16;
            if (onlyEmojiCodes) imgSize = (codeCount == 1) ? 48 : 32;

            // Attributes for text
            javax.swing.text.SimpleAttributeSet attr = new javax.swing.text.SimpleAttributeSet();
            javax.swing.text.StyleConstants.setFontFamily(attr, font.getFamily());
            javax.swing.text.StyleConstants.setFontSize(attr, font.getSize());
            javax.swing.text.StyleConstants.setForeground(attr, fg);

            // Toujours aligner à gauche (le style des emoji seule reste grand mais aligné à gauche)
            javax.swing.text.SimpleAttributeSet paragraph = new javax.swing.text.SimpleAttributeSet();
            javax.swing.text.StyleConstants.setAlignment(paragraph, javax.swing.text.StyleConstants.ALIGN_LEFT);

            // Si message uniquement emoji, insérer un caractère invisible pour forcer l'alignement gauche
            if (onlyEmojiCodes) {
                doc.insertString(doc.getLength(), "\u200B", attr);
            }

            int last = 0;
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("(:\\w+:)|(@\\w+)");
            java.util.regex.Matcher m = p.matcher(raw);
            while (m.find()) {
                if (m.start() > last) {
                    String part = raw.substring(last, m.start());
                    part = insertZWSEveryN(part, 40);
                    doc.insertString(doc.getLength(), part, attr);
                }
                String emojiCode = m.group(1);
                String mention = m.group(2);
                if (emojiCode != null) {
                    String url = EmojiBinders.getEmojiImageUrl(emojiCode);
                    if (url != null) {
                        try {
                            java.net.URL u = new java.net.URL(url);
                            java.awt.Image img = javax.imageio.ImageIO.read(u);
                            if (img != null) {
                                java.awt.Image scaled = img.getScaledInstance(imgSize, imgSize, java.awt.Image.SCALE_SMOOTH);
                                javax.swing.ImageIcon icon = new javax.swing.ImageIcon(scaled);
                                // insert icon
                                pane.setCaretPosition(doc.getLength());
                                pane.insertIcon(icon);
                            } else {
                                // fallback unicode
                                String uni = EmojiBinders.replaceEmojiCodesUnicode(emojiCode);
                                uni = insertZWSEveryN(uni, 40);
                                doc.insertString(doc.getLength(), uni, attr);
                            }
                        } catch (Exception ex) {
                            String uni = EmojiBinders.replaceEmojiCodesUnicode(emojiCode);
                            uni = insertZWSEveryN(uni, 40);
                            doc.insertString(doc.getLength(), uni, attr);
                        }
                    } else {
                        String uni = EmojiBinders.replaceEmojiCodesUnicode(emojiCode);
                        uni = insertZWSEveryN(uni, 40);
                        doc.insertString(doc.getLength(), uni, attr);
                    }
                } else if (mention != null) {
                    javax.swing.text.SimpleAttributeSet mAttr = new javax.swing.text.SimpleAttributeSet();
                    javax.swing.text.StyleConstants.setBold(mAttr, true);
                    javax.swing.text.StyleConstants.setForeground(mAttr, Color.decode("#5865F2"));
                    doc.insertString(doc.getLength(), mention, mAttr);
                }
                last = m.end();
            }
            if (last < raw.length()) {
                String part = raw.substring(last);
                part = insertZWSEveryN(part, 40);
                doc.insertString(doc.getLength(), part, attr);
            }

            // Apply paragraph alignment
            doc.setParagraphAttributes(0, doc.getLength(), paragraph, false);
            pane.setCaretPosition(0);
        } catch (Exception ex) {
            // fallback to simple HTML rendering if anything fails
            pane.setContentType("text/html");
            pane.setText(toHtml(text, font, fg));
        }
    }

    // Insert zero-width space \u200B every n characters in long sequences without whitespace to enable wrapping
    private static String insertZWSEveryN(String s, int n) {
        if (s == null || s.length() <= n) return s;
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            sb.append(c);
            count++;
            if (Character.isWhitespace(c)) {
                count = 0;
            } else if (count >= n) {
                sb.append('\u200B');
                count = 0;
            }
        }
        return sb.toString();
    }

    private String formatTimestamp(long millis) {
        try {
            return DATE_FORMATTER.format(
                    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()));
        } catch (Exception e) {
            if (this.viewContext.logger() != null)
                this.viewContext.logger().debug("Erreur formatage date: " + e.getMessage());
            return String.valueOf(millis);
        }
    }

    private javax.swing.JComponent createContentComponent(String text, Font font, Color fg) {
        boolean hasEmojiCode = text != null && text.matches(".*(:\\w+:).*");
        boolean hasMention = text != null && text.matches(".*@\\w+.*");
        if (!hasEmojiCode && !hasMention && text != null && text.length() > 120) {
            // Plain long text -> JTextArea for reliable wrapping, wrapped in a JPanel so it expands
            javax.swing.JTextArea ta = new javax.swing.JTextArea();
            ta.setOpaque(false);
            ta.setEditable(false);
            ta.setLineWrap(true);
            ta.setWrapStyleWord(true);
            ta.setBorder(null);
            ta.setFocusable(false);
            ta.setFont(font);
            ta.setForeground(fg);
            // replace emoji codes by unicode just in case
            String display = EmojiBinders.replaceEmojiCodesUnicode(text);
            ta.setText(display);
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);
            wrapper.add(ta, BorderLayout.CENTER);
            return wrapper;
        } else {
             javax.swing.JTextPane tp = new javax.swing.JTextPane();
             tp.setEditable(false);
             tp.setOpaque(false);
             tp.setBorder(null);
             tp.setFocusable(false);
             renderTextToPane(tp, text, font, fg);
             return tp;
         }
     }

    private void updateContentComponent(String text, Font font, Color fg) {
        try {
            boolean needPlain = !(text != null && text.matches(".*(:\\w+:).*"))
                    && !(text != null && text.matches(".*@\\w+.*"))
                    && (text != null && text.length() > 120);

            if (needPlain && !(contentPane instanceof javax.swing.JTextArea)) {
                // remplacer par JTextArea
                Container parent = contentPane.getParent();
                if (parent == null) return;
                GridBagLayout layout = (GridBagLayout) parent.getLayout();
                GridBagConstraints gbc = new GridBagConstraints(
                        0, 1, 2, 1, 1.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0), 0, 0
                );
                parent.remove(contentPane);
                contentPane = createContentComponent(text, font, fg);
                parent.add(contentPane, gbc);
                parent.revalidate(); parent.repaint();
            } else if (!needPlain && contentPane instanceof javax.swing.JTextArea) {
                // replace JTextArea by JTextPane
                Container parent = contentPane.getParent();
                if (parent == null) return;
                GridBagConstraints gbc = new GridBagConstraints(
                        0, 1, 2, 1, 1.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0), 0, 0
                );
                parent.remove(contentPane);
                contentPane = createContentComponent(text, font, fg);
                parent.add(contentPane, gbc);
                parent.revalidate(); parent.repaint();
            } else {
                // same type -> update in place
                if (contentPane instanceof javax.swing.JTextArea) {
                    javax.swing.JTextArea ta = (javax.swing.JTextArea) contentPane;
                    ta.setText(EmojiBinders.replaceEmojiCodesUnicode(text));
                } else if (contentPane instanceof javax.swing.JPanel) {
                    // wrapper with JTextArea inside
                    java.awt.Component c = ((javax.swing.JPanel) contentPane).getComponent(0);
                    if (c instanceof javax.swing.JTextArea) {
                        ((javax.swing.JTextArea) c).setText(EmojiBinders.replaceEmojiCodesUnicode(text));
                    }
                } else if (contentPane instanceof javax.swing.JTextPane) {
                    renderTextToPane((javax.swing.JTextPane) contentPane, text, font, fg);
                }
            }
        } catch (Exception e) {
             // fallback : set HTML on original component if possible
             if (contentPane instanceof javax.swing.JTextPane) {
                 ((javax.swing.JTextPane) contentPane).setContentType("text/html");
                 ((javax.swing.JTextPane) contentPane).setText(toHtml(text, font, fg));
             }
         }
     }
}
