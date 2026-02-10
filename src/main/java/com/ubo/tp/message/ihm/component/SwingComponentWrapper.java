package com.ubo.tp.message.ihm.component;

import javax.swing.*;

/**
 * Petit wrapper permettant d'utiliser un JComponent pur (JPanel, etc.)
 * dans les APIs qui attendent des {@link Component} marqueurs.
 */
public class SwingComponentWrapper implements Component {
    private final JComponent component;

    public SwingComponentWrapper(JComponent component) {
        this.component = component;
    }

    public JComponent getComponent() {
        return component;
    }
}

