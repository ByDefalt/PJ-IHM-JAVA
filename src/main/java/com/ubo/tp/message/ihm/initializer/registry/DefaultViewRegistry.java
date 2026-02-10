package com.ubo.tp.message.ihm.initializer.registry;

import com.ubo.tp.message.ihm.initializer.model.InitializationContext;
import com.ubo.tp.message.ihm.screen.View;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Implémentation thread-safe de {@link ViewRegistry} utilisant une map
 * concurrente pour stocker les créateurs de vues.
 */
public class DefaultViewRegistry implements ViewRegistry {

    private final Map<String, Function<InitializationContext, View>> creators = new ConcurrentHashMap<>();

    /**
     * Enregistre un créateur de vue associé à un identifiant.
     *
     * @param id      L'identifiant de la vue.
     * @param creator La fonction créatrice de la vue.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <V extends View> void register(String id, Class<V> type, Function<InitializationContext, V> creator) {
        if (id == null || creator == null || type == null) return;
        creators.putIfAbsent(id, (Function<InitializationContext, View>) creator);
    }

    /**
     * Vérifie si un créateur de vue est enregistré pour un identifiant donné.
     *
     * @param id L'identifiant de la vue.
     * @return true si un créateur est enregistré, sinon false.
     */
    @Override
    public boolean has(String id) {
        return creators.containsKey(id);
    }

    /**
     * Crée une instance de vue en utilisant le créateur enregistré pour l'identifiant donné.
     *
     * @param id      L'identifiant de la vue.
     * @param context Le contexte d'initialisation.
     * @return La vue créée, ou null si aucun créateur n'est trouvé.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <V extends View> V create(String id, InitializationContext context, Class<V> type) {
        Function<InitializationContext, View> f = creators.get(id);
        if (f == null) return null;
        View view = f.apply(context);
        if (view == null) return null;
        if (type.isInstance(view)) return (V) view;
        throw new ClassCastException("View created for id '" + id + "' is not of expected type: " + type.getName());
    }

    /**
     * Retourne l'ensemble des identifiants de vue enregistrés.
     *
     * @return Un ensemble d'identifiants de vue.
     */
    @Override
    public Set<String> getIds() {
        return creators.keySet();
    }

    /**
     * Supprime le créateur de vue enregistré pour un identifiant donné.
     *
     * @param id L'identifiant de la vue à supprimer.
     */
    @Override
    public void remove(String id) {
        creators.remove(id);
    }
}
