package com.ubo.tp.message.ihm.initializer.module;

import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.ihm.initializer.registry.ControllerRegistry;
import com.ubo.tp.message.ihm.initializer.registry.ViewRegistry;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.navigation.NavigationService;

/**
 * Module d'initialisation UI : encapsule l'enregistrement d'un groupe de
 * vues (ex. auth) pour respecter OCP (ajout de nouveaux modules sans modifier
 * UIInitializer).
 */
public interface UIModule {
    void register(NavigationService navigation, DataManager dataManager, Logger logger, ControllerRegistry controllerRegistry, ViewRegistry viewRegistry);
}
