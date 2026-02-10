package com.ubo.tp.message.ihm.initializer.registry;

import com.ubo.tp.message.ihm.initializer.model.InitializationContext;

import javax.swing.JComponent;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Helpers pour construire des créateurs de controllers à partir de vues.
 * Utile lorsque la création du controller dépend d'une vue préalablement construite.
 */
public final class ControllerRegistryUtils {

    private ControllerRegistryUtils() {}

    /**
     * Retourne un créateur Function&lt;InitializationContext, C&gt; qui construit
     * d'abord la vue (via viewRegistry.create(viewId, ctx) ou viewFallbackCreator)
     * puis instancie le controller via controllerConstructor.
     *
     * @param viewId identifiant de la vue à récupérer
     * @param controllerType type attendu du controller
     * @param viewRegistry registry de vues
     * @param controllerConstructor fonction qui reçoit la vue et le context et retourne le controller
     * @param viewFallbackCreator créateur de repli pour la vue
     * @param <C> type du controller
     * @return fonction créatrice de controller
     */
    public static <C>
    Function<InitializationContext, C> createControllerFromView(
            String viewId,
            Class<C> controllerType,
            ViewRegistry viewRegistry,
            BiFunction<JComponent, InitializationContext, C> controllerConstructor,
            Function<InitializationContext, JComponent> viewFallbackCreator
    ) {
        Objects.requireNonNull(viewRegistry);
        Objects.requireNonNull(controllerConstructor);

        return ctx -> {
            if (ctx == null) return null;
            JComponent view = null;
            try {
                view = viewRegistry.create(viewId, ctx);
            } catch (Exception ignored) {
                // fallback below
            }
            if (view == null && viewFallbackCreator != null) {
                view = viewFallbackCreator.apply(ctx);
            }
            C controller = controllerConstructor.apply(view, ctx);
            return controller;
        };
    }

    /**
     * Register convenience : enregistre directement le créateur obtenu via
     * createControllerFromView dans le ControllerRegistry fourni.
     */
    public static <C>
    void registerFromView(
            com.ubo.tp.message.ihm.initializer.registry.ControllerRegistry controllerRegistry,
            String controllerId,
            Class<C> controllerType,
            ViewRegistry viewRegistry,
            BiFunction<JComponent, InitializationContext, C> controllerConstructor,
            Function<InitializationContext, JComponent> viewFallbackCreator
    ) {
        Objects.requireNonNull(controllerRegistry);
        Function<InitializationContext, C> creator = createControllerFromView(controllerId, controllerType, viewRegistry, controllerConstructor, viewFallbackCreator);
        controllerRegistry.register(controllerId, controllerType, creator);
    }
}
