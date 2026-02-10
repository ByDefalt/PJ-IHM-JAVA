package com.ubo.tp.message.ihm.initializer.model;

import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.ihm.initializer.UIInitializer;
import com.ubo.tp.message.ihm.initializer.registry.ControllerRegistry;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.navigation.NavigationService;

/**
 * Contexte utilisé lors de la création de controllers et de views par les
 * registries. Il regroupe les services nécessaires (DataManager, NavigationService,
 * Logger) ainsi que l'accès au {@link ControllerRegistry} qui permet de récupérer
 * ou créer d'autres controllers.
 * <p>
 * Lifecycle / portée :
 * - Une instance de InitializationContext est construite par {@link UIInitializer}
 *   au moment de l'initialisation des vues (méthode {@code initViews()}).
 * - Le contexte est passé aux créateurs enregistrés dans les registries (controllers
 *   et views) pendant cette phase d'initialisation. Il n'est pas garanti d'être valide
 *   en dehors de ce flux d'initialisation (c'est un objet à usage 'init').
 * - Si vous avez besoin d'accéder aux mêmes services plus tard (runtime), fournissez
 *   explicitement un mécanisme stable (ex : passer NavigationService au controller,
 *   ou utiliser un singleton / DI container approprié).
 */
public class InitializationContext {
    private final IDataManager dataManager;
    private final NavigationService navigation;
    private final Logger logger;
    private final ControllerRegistry controllerRegistry;

    public InitializationContext(IDataManager dataManager, NavigationService navigation, Logger logger, ControllerRegistry controllerRegistry) {
        this.dataManager = dataManager;
        this.navigation = navigation;
        this.logger = logger;
        this.controllerRegistry = controllerRegistry;
    }

    public IDataManager getDataManager() {
        return dataManager;
    }

    public NavigationService getNavigation() {
        return navigation;
    }

    public Logger getLogger() {
        return logger;
    }

    public ControllerRegistry getControllerRegistry() {
        return controllerRegistry;
    }
}
