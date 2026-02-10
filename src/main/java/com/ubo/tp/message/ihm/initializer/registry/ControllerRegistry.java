package com.ubo.tp.message.ihm.initializer.registry;

import com.ubo.tp.message.ihm.initializer.model.InitializationContext;
import com.ubo.tp.message.controller.service.Controller;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Registry pour enregistrer et fournir des instances de controllers par identifiant.
 * <p>
 * Un controller est enregistré via un créateur (Function) prenant un
 * {@link InitializationContext} et retournant une instance typée. Le
 * {@link ControllerRegistry} gère également un cache de singletons pour
 * réutiliser des instances créées précédemment.
 * </p>
 * <p>
 * Contrat important : les créateurs doivent être idempotents et ne pas
 * provoquer de cycles de dépendances non résolus lors de l'initialisation.
 * </p>
 */
public interface ControllerRegistry {
    /**
     * Enregistre un créateur typé pour l'id donné.
     *
     * @param id identifiant logique du controller (non-null)
     * @param type classe attendue du controller
     * @param creator fonction de création qui reçoit le {@link InitializationContext}
     *                et retourne une instance du controller
     * @param <T> type du controller (doit étendre {@link Controller})
     */
    <T extends Controller> void register(String id, Class<T> type, Function<InitializationContext, T> creator);

    /**
     * Indique si un identifiant est connu (créateur ou singleton présent).
     *
     * @param id identifiant à tester
     * @return true si le registry contient le créateur ou une instance
     */
    boolean has(String id);

    /**
     * Crée ou récupère le controller associé à l'id et vérifie son type.
     *
     * @param id identifiant du controller
     * @param context contexte d'initialisation passé au créateur
     * @param type type attendu en sortie
     * @param <T> type du controller (doit étendre {@link Controller})
     * @return instance du controller ou null si non trouvé
     * @throws ClassCastException si l'instance existante n'est pas du type attendu
     */
    <T extends Controller> T create(String id, InitializationContext context, Class<T> type);

    /**
     * Retourne l'ensemble des ids enregistrés (créateurs disponibles).
     *
     * @return set d'identifiants
     */
    Set<String> getIds();

    /**
     * Retire un enregistrement (créateur et singleton associé) du registry.
     *
     * @param id identifiant à retirer
     */
    void remove(String id);

    /**
     * Retourne le controller existant ou en crée un via le supplier et l'enregistre
     * (singleton) pour les appels suivants.
     *
     * @param id identifiant du controller
     * @param context contexte d'initialisation
     * @param type type attendu
     * @param factory supplier de repli utilisé si aucun créateur n'est présent
     * @param <T> type du controller (doit étendre {@link Controller})
     * @return instance créée ou existante
     */
    <T extends Controller> T getOrCreate(String id, InitializationContext context, Class<T> type, Supplier<T> factory);
}
