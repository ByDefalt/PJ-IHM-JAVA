package com.ubo.tp.message.ihm.initializer.registry.utils;

import com.ubo.tp.message.ihm.initializer.model.InitializationContext;
import com.ubo.tp.message.ihm.initializer.registry.ControllerRegistry;
import com.ubo.tp.message.ihm.initializer.registry.ViewRegistry;

import javax.swing.JComponent;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Helpers to build controller creators from views to avoid boilerplate when the
 * controller needs the view to be created first (controller owns/uses the view).
 */
public final class ControllerRegistryUtils {

    private ControllerRegistryUtils() {}

    /**
     * Returns a creator Function<InitializationContext, C> which will build a view
     * (using viewRegistry.create(viewId, ctx) or viewFallbackCreator) and then
     * construct the controller using the provided controllerConstructor.
     *
     * controllerConstructor receives the created view and the InitializationContext.
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
            // It's possible view is still null; controllerConstructor should handle null view accordingly
            C controller = controllerConstructor.apply(view, ctx);
            return controller;
        };
    }

    /**
     * Convenience helper: register a controller factory into the given controllerRegistry
     * that is created from a view (viewId). This wraps createControllerFromView and
     * calls controllerRegistry.register(...).
     */
    public static <C>
    void registerFromView(
            ControllerRegistry controllerRegistry,
            String controllerId,
            Class<C> controllerType,
            ViewRegistry viewRegistry,
            BiFunction<JComponent, InitializationContext, C> controllerConstructor,
            Function<InitializationContext, JComponent> viewFallbackCreator
    ) {
        Objects.requireNonNull(controllerRegistry);
        Function<InitializationContext, C> creator = createControllerFromView(viewIdFrom(controllerId), controllerType, viewRegistry, controllerConstructor, viewFallbackCreator);
        controllerRegistry.register(controllerId, controllerType, creator);
    }

    // helper in case we want to conventionally derive a view id from controller id; for now just return controllerId but kept for extension
    private static String viewIdFrom(String controllerId) {
        return controllerId; // default behaviour: same id; caller may pass explicit viewId separately if needed
    }
}
