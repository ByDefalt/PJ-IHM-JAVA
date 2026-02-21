package com.ubo.tp.message.theme;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;

/**
 * Thème Discord fidèle — basé sur FlatLaf Dark.
 * <p>
 * Palette officielle Discord (Dark Mode) :
 * #36393F  background primary     (panneaux centraux)
 * #2F3136  background secondary   (sidebars)
 * #202225  background tertiary    (barre de membres, header de groupe)
 * #18191C  background floating    (menus, tooltips, modals)
 * #40444B  channel text input
 * #DCDDDE  text normal
 * #B9BBBE  text muted
 * #FFFFFF  text heading
 * #5865F2  bleu accent / brand
 * #3BA55D  vert (online / success)
 * #ED4245  rouge (danger / déconnexion)
 */
public class DiscordTheme extends AbstractTheme {

    // ── Palette ─────────────────────────────────────────────────────────────

    private static final ColorUIResource BG_PRIMARY = c(0x36393F);
    private static final ColorUIResource BG_SECONDARY = c(0x2F3136);
    private static final ColorUIResource BG_TERTIARY = c(0x202225);
    private static final ColorUIResource BG_FLOATING = c(0x18191C);
    private static final ColorUIResource BG_INPUT = c(0x40444B);
    private static final ColorUIResource BG_MODIFIER = c(0x4F545C); // hover léger

    private static final ColorUIResource TEXT_NORMAL = c(0xDCDDDE);
    private static final ColorUIResource TEXT_MUTED = c(0x72767D);
    private static final ColorUIResource TEXT_HEADING = c(0xFFFFFF);

    private static final ColorUIResource ACCENT = c(0x5865F2); // bleu Discord
    private static final ColorUIResource ACCENT_HOVER = c(0x4752C4);
    private static final ColorUIResource SUCCESS = c(0x3BA55D);
    private static final ColorUIResource DANGER = c(0xED4245);
    private static final ColorUIResource SEPARATOR = c(0x42454B);

    private static final ColorUIResource SCROLLBAR = c(0x202225);
    private static final ColorUIResource SCROLLBAR_THUMB = c(0x1A1B1E);

    /**
     * Convertit un int RGB hexadécimal en ColorUIResource.
     */
    private static ColorUIResource c(int rgb) {
        return new ColorUIResource(new Color(rgb));
    }

