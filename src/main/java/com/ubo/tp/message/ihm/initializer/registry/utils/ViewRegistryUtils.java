package com.ubo.tp.message.ihm.initializer.registry.utils;

import com.ubo.tp.message.ihm.initializer.model.InitializationContext;
import com.ubo.tp.message.ihm.initializer.registry.ControllerRegistry;

import javax.swing.JComponent;
import java.util.function.Function;

/**
 * Helpers to build view creators for the ViewRegistry to avoid repeating boilerplate.
 */
public final class ViewRegistryUtils {

    private ViewRegistryUtils() {}

    @FunctionalInterface
    public interface TriFunction<A,B,C,R> {
        R apply(A a, B b, C c);
    }

    /**
     * Create a view creator that resolves a controller from the ControllerRegistry
     * and builds the component and view using the provided factories.
     *
     * Example usage:
     * viewRegistry.register("register", ViewRegistryUtils.createViewFromController(
     *     "registerController",
     *     IRegisterController.class,
     *     ctx -> new RegisterController(),
     *     ctx -> new RegisterComponent(ctx.getLogger()),
     *     (ctrl, comp, ctx) -> new RegisterView(ctrl, comp, ctx.getLogger())
     * ));
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
            ControllerRegistry registry = ctx.getControllerRegistry();
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
