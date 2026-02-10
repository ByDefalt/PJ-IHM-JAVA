package com.ubo.tp.message.ihm.initializer;

import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.ihm.initializer.model.InitializationContext;
import com.ubo.tp.message.ihm.initializer.module.UIModule;
import com.ubo.tp.message.ihm.initializer.registry.ControllerRegistry;
import com.ubo.tp.message.ihm.initializer.registry.DefaultControllerRegistry;
import com.ubo.tp.message.ihm.initializer.registry.DefaultViewRegistry;
import com.ubo.tp.message.ihm.initializer.registry.ViewRegistry;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.navigation.NavigationService;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe responsable de l'initialisation des vues de l'interface (enregistrement
 * des cartes auprès du NavigationService). Permet de sortir cette logique de
 * la classe MessageApp pour centraliser la configuration UI.
 *
 * Lifecycle & recommandations :
 * - L'initialisation des vues se fait via la méthode {@link #initViews()} qui :
 *   1) construit un {@link InitializationContext} (contenant DataManager,
 *      NavigationService, Logger et ControllerRegistry),
 *   2) appelle {@link UIModule#register(...)} pour que chaque module enregistre
 *      ses créateurs de controllers et views dans les registries,
 *   3) invoque ensuite les créateurs de vues pour obtenir les composants et les
 *      enregistre auprès du {@link NavigationService}.
 * - Le {@link InitializationContext} est conçu pour ce flux d'initialisation
 *   et n'est pas garanti d'être valide en dehors de l'appel à {@link #initViews()}.
 * - Pour éviter les cycles de dépendances, les créateurs doivent rester idempotents
 *   et éviter d'enregistrer d'autres créateurs dépendants réciproquement.
 */
public class UIInitializer {

    private final NavigationService navigation;
    private final IDataManager dataManager;
    private final Logger logger;
    private final List<UIModule> modules = new ArrayList<>();
    private final ViewRegistry viewRegistry = new DefaultViewRegistry();
    private final ControllerRegistry controllerRegistry = new DefaultControllerRegistry();

    public UIInitializer(NavigationService navigation, IDataManager dataManager, Logger logger) {
        this.navigation = navigation;
        this.dataManager = dataManager;
        this.logger = logger;
    }

    /**
     * Enregistre un module UI à initialiser ultérieurement.
     */
    public void register(UIModule module) {
        if (module == null) return;
        this.modules.add(module);
    }

    /**
     * Enregistre plusieurs modules en une fois.
     */
    public void registerAll(UIModule... modules) {
        if (modules == null || modules.length == 0) return;
        this.modules.addAll(Arrays.asList(modules));
    }

    public ControllerRegistry getControllerRegistry() { return controllerRegistry; }
    public ViewRegistry getViewRegistry() { return viewRegistry; }
    public IDataManager getDataManager() { return dataManager; }

    /**
     * Supprime une vue enregistrée : la retire du NavigationService et du ViewRegistry.
     * Retourne true si la vue a été trouvée et retirée.
     */
    public boolean removeView(String id) {
        boolean removed = false;
        try {
            // remove from navigation if present
            if (id != null && navigation.hasView(id)) {
                navigation.removeView(id);
                removed = true;
            }
        } catch (Exception e) {
            if (logger != null) logger.warn("UIInitializer: failed to remove view from navigation: " + e.getMessage());
        }

        try {
            // remove the creator from the registry as well
            if (id != null && viewRegistry.has(id)) {
                viewRegistry.remove(id);
                removed = true;
            }
        } catch (Exception e) {
            if (logger != null) logger.warn("UIInitializer: failed to remove view from registry: " + e.getMessage());
        }

        return removed;
    }

    /**
     * Enregistre les modules (appelle leur méthode register).
     *
     * Séparation de responsabilité : cette méthode ne crée pas de vues, elle
     * permet seulement aux modules d'enregistrer des créateurs dans les
     * registries (ControllerRegistry, ViewRegistry). Appel idempotent si les
     * créateurs utilisent putIfAbsent.
     */
    public void registerModules() {
        if (logger != null) logger.debug("UIInitializer: registerModules start");
        if (!modules.isEmpty()) {
            for (UIModule m : modules) {
                try {
                    m.register(navigation, dataManager, logger, controllerRegistry, viewRegistry);
                } catch (Exception e) {
                    if (logger != null) logger.warn("UIInitializer: module registration failed: " + e.getMessage());
                }
            }
        }
        if (logger != null) logger.debug("UIInitializer: registerModules done");
    }

    /**
     * Construit les vues à partir du ViewRegistry et les attache au NavigationService.
     * Cette méthode crée le {@link InitializationContext} puis invoque tous les
     * créateurs de vues enregistrés.
     */
    public void buildAndAttachViews() {
        if (logger != null) logger.debug("UIInitializer: buildAndAttachViews start");

        InitializationContext ctx = new InitializationContext(dataManager, navigation, logger, controllerRegistry);

        for (String id : viewRegistry.getIds()) {
            try {
                JComponent comp = viewRegistry.create(id, ctx);
                if (comp != null && !navigation.hasView(id)) {
                    navigation.addView(id, comp);
                }
            } catch (Exception e) {
                if (logger != null) logger.warn("UIInitializer: failed to create view '" + id + "': " + e.getMessage());
            }
        }

        if (logger != null) logger.debug("UIInitializer: buildAndAttachViews done");
    }

    /**
     * Orchestrateur de haut niveau : enregistre les modules puis crée et attache
     * les vues. Méthode de convenance qui préserve l'API existante.
     */
    public void initViews() {
        if (logger != null) logger.debug("UIInitializer: initViews start");
        registerModules();
        buildAndAttachViews();

        // Afficher la vue de login par défaut s'il existe
        if (navigation.hasView("login")) {
            navigation.showView("login");
        }

        if (logger != null) logger.debug("UIInitializer: initViews done");
    }
}