    @Override
    public void apply() {

        UIManager.put("Component.arc", 8);
        UIManager.put("Button.arc", 4);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("TextArea.arc", 8);
        UIManager.put("Component.focusWidth", 2);
        UIManager.put("Component.focusColor", ACCENT);
        UIManager.put("Component.innerFocusWidth", 0);

        UIManager.put("ScrollBar.width", 8);
        UIManager.put("ScrollBar.thumbArc", 4);
        UIManager.put("ScrollBar.background", BG_SECONDARY);
        UIManager.put("ScrollBar.thumb", SCROLLBAR_THUMB);
        UIManager.put("ScrollBar.hoverThumbColor", BG_MODIFIER);
        UIManager.put("ScrollBar.trackColor", BG_SECONDARY);
        UIManager.put("ScrollBar.showButtons", false);

        UIManager.put("Panel.background", BG_PRIMARY);
        UIManager.put("RootPane.background", BG_TERTIARY);
        UIManager.put("Desktop.background", BG_TERTIARY);
        UIManager.put("control", BG_PRIMARY);

        UIManager.put("Label.foreground", TEXT_NORMAL);
        UIManager.put("Label.disabledForeground", TEXT_MUTED);

        UIManager.put("Button.background", BG_MODIFIER);
        UIManager.put("Button.foreground", TEXT_HEADING);
        UIManager.put("Button.hoverBackground", new ColorUIResource(0x5D6269));
        UIManager.put("Button.pressedBackground", new ColorUIResource(0x686D73));
        UIManager.put("Button.disabledBackground", BG_SECONDARY);
        UIManager.put("Button.disabledForeground", TEXT_MUTED);
        UIManager.put("Button.focusedBackground", BG_MODIFIER);
        UIManager.put("Button.borderColor", BG_MODIFIER);
        UIManager.put("Button.default.background", ACCENT);
        UIManager.put("Button.default.foreground", TEXT_HEADING);
        UIManager.put("Button.default.hoverBackground", ACCENT_HOVER);
        UIManager.put("Button.default.focusedBackground", ACCENT_HOVER);
        UIManager.put("Button.default.borderColor", ACCENT);

        UIManager.put("TextField.background", BG_INPUT);
        UIManager.put("TextField.foreground", TEXT_NORMAL);
        UIManager.put("TextField.caretForeground", TEXT_NORMAL);
        UIManager.put("TextField.selectionBackground", ACCENT);
        UIManager.put("TextField.selectionForeground", TEXT_HEADING);
        UIManager.put("TextField.placeholderForeground", TEXT_MUTED);
        UIManager.put("TextField.borderColor", BG_TERTIARY);
        UIManager.put("TextField.focusedBorderColor", ACCENT);

        UIManager.put("TextArea.background", BG_INPUT);
        UIManager.put("TextArea.foreground", TEXT_NORMAL);
        UIManager.put("TextArea.caretForeground", TEXT_NORMAL);
        UIManager.put("TextArea.selectionBackground", ACCENT);
        UIManager.put("TextArea.selectionForeground", TEXT_HEADING);

        UIManager.put("PasswordField.background", BG_INPUT);
        UIManager.put("PasswordField.foreground", TEXT_NORMAL);
        UIManager.put("PasswordField.caretForeground", TEXT_NORMAL);

        UIManager.put("ComboBox.background", BG_INPUT);
        UIManager.put("ComboBox.foreground", TEXT_NORMAL);
        UIManager.put("ComboBox.buttonBackground", BG_INPUT);
        UIManager.put("ComboBox.selectionBackground", ACCENT);
        UIManager.put("ComboBox.selectionForeground", TEXT_HEADING);
        UIManager.put("ComboBox.popupBackground", BG_FLOATING);
        UIManager.put("ComboBox.borderColor", BG_TERTIARY);

        UIManager.put("List.background", BG_PRIMARY);
        UIManager.put("List.foreground", TEXT_NORMAL);
        UIManager.put("List.selectionBackground", BG_MODIFIER);
        UIManager.put("List.selectionForeground", TEXT_HEADING);
        UIManager.put("List.hoverBackground", new ColorUIResource(0x42454B));

        UIManager.put("Table.background", BG_PRIMARY);
        UIManager.put("Table.foreground", TEXT_NORMAL);
        UIManager.put("Table.gridColor", SEPARATOR);
        UIManager.put("Table.selectionBackground", ACCENT);
        UIManager.put("Table.selectionForeground", TEXT_HEADING);
        UIManager.put("TableHeader.background", BG_SECONDARY);
        UIManager.put("TableHeader.foreground", TEXT_MUTED);

        UIManager.put("Tree.background", BG_SECONDARY);
        UIManager.put("Tree.foreground", TEXT_NORMAL);
        UIManager.put("Tree.selectionBackground", BG_MODIFIER);
        UIManager.put("Tree.selectionForeground", TEXT_HEADING);

        UIManager.put("ScrollPane.background", BG_PRIMARY);
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());

        UIManager.put("ToolTip.background", BG_FLOATING);
        UIManager.put("ToolTip.foreground", TEXT_HEADING);
        UIManager.put("ToolTip.border",
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BG_MODIFIER, 1),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        UIManager.put("MenuBar.background", BG_TERTIARY);
        UIManager.put("MenuBar.foreground", TEXT_NORMAL);
        UIManager.put("MenuBar.borderColor", SEPARATOR);
        UIManager.put("Menu.background", BG_TERTIARY);
        UIManager.put("Menu.foreground", TEXT_NORMAL);
        UIManager.put("Menu.selectionBackground", ACCENT);
        UIManager.put("Menu.selectionForeground", TEXT_HEADING);
        UIManager.put("PopupMenu.background", BG_FLOATING);
        UIManager.put("PopupMenu.foreground", TEXT_NORMAL);
        UIManager.put("PopupMenu.borderColor", BG_MODIFIER);
        UIManager.put("MenuItem.background", BG_FLOATING);
        UIManager.put("MenuItem.foreground", TEXT_NORMAL);
        UIManager.put("MenuItem.selectionBackground", ACCENT);
        UIManager.put("MenuItem.selectionForeground", TEXT_HEADING);

