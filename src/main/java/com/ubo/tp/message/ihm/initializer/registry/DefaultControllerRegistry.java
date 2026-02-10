package com.ubo.tp.message.ihm.initializer.registry;

import com.ubo.tp.message.ihm.initializer.model.InitializationContext;
import com.ubo.tp.message.controller.service.Controller;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Implémentation par défaut de {@link ControllerRegistry}.
 * <p>
 * Utilise des maps thread-safe (ConcurrentHashMap) pour stocker les créateurs et
 * les singletons. Les méthodes sont sûres pour un usage concurrent typique.
 * </p>
 */
public class DefaultControllerRegistry implements ControllerRegistry {

    // stocke des créateurs hétérogènes ; le wildcard évite l'erreur de cast
    private final Map<String, Function<InitializationContext, ?>> creators = new ConcurrentHashMap<>();
    private final Map<String, Object> singletons = new ConcurrentHashMap<>();
    private final Map<String, Class<?>> types = new ConcurrentHashMap<>();

    @Override
    public <T extends Controller> void register(String id, Class<T> type, Function<InitializationContext, T> creator) {
        if (id == null || creator == null || type == null) return;
        creators.putIfAbsent(id, creator);
        types.putIfAbsent(id, type);
    }

    @Override
    public boolean has(String id) {
        return creators.containsKey(id) || singletons.containsKey(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Controller> T create(String id, InitializationContext context, Class<T> type) {
        // first, check singleton cache
        Object existing = singletons.get(id);
        if (existing != null) {
            if (type.isInstance(existing)) return (T) existing;
            throw new ClassCastException("Controller for id '" + id + "' is not of expected type " + type.getName());
        }

        Function<InitializationContext, ?> f = creators.get(id);
        if (f == null) return null;
        Object obj = f.apply(context);
        if (obj == null) return null;
        if (type.isInstance(obj)) return (T) obj;
        throw new ClassCastException("Controller registered with id '" + id + "' is not of expected type: " + type.getName());
    }

    @Override
    public Set<String> getIds() {
        return creators.keySet();
    }

    @Override
    public void remove(String id) {
        creators.remove(id);
        singletons.remove(id);
        types.remove(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Controller> T getOrCreate(String id, InitializationContext context, Class<T> type, Supplier<T> factory) {
        // If already exists as singleton return it
        Object instance = singletons.get(id);
        if (instance != null) {
            if (type.isInstance(instance)) return (T) instance;
            throw new ClassCastException("Existing singleton for id '" + id + "' is not of expected type: " + type.getName());
        }

        // computeIfAbsent to ensure thread-safety when creating singleton
        Object created = singletons.computeIfAbsent(id, k -> {
            // try to use registered creator first
            Function<InitializationContext, ?> f = creators.get(id);
            if (f != null) {
                Object c = f.apply(context);
                if (c != null) return c;
            }
            // fallback: use supplier factory
            return factory.get();
        });

        if (type.isInstance(created)) return (T) created;
        throw new ClassCastException("Created singleton for id '" + id + "' is not of expected type: " + type.getName());
    }
}
