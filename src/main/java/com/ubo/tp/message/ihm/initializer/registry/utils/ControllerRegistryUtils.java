package com.ubo.tp.message.ihm.initializer.registry.utils;

import com.ubo.tp.message.ihm.initializer.model.InitializationContext;
import com.ubo.tp.message.ihm.initializer.registry.ControllerRegistry;
import com.ubo.tp.message.ihm.initializer.registry.ViewRegistry;

import javax.swing.JComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Helpers to build controller creators from views to avoid boilerplate when the
 * controller needs the view to be created first (controller owns/uses the view).
 */
public final class ControllerRegistryUtils {

    private ControllerRegistryUtils() {}

    // descriptor for registering multiple controllers from a single view
    public static final class ControllerDescriptor {
        private final String controllerId;
        private final Class<?> controllerType;
        private final BiFunction<JComponent, InitializationContext, ?> constructor;

        public ControllerDescriptor(String controllerId, Class<?> controllerType, BiFunction<JComponent, InitializationContext, ?> constructor) {
            this.controllerId = Objects.requireNonNull(controllerId);
            this.controllerType = Objects.requireNonNull(controllerType);
            this.constructor = Objects.requireNonNull(constructor);
        }

        public String getControllerId() { return controllerId; }
        public Class<?> getControllerType() { return controllerType; }
        public BiFunction<JComponent, InitializationContext, ?> getConstructor() { return constructor; }
    }

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
     * Variant that allows creating a controller from multiple views (list of view ids).
     * The controllerConstructor receives a List<JComponent> (same order as viewIds).
     * If all created views are null and viewFallbackCreator is provided, the fallback
     * is used (should return a list of the same size or at least usable by constructor).
     */
    public static <C>
    Function<InitializationContext, C> createControllerFromViews(
            List<String> viewIds,
            Class<C> controllerType,
            ViewRegistry viewRegistry,
            BiFunction<List<JComponent>, InitializationContext, C> controllerConstructor,
            Function<InitializationContext, List<JComponent>> viewFallbackCreator
    ) {
        Objects.requireNonNull(viewRegistry);
        Objects.requireNonNull(controllerConstructor);
        Objects.requireNonNull(viewIds);

        return ctx -> {
            if (ctx == null) return null;

            List<JComponent> views = new ArrayList<>(viewIds.size());
            boolean anyNonNull = false;
            for (String vid : viewIds) {
                JComponent v = null;
                try {
                    v = viewRegistry.create(vid, ctx);
                } catch (Exception ignored) {
                    // ignore, fallback below
                }
                if (v != null) anyNonNull = true;
                views.add(v);
            }

            if (!anyNonNull && viewFallbackCreator != null) {
                List<JComponent> fallback = viewFallbackCreator.apply(ctx);
                if (fallback != null) views = fallback;
            }

            return controllerConstructor.apply(views, ctx);
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

    /**
     * Convenience helper: register a controller factory into the given controllerRegistry
     * that is created from multiple views (viewIds). This wraps createControllerFromViews
     * and registers the resulting factory.
     */
    public static <C>
    void registerFromViews(
            ControllerRegistry controllerRegistry,
            String controllerId,
            Class<C> controllerType,
            ViewRegistry viewRegistry,
            List<String> viewIds,
            BiFunction<List<JComponent>, InitializationContext, C> controllerConstructor,
            Function<InitializationContext, List<JComponent>> viewFallbackCreator
    ) {
        Objects.requireNonNull(controllerRegistry);
        Function<InitializationContext, C> creator = createControllerFromViews(viewIds, controllerType, viewRegistry, controllerConstructor, viewFallbackCreator);
        controllerRegistry.register(controllerId, controllerType, creator);
    }

    /**
     * Register multiple controllers that are created from the same viewId.
     * Each descriptor contains the controllerId, type and a constructor that receives
     * the created view (JComponent) and the InitializationContext.
     */
    public static void registerMultipleFromView(
            ControllerRegistry controllerRegistry,
            String viewId,
            ViewRegistry viewRegistry,
            List<ControllerDescriptor> descriptors,
            Function<InitializationContext, JComponent> viewFallbackCreator
    ) {
        Objects.requireNonNull(controllerRegistry);
        Objects.requireNonNull(viewRegistry);
        Objects.requireNonNull(descriptors);

        for (ControllerDescriptor d : descriptors) {
            // create a creator function for each descriptor
            Function<InitializationContext, Object> creator = ctx -> {
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
                return d.getConstructor().apply(view, ctx);
            };
            // register into the controller registry using raw types to avoid complex generics
            controllerRegistry.register(d.getControllerId(), (Class) d.getControllerType(), (Function) creator);
        }
    }

    // helper in case we want to conventionally derive a view id from controller id; for now just return controllerId but kept for extension
    private static String viewIdFrom(String controllerId) {
        return controllerId; // default behaviour: same id; caller may pass explicit viewId separately if needed
    }
}
