package com.ubo.tp.message.navigation;

import javax.swing.JComponent;
import java.awt.GridBagConstraints;

/**
 * Service de navigation applicatif exposé aux controllers.
 * <p>
 * Permet d'enregistrer et d'afficher des vues sans exposer les détails
 * d'implémentation de la fenêtre principale. Les implémentations doivent veiller
 * à l'idempotence des enregistrements (plusieurs appels addView ne doivent pas
 * provoquer d'erreurs).
 * </p>
 */
public interface NavigationService {

    /**
     * Ajoute (enregistre) une vue identifiée par id.
     *
     * @param id identifiant logique de la vue
     * @param view composant Swing à afficher
     */
    void addView(String id, JComponent view);

    /**
     * Ajoute (enregistre) une vue identifiée par id en fournissant des
     * contraintes GridBag pour le wrapper qui contiendra la vue.
     *
     * @param id identifiant logique de la vue
     * @param view composant Swing à afficher
     * @param gbc contraintes GridBagConstraints utilisées pour placer la vue
     */
    void addView(String id, JComponent view, GridBagConstraints gbc);

    /**
     * Affiche la vue identifiée par id (si elle est enregistrée).
     *
     * @param id identifiant logique de la vue
     */
    void showView(String id);

    /**
     * Indique si la vue est déjà enregistrée.
     *
     * @param id identifiant à tester
     * @return true si la vue est connue
     */
    boolean hasView(String id);

    /**
     * Retire la vue identifiée par id de la navigation (si elle existe).
     *
     * @param id identifiant de la vue
     */
    void removeView(String id);
}
