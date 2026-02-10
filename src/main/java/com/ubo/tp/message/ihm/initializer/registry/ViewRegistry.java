package com.ubo.tp.message.ihm.initializer.registry;

import com.ubo.tp.message.ihm.initializer.model.InitializationContext;
import com.ubo.tp.message.ihm.screen.View;

import java.util.Set;
import java.util.function.Function;

/**
 * Registry pour l'enregistrement de créateurs de vues typées identifiées
 * par un id. Les créateurs reçoivent un {@link InitializationContext} et
 * retournent une {@link View} prête à être attachée au NavigationService.
 */
public interface ViewRegistry {

    /**
     * Enregistre un créateur de vue typée pour l'identifiant donné.
     *
     * @param id identifiant logique de la vue
     * @param type classe attendue de la vue
     * @param creator fonction prenant le {@link InitializationContext} et
     *                retournant une instance de la vue
     * @param <T> type de la vue (doit étendre {@link View})
     */
    <T extends View> void register(String id, Class<T> type, Function<InitializationContext, T> creator);

    /**
     * Compatibility overload: accepts creators returning any subtype of View.
     */
    @SuppressWarnings("unchecked")
    default void register(String id, Function<InitializationContext, ? extends View> creator) {
        if (id == null || creator == null) return;
        Function<InitializationContext, View> c = (Function<InitializationContext, View>) creator;
        register(id, View.class, c);
    }

    /**
     * Indique si un créateur pour l'id est présent.
     *
     * @param id identifiant testé
     * @return true si un créateur est enregistré
     */
    boolean has(String id);

    /**
     * Construit la vue associée à l'id en invoquant le créateur avec le
     * contexte fourni et vérifie le type attendu.
     *
     * @param id identifiant de la vue
     * @param context contexte d'initialisation
     * @param type type attendu
     * @param <T> type de la vue
     * @return vue typée ou null si aucun créateur n'existe
     * @throws ClassCastException si l'instance retournée n'est pas du type attendu
     */
    <T extends View> T create(String id, InitializationContext context, Class<T> type);

    /**
     * Convenience: create without specifying a class returns a {@link View}.
     */
    default View create(String id, InitializationContext context) {
        return create(id, context, View.class);
    }

    /**
     * Liste des identifiants enregistrés.
     *
     * @return set d'identifiants
     */
    Set<String> getIds();

    /**
     * Retire un créateur de la registry.
     *
     * @param id identifiant à retirer
     */
    void remove(String id);
}
