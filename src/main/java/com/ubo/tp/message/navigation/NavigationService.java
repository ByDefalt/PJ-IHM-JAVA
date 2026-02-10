package com.ubo.tp.message.navigation;

import javax.swing.JComponent;
import java.awt.GridBagConstraints;

/**
 * Service de navigation applicatif, abstraction utilisée par les controllers
 * pour naviguer sans dépendre directement de la vue.
 */
public interface NavigationService {

    /**
     * Ajoute (enregistre) une vue identifiée par id.
     */
    void addView(String id, JComponent view);

    /**
     * Ajoute (enregistre) une vue identifiée par id en fournissant des
     * contraintes GridBag pour le wrapper qui contiendra la vue.
     */
    void addView(String id, JComponent view, GridBagConstraints gbc);

    /**
     * Affiche la vue identifiée par id.
     */
    void showView(String id);

    /**
     * Indique si la vue est déjà enregistrée.
     */
    boolean hasView(String id);

    /**
     * Retire la vue identifiée par id de la navigation (si elle existe).
     */
    void removeView(String id);
}
