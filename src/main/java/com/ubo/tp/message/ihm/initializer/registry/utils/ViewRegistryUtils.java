package com.ubo.tp.message.ihm.initializer.registry.utils;

import com.ubo.tp.message.ihm.initializer.model.InitializationContext;

import javax.swing.JComponent;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utilitaires pour construire des créateurs de vues réutilisables afin de
 * réduire le boilerplate lors de l'enregistrement dans le {@link com.ubo.tp.message.ihm.initializer.registry.ViewRegistry}.
 *
 * Exemple d'utilisation :
 * <pre>
 * viewRegistry.register("register", ViewRegistryUtils.createViewFromController(
 *   "registerController",
 *   IRegisterController.class,
 *   ctx -> new RegisterController(),
 *   ctx -> new RegisterComponent(ctx.getLogger()),
 *   (ctrl, comp, ctx) -> new RegisterView(ctrl, comp, ctx.getLogger())
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
     * puis construit le component et la view.
     *
     * @param controllerId id logique du controller à rechercher
     * @param controllerType type attendu du controller
     * @param controllerFallbackCreator supplier de repli pour le controller si non trouvé
     * @param componentCreator fonction de création du component UI
     * @param viewConstructor fonction qui assemble controller + component + context en Vue
     * @param <C> type du controller
     * @param <Comp> type du component (JComponent)
     * @param <V> type de la view (JComponent)
     * @return fonction prenant un {@link InitializationContext} et retournant un JComponent
     */
    public static <C, Comp extends JComponent, V extends JComponent>
    Function<InitializationContext, JComponent> createViewFromController(
            String controllerId,
            Class<C> controllerType,
            Function<InitializationContext, C> controllerFallbackCreator,
            Function<InitializationContext, Comp> componentCreator,
            TriFunction<C, Comp, InitializationContext, V> viewConstructor
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
            Comp comp = componentCreator.apply(ctx);
            V view = viewConstructor.apply(controller, comp, ctx);
            return (JComponent) view;
        };
    }
}
