package com.ubo.tp.message.ihm.initializer.registry;

import com.ubo.tp.message.ihm.initializer.model.InitializationContext;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Registry pour enregistrer des créateurs de controllers par id.
 * Le créateur reçoit un {@link InitializationContext} et retourne un instance typée.
 */
public interface ControllerRegistry {
    /**
     * Enregistre un créateur typé pour l'id donné.
     */
    <T> void register(String id, Class<T> type, Function<InitializationContext, T> creator);

    boolean has(String id);

    /**
     * Crée (ou récupère) le controller associé à l'id et vérifie son type.
     */
    <T> T create(String id, InitializationContext context, Class<T> type);

    Set<String> getIds();

    void remove(String id);

    /**
     * Retourne le controller existant ou en crée un via le supplier et l'enregistre
     * (singleton) pour les appels suivants.
     */
    <T> T getOrCreate(String id, InitializationContext context, Class<T> type, Supplier<T> factory);
}
