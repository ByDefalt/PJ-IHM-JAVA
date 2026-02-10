package com.ubo.tp.message.ihm.initializer.registry;

import com.ubo.tp.message.ihm.initializer.model.InitializationContext;

import javax.swing.JComponent;
import java.util.Set;
import java.util.function.Function;

/**
 * Registry pour l'enregistrement de créateurs de vues (JComponent) identifiés
 * par un id. Les créateurs reçoivent un {@link InitializationContext} et
 * retournent un composant Swing prêt à être attaché au NavigationService.
 */
public interface ViewRegistry {

    /**
     * Enregistre un créateur de vue pour l'identifiant donné.
     *
     * @param id identifiant logique de la vue
     * @param creator fonction prenant le {@link InitializationContext} et
     *                retournant un {@link JComponent}
     */
    void register(String id, Function<InitializationContext, JComponent> creator);

    /**
     * Indique si un créateur pour l'id est présent.
     *
     * @param id identifiant testé
     * @return true si un créateur est enregistré
     */
    boolean has(String id);

    /**
     * Construit la vue associée à l'id en invoquant le créateur avec le
     * contexte fourni.
     *
     * @param id identifiant de la vue
     * @param context contexte d'initialisation
     * @return composant JComponent ou null si aucun créateur n'existe
     */
    JComponent create(String id, InitializationContext context);

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
