package com.ubo.tp.message.ihm.initializer.module;

import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.ihm.initializer.registry.ControllerRegistry;
import com.ubo.tp.message.ihm.initializer.registry.ViewRegistry;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.navigation.NavigationService;

/**
 * Module d'initialisation UI : encapsule l'enregistrement d'un groupe de
 * vues et controllers (ex. auth). Permet l'extension de l'interface sans
 * modification de {@link com.ubo.tp.message.ihm.initializer.UIInitializer}.
 */
public interface UIModule {

    /**
     * Enregistre dans les registries les créateurs de controllers et de vues
     * nécessaires à ce module.
     *
     * @param navigation service de navigation utilisé pour attacher les vues
     * @param dataManager service d'accès aux données (abstraction {@link IDataManager})
     * @param logger logger de l'application
     * @param controllerRegistry registry de controllers
     * @param viewRegistry registry de vues
     */
    void register(NavigationService navigation, IDataManager dataManager, Logger logger, ControllerRegistry controllerRegistry, ViewRegistry viewRegistry);
}
