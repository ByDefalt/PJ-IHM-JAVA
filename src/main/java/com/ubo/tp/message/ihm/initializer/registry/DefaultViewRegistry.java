package com.ubo.tp.message.ihm.initializer.registry;

import com.ubo.tp.message.ihm.initializer.model.InitializationContext;

import javax.swing.JComponent;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Implémentation thread-safe de {@link ViewRegistry} utilisant une map
 * concurrente pour stocker les créateurs de vues.
 */
public class DefaultViewRegistry implements ViewRegistry {

    private final Map<String, Function<InitializationContext, JComponent>> creators = new ConcurrentHashMap<>();

    /**
     * Enregistre un créateur de vue associé à un identifiant.
     *
     * @param id      L'identifiant de la vue.
     * @param creator La fonction créatrice de la vue.
     */
    @Override
    public void register(String id, Function<InitializationContext, JComponent> creator) {
        if (id == null || creator == null) return;
        creators.putIfAbsent(id, creator);
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
     * @return La composante JComponent créée, ou null si aucun créateur n'est trouvé.
     */
    @Override
    public JComponent create(String id, InitializationContext context) {
        Function<InitializationContext, JComponent> f = creators.get(id);
        return f != null ? f.apply(context) : null;
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
