package com.ubo.tp.message.ihm.initializer.module;

import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.ihm.initializer.registry.ControllerRegistry;
import com.ubo.tp.message.ihm.initializer.registry.ViewRegistry;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.navigation.NavigationService;

import javax.swing.JComponent;
import javax.swing.JLabel;
import java.awt.BorderLayout;

/**
 * Exemple de module "settings" pour démontrer l'enregistrement via ViewRegistry.
 */
public class SettingsModule implements UIModule {
    @Override
    public void register(NavigationService navigation, IDataManager dataManager, Logger logger, ControllerRegistry controllerRegistry, ViewRegistry viewRegistry) {
        // On enregistre un créateur qui construit une vue simple "settings".
        viewRegistry.register("settings", ctx -> {
            javax.swing.JPanel panel = new javax.swing.JPanel(new BorderLayout());
            panel.add(new JLabel("Settings (placeholder)"), BorderLayout.CENTER);
            return (JComponent) panel;
        });
    }
}
