package com.ubo.tp.message.ihm.initializer.registry.utils;

import com.ubo.tp.message.controller.service.Controller;
import com.ubo.tp.message.ihm.component.Component;
import com.ubo.tp.message.ihm.initializer.model.InitializationContext;
import com.ubo.tp.message.ihm.screen.View;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Utilitaires pour construire des créateurs de vues réutilisables afin de réduire
 * le boilerplate lors de l'enregistrement dans le {@link com.ubo.tp.message.ihm.initializer.registry.ViewRegistry}.
 * Exemple d'utilisation :
 * <pre>
 * viewRegistry.register("register", ViewRegistryUtils.createViewFromController(
 *   "registerController",
 *   IRegisterController.class,
 *   ctx -> new RegisterController(),
 *   ctx -> Collections.singletonList(new RegisterComponent(ctx.getLogger())),
 *   (ctrl, comps, ctx) -> new RegisterView(ctrl, (RegisterComponent) comps.get(0), ctx.getLogger())
 * ));
 * </pre>
 */
public final class ViewRegistryUtils {

    private ViewRegistryUtils() {}

    @FunctionalInterface
    public interface TriFunction<A,B,C,R> {
        R apply(A a, B b, C c);
    }

    /**
     * Crée un créateur de vue qui résout d'abord un controller via le ControllerRegistry
     * puis construit la liste de components et la view.
     *
     * @param controllerId id logique du controller à rechercher
     * @param controllerType type attendu du controller
     * @param controllerFallbackCreator supplier de repli pour le controller si non trouvé
     * @param componentCreator fonction de création d'une liste de components UI
     * @param viewConstructor fonction qui assemble controller + liste de components + context en Vue
     * @param <C> type du controller
     * @param <Comp> type du component (implémentation de Component)
     * @param <V> type de la view (View)
     * @return fonction prenant un {@link InitializationContext} et retournant une View
     */
    public static <C extends Controller, Comp extends Component, V extends View>
    Function<InitializationContext, V> createViewFromController(
            String controllerId,
            Class<C> controllerType,
            Function<InitializationContext, C> controllerFallbackCreator,
            Function<InitializationContext, List<Comp>> componentCreator,
            TriFunction<C, List<Comp>, InitializationContext, V> viewConstructor
    ) {
        return ctx -> {
            if (ctx == null) return null;
            com.ubo.tp.message.ihm.initializer.registry.ControllerRegistry registry = ctx.getControllerRegistry();
            C controller = null;
            try {
                controller = registry.create(controllerId, ctx, controllerType);
            } catch (ClassCastException ignored) {
                // will fallback
            }
            if (controller == null) {
                controller = controllerFallbackCreator != null ? controllerFallbackCreator.apply(ctx) : null;
            }
            List<Comp> comps = componentCreator.apply(ctx);
            return viewConstructor.apply(controller, comps, ctx);
        };
    }

    /**
     * Helper pour la situation fréquente "une vue = un seul composant" :
     * accepte une factory de component unique et la convertit en List interne.
     * Permet d'éviter d'écrire Collections.singletonList(...) partout.
     */
    public static <C extends Controller, Comp extends Component, V extends View>
    Function<InitializationContext, V> createViewFromControllerSingle(
            String controllerId,
            Class<C> controllerType,
            Function<InitializationContext, C> controllerFallbackCreator,
            Function<InitializationContext, Comp> singleComponentCreator,
            TriFunction<C, Comp, InitializationContext, V> singleViewConstructor
    ) {
        // wrap single component into a list and adapt the viewConstructor
        return createViewFromController(
                controllerId,
                controllerType,
                controllerFallbackCreator,
                ctx -> Collections.singletonList(singleComponentCreator.apply(ctx)),
                (ctrl, comps, ctx) -> singleViewConstructor.apply(ctrl, comps.getFirst(), ctx)
        );
    }
}