        UIManager.put("TabbedPane.background", BG_PRIMARY);
        UIManager.put("TabbedPane.foreground", TEXT_MUTED);
        UIManager.put("TabbedPane.selectedBackground", BG_PRIMARY);
        UIManager.put("TabbedPane.selectedForeground", TEXT_HEADING);
        UIManager.put("TabbedPane.underlineColor", ACCENT);
        UIManager.put("TabbedPane.hoverColor", BG_MODIFIER);

        UIManager.put("SplitPane.background", BG_TERTIARY);
        UIManager.put("SplitPaneDivider.draggingColor", ACCENT);
        UIManager.put("SplitPane.dividerSize", 4);

        UIManager.put("Separator.foreground", SEPARATOR);
        UIManager.put("Separator.background", SEPARATOR);

        UIManager.put("ProgressBar.background", BG_TERTIARY);
        UIManager.put("ProgressBar.foreground", ACCENT);

        UIManager.put("CheckBox.background", BG_PRIMARY);
        UIManager.put("CheckBox.foreground", TEXT_NORMAL);
        UIManager.put("CheckBox.icon.background", BG_INPUT);
        UIManager.put("CheckBox.icon.selectedColor", ACCENT);
        UIManager.put("RadioButton.background", BG_PRIMARY);
        UIManager.put("RadioButton.foreground", TEXT_NORMAL);

        UIManager.put("Spinner.background", BG_INPUT);
        UIManager.put("Spinner.foreground", TEXT_NORMAL);
        UIManager.put("Spinner.borderColor", BG_TERTIARY);

        UIManager.put("OptionPane.background", BG_SECONDARY);
        UIManager.put("OptionPane.foreground", TEXT_NORMAL);
        UIManager.put("OptionPane.messageForeground", TEXT_NORMAL);
        UIManager.put("Dialog.background", BG_SECONDARY);

        UIManager.put("TitlePane.background", BG_TERTIARY);
        UIManager.put("TitlePane.foreground", TEXT_HEADING);
        UIManager.put("TitlePane.buttonHoverBackground", BG_MODIFIER);
        UIManager.put("TitlePane.buttonPressedBackground", new ColorUIResource(0x686D73));

        applyFont("Segoe UI", 14);

        FlatDarkLaf.setup();

        for (Window w : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(w);
            w.pack();
            w.repaint();
        }
    }

    /**
     * Applique une police à tous les composants textuels courants.
     * Essaie d'abord {@code preferredFamily}, sinon retombe sur "SansSerif".
     */
    private void applyFont(String preferredFamily, int size) {
        String family = isFontAvailable(preferredFamily) ? preferredFamily : "SansSerif";
        FontUIResource regular = new FontUIResource(family, Font.PLAIN, size);
        FontUIResource bold = new FontUIResource(family, Font.BOLD, size);

        String[] regularKeys = {
                "Label.font", "Button.font", "TextField.font", "TextArea.font",
                "List.font", "ComboBox.font", "Table.font", "MenuItem.font",
                "Menu.font", "MenuBar.font", "ToolTip.font", "CheckBox.font",
                "RadioButton.font", "TabbedPane.font", "ProgressBar.font",
                "Spinner.font", "OptionPane.font", "PasswordField.font",
        };
        String[] boldKeys = {
                "TitledBorder.font", "TableHeader.font", "TitlePane.font"
        };

        for (String key : regularKeys) UIManager.put(key, regular);
        for (String key : boldKeys) UIManager.put(key, bold);
    }

    private boolean isFontAvailable(String name) {
        for (String f : GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames()) {
            if (f.equalsIgnoreCase(name)) return true;
        }
        return false;
    }
}