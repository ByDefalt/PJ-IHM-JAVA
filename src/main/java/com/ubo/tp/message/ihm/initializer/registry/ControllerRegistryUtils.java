package com.ubo.tp.message.ihm.initializer.registry;

import com.ubo.tp.message.controller.service.Controller;
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
     * Descriptor permettant d'enregistrer plusieurs controllers créés à partir
     * d'une même vue : identifiant, type et constructeur (qui reçoit la vue).
     */
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
    public static <C extends Controller>
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
            return controllerConstructor.apply(view, ctx);
        };
    }

}
