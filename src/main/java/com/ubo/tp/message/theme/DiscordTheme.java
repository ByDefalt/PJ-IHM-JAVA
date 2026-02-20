package com.ubo.tp.message.theme;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.util.ColorFunctions;
import com.ubo.tp.message.common.Constants;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * Thème "Discord-like" basé sur FlatLaf Dark.
 *
 * Définit des UI defaults cohérents avec les vues du projet (ListUserView, ListCanalView,
 * ListMessageView, InputMessageView, UserView, MessageView, etc.).
 */
public class DiscordTheme extends AbstractTheme {

    @Override
    public void apply() {
        // Installer le LAF Flat Dark
        FlatDarkLaf.setup();

        // Couleurs principales (inspirées de Discord Dark)
        ColorUIResource background = new ColorUIResource(54, 57, 63); // panel background
        ColorUIResource sidebar = new ColorUIResource(47, 49, 54); // sidebar
        ColorUIResource panel = new ColorUIResource(64, 68, 75);
        ColorUIResource accent = new ColorUIResource(88, 101, 242); // bleu accent
        ColorUIResource muted = new ColorUIResource(100, 100, 100);
        ColorUIResource white = new ColorUIResource(220, 221, 222);

        // Formes et arrondis
        UIManager.put("Component.focusWidth", 2);
        UIManager.put("Component.focusColor", new ColorUIResource(120, 130, 220));
        UIManager.put("Button.arc", 10);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("TextArea.arc", 8);
        UIManager.put("ScrollBar.thumbArc", 8);
        UIManager.put("ScrollBar.width", 12);

        // Couleurs globales
        UIManager.put("Panel.background", background);
        UIManager.put("control", background);
        UIManager.put("Label.foreground", white);
        UIManager.put("List.background", background);
        UIManager.put("List.foreground", white);
        UIManager.put("Table.background", background);

        // Sidebar specifics
        UIManager.put("Sidebar.background", sidebar);
        UIManager.put("Sidebar.foreground", white);

        // Buttons
        UIManager.put("Button.background", panel);
        UIManager.put("Button.foreground", white);
        UIManager.put("Button.selectionBackground", accent);
        UIManager.put("Button.selectionForeground", white);

        // Tooltips
        UIManager.put("ToolTip.background", new ColorUIResource(60, 63, 65));
        UIManager.put("ToolTip.foreground", white);

        // Inputs
        UIManager.put("TextField.background", new ColorUIResource(60, 63, 65));
        UIManager.put("TextField.foreground", white);
        UIManager.put("TextArea.background", new ColorUIResource(60, 63, 65));
        UIManager.put("TextArea.foreground", white);

        // ScrollPane
        UIManager.put("ScrollPane.background", background);

        // Message/List item specifics: lighten or darken backgrounds
        ColorUIResource entryBg = new ColorUIResource(64, 68, 75);
        ColorUIResource entryHover = new ColorUIResource(72, 75, 82);
        UIManager.put("List.itemBackground", entryBg);
        UIManager.put("List.itemHoverBackground", entryHover);

        // Accent
        UIManager.put("Component.focusColor", accent);
        UIManager.put("nimbusOrange", accent);

        // Misc
        UIManager.put("Separator.foreground", new ColorUIResource(75, 78, 85));

        // Force refresh of current windows
        for (Window w : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(w);
            w.invalidate();
            w.validate();
            w.repaint();
        }
    }
}

